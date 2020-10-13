#include <xf86drm.h>
#include <xf86drmMode.h>
#include <gbm.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include "utils.h"

/*
 * original code from https://github.com/matusnovak/rpi-opengl-without-x
 */

static int device;
static drmModeModeInfo mode;
static struct gbm_device *gbmDevice;
static struct gbm_surface *gbmSurface;
static drmModeCrtc *crtc;
static uint32_t connectorId;

static EGLDisplay display;
static EGLSurface surface;
static EGLContext context;

static const EGLint configAttribs[] = {
    EGL_RED_SIZE, 8,
    EGL_GREEN_SIZE, 8,
    EGL_BLUE_SIZE, 8,
    EGL_DEPTH_SIZE, 8,
    EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
    EGL_NONE};

static const EGLint contextAttribs[] = {
    EGL_CONTEXT_CLIENT_VERSION, 2,
    EGL_NONE};

static drmModeConnector *getConnector(drmModeRes *resources) {
    for (int i = 0; i < resources->count_connectors; i++) {
        drmModeConnector *connector = drmModeGetConnector(device, resources->connectors[i]);
        if (connector->connection == DRM_MODE_CONNECTED) {
            return connector;
        }
        drmModeFreeConnector(connector);
    }
    return NULL;
}

static drmModeEncoder *findEncoder(drmModeConnector *connector) {
    if (connector->encoder_id) {
        return drmModeGetEncoder(device, connector->encoder_id);
    }
    return NULL;
}

static int getDisplay(EGLDisplay *display) {
    drmModeRes *resources = drmModeGetResources(device);
    if (resources == NULL) {
        fprintf(stderr, "Unable to get DRM resources\n");
        return -1;
    }

    drmModeConnector *connector = getConnector(resources);
    if (connector == NULL) {
        fprintf(stderr, "Unable to get connector\n");
        drmModeFreeResources(resources);
        return -1;
    }

    connectorId = connector->connector_id;
    mode = connector->modes[1];
    printf("resolution: %ix%i\n", mode.hdisplay, mode.vdisplay);

    drmModeEncoder *encoder = findEncoder(connector);
    if (encoder == NULL) {
        fprintf(stderr, "Unable to get encoder\n");
        drmModeFreeConnector(connector);
        drmModeFreeResources(resources);
        return -1;
    }

    crtc = drmModeGetCrtc(device, encoder->crtc_id);
    drmModeFreeEncoder(encoder);
    drmModeFreeConnector(connector);
    drmModeFreeResources(resources);
    gbmDevice = gbm_create_device(device);
    gbmSurface = gbm_surface_create(gbmDevice, mode.hdisplay, mode.vdisplay, GBM_FORMAT_XRGB8888, GBM_BO_USE_SCANOUT | GBM_BO_USE_RENDERING);
    *display = eglGetDisplay(gbmDevice);
    return 0;
}

static int matchConfigToVisual(EGLDisplay display, EGLint visualId, EGLConfig *configs, int count) {
    EGLint id;
    for (int i = 0; i < count; ++i) {
        if (!eglGetConfigAttrib(display, configs[i], EGL_NATIVE_VISUAL_ID, &id))
            continue;
        if (id == visualId)
            return i;
    }
    return -1;
}

static struct gbm_bo *previousBo = NULL;
static uint32_t previousFb;

static void gbmSwapBuffers(EGLDisplay *display, EGLSurface *surface) {
    eglSwapBuffers(*display, *surface);
    struct gbm_bo *bo = gbm_surface_lock_front_buffer(gbmSurface);
    uint32_t handle = gbm_bo_get_handle(bo).u32;
    uint32_t pitch = gbm_bo_get_stride(bo);
    uint32_t fb;
    drmModeAddFB(device, mode.hdisplay, mode.vdisplay, 24, 32, pitch, handle, &fb);
    drmModeSetCrtc(device, crtc->crtc_id, fb, 0, 0, &connectorId, 1, &mode);

    if (previousBo) {
        drmModeRmFB(device, previousFb);
        gbm_surface_release_buffer(gbmSurface, previousBo);
    }
    previousBo = bo;
    previousFb = fb;
}

static void gbmClean() {
    // set the previous crtc
    drmModeSetCrtc(device, crtc->crtc_id, crtc->buffer_id, crtc->x, crtc->y, &connectorId, 1, &crtc->mode);
    drmModeFreeCrtc(crtc);

    if (previousBo) {
        drmModeRmFB(device, previousFb);
        gbm_surface_release_buffer(gbmSurface, previousBo);
    }

    gbm_surface_destroy(gbmSurface);
    gbm_device_destroy(gbmDevice);
}

static const char *eglGetErrorStr() {
    switch (eglGetError()) {
    case EGL_SUCCESS:
        return "The last function succeeded without error.";
    case EGL_NOT_INITIALIZED:
        return "EGL is not initialized, or could not be initialized, for the "
               "specified EGL display connection.";
    case EGL_BAD_ACCESS:
        return "EGL cannot access a requested resource (for example a context "
               "is bound in another thread).";
    case EGL_BAD_ALLOC:
        return "EGL failed to allocate resources for the requested operation.";
    case EGL_BAD_ATTRIBUTE:
        return "An unrecognized attribute or attribute value was passed in the "
               "attribute list.";
    case EGL_BAD_CONTEXT:
        return "An EGLContext argument does not name a valid EGL rendering "
               "context.";
    case EGL_BAD_CONFIG:
        return "An EGLConfig argument does not name a valid EGL frame buffer "
               "configuration.";
    case EGL_BAD_CURRENT_SURFACE:
        return "The current surface of the calling thread is a window, pixel "
               "buffer or pixmap that is no longer valid.";
    case EGL_BAD_DISPLAY:
        return "An EGLDisplay argument does not name a valid EGL display "
               "connection.";
    case EGL_BAD_SURFACE:
        return "An EGLSurface argument does not name a valid surface (window, "
               "pixel buffer or pixmap) configured for GL rendering.";
    case EGL_BAD_MATCH:
        return "Arguments are inconsistent (for example, a valid context "
               "requires buffers not supplied by a valid surface).";
    case EGL_BAD_PARAMETER:
        return "One or more argument values are invalid.";
    case EGL_BAD_NATIVE_PIXMAP:
        return "A NativePixmapType argument does not refer to a valid native "
               "pixmap.";
    case EGL_BAD_NATIVE_WINDOW:
        return "A NativeWindowType argument does not refer to a valid native "
               "window.";
    case EGL_CONTEXT_LOST:
        return "A power management event has occurred. The application must "
               "destroy all contexts and reinitialise OpenGL ES state and "
               "objects to continue rendering.";
    default:
        break;
    }
    return "Unknown error!";
}

int rpi4fb_init(int width, int height) {
	// You can try changing this to "card0" if "card1" does not work.
	device = open("/dev/dri/card1", O_RDWR | O_CLOEXEC);
	if (getDisplay(&display) != 0) {
		fprintf(stderr, "Unable to get EGL display\n");
		close(device);
		return -1;
	}

	// We will use the screen resolution as the desired width and height for the viewport.
	int desiredWidth = mode.hdisplay;
	int desiredHeight = mode.vdisplay;

	// Other variables we will need further down the code.
	int major, minor;

	if (eglInitialize(display, &major, &minor) == EGL_FALSE) {
		fprintf(stderr, "Failed to get EGL version! Error: %s\n",
				eglGetErrorStr());
		eglTerminate(display);
		gbmClean();
		return EXIT_FAILURE;
	}

	// Make sure that we can use OpenGL in this EGL app.
	eglBindAPI(EGL_OPENGL_API);

	printf("Initialized EGL version: %d.%d\n", major, minor);

	EGLint count;
	EGLint numConfigs;
	eglGetConfigs(display, NULL, 0, &count);
	EGLConfig *configs = malloc(count * sizeof(configs));

	if (!eglChooseConfig(display, configAttribs, configs, count, &numConfigs))	{
		fprintf(stderr, "Failed to get EGL configs! Error: %s\n", eglGetErrorStr());
		eglTerminate(display);
		gbmClean();
		return EXIT_FAILURE;
	}

	// I am not exactly sure why the EGL config must match the GBM format.
	// But it works!
	int configIndex = matchConfigToVisual(display, GBM_FORMAT_XRGB8888, configs, numConfigs);
	if (configIndex < 0) {
		fprintf(stderr, "Failed to find matching EGL config! Error: %s\n", eglGetErrorStr());
		eglTerminate(display);
		gbm_surface_destroy(gbmSurface);
		gbm_device_destroy(gbmDevice);
		return EXIT_FAILURE;
	}

	context = eglCreateContext(display, configs[configIndex], EGL_NO_CONTEXT, contextAttribs);
	if (context == EGL_NO_CONTEXT) {
		fprintf(stderr, "Failed to create EGL context! Error: %s\n", eglGetErrorStr());
		eglTerminate(display);
		gbmClean();
		return EXIT_FAILURE;
	}

	surface = eglCreateWindowSurface(display, configs[configIndex], gbmSurface, NULL);
	if (surface == EGL_NO_SURFACE) {
		fprintf(stderr, "Failed to create EGL surface! Error: %s\n", eglGetErrorStr());
		eglDestroyContext(display, context);
		eglTerminate(display);
		gbmClean();
		return EXIT_FAILURE;
	}

	free(configs);
	eglMakeCurrent(display, surface, surface, context);

	// Set GL Viewport size, always needed!
	glViewport(0, 0, desiredWidth, desiredHeight);

	// Get GL Viewport size and test if it is correct.
	GLint viewport[4];
	glGetIntegerv(GL_VIEWPORT, viewport);

	// viewport[2] and viewport[3] are viewport width and height respectively
	printf("GL Viewport size: %dx%d\n", viewport[2], viewport[3]);

	if (viewport[2] != desiredWidth || viewport[3] != desiredHeight) {
		fprintf(stderr, "Error! The glViewport returned incorrect values! Something is wrong!\n");
		eglDestroyContext(display, context);
		eglDestroySurface(display, surface);
		eglTerminate(display);
		gbmClean();
		return EXIT_FAILURE;
	}
	log_debug("init done\n");
	return EXIT_SUCCESS;
}

void rpi4fb_swap_buffers() {
	gbmSwapBuffers(&display, &surface);
}

void rpi4fb_done() {
	eglDestroyContext(display, context);
	eglDestroySurface(display, surface);
	eglTerminate(display);
	gbmClean();

	close(device);
}
