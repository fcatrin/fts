#include <stdio.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <libs/nanovg/nanovg.h>
#include <libs/nanovg/nanovg_gl.h>
#include <libs/nanovg/nanovg_gl_utils.h>
#include "utils.h"

static struct NVGcontext* vg;

static int r, g, b, a;
static int r_start, g_start, b_start, a_start;
static int r_end, g_end, b_end, a_end;

#define MAX_FB_SIZE 200
NVGLUframebuffer *fbs[MAX_FB_SIZE];

void graphics_init() {
	vg = nvgCreateGLES2(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
	if (!vg) {
		fts_gl_log_error("Could not init nanovg ");
	}
}

void graphics_frame_begin(int width, int height) {
	glViewport(0, 0, width, height);
	nvgBeginFrame(vg, width, height, 1.0f);
}

void graphics_frame_end() {
	nvgEndFrame(vg);
}

void graphics_set_color(int red, int green, int blue, int alpha) {
	r = red;
	g = green;
	b = blue;
	a = alpha;
}

void graphics_set_color_start(int red, int green, int blue, int alpha) {
	r_start = red;
	g_start = green;
	b_start = blue;
	a_start = alpha;
}

void graphics_set_color_end(int red, int green, int blue, int alpha) {
	r_end = red;
	g_end = green;
	b_end = blue;
	a_end = alpha;
}

void graphics_draw_filled_rect(int x, int y, int width, int height, int radius) {
	nvgBeginPath(vg);
	nvgRoundedRect(vg, x, y, width, height, radius);
	nvgFillColor(vg, nvgRGBA(r, g, b, a));
	nvgFill(vg);
}

void graphics_draw_rect(int x, int y, int width, int height, int radius) {
	nvgBeginPath(vg);
	nvgRoundedRect(vg, x, y, width, height, radius);
	nvgStrokeColor(vg, nvgRGBA(r, g, b, a));
	nvgStroke(vg);
}

void graphics_draw_gradient_rect(int x, int y, int width, int height, int radius, int angle) {

	float radius_w = width  / 2;
	float radius_h = height / 2;

	float radians = angle / 180.0 * NVG_PI;
	float cosw = cosf(radians) * radius_w;
	float sinh = sinf(radians) * radius_h;
	float cx = x + radius_w;
	float cy = y + radius_h;

	float gx2 = cx + cosw;
	float gy2 = cy + sinh;
	float gx1 = cx - cosw;
	float gy1 = cy - sinh;

	NVGpaint paint =
			nvgLinearGradient(vg, gx1, gy1, gx2, gy2,
					nvgRGBA(r_start, g_start, b_start, a_start),
					nvgRGBA(r_end, g_end, b_end,a_end));


	nvgBeginPath(vg);
	nvgRoundedRect(vg, x, y, width, height, radius);
	nvgFillPaint(vg, paint);
	nvgFill(vg);
}

void graphics_draw_line(int x, int y, int dx, int dy) {
	nvgBeginPath(vg);
	nvgMoveTo(vg, x, y);
	nvgLineTo(vg, dx, dy);
	nvgStrokeColor(vg, nvgRGBA(r, g, b, a));
	nvgStroke(vg);
}

bool graphics_create_font(const char *alias, const char *path) {
	int result = nvgCreateFont(vg, alias, path);
	fts_gl_log_debug("create font %s %s result:%s", alias, path, result >= 0 ? "true":"false");

	return result >= 0;
}

void graphics_set_font_size(int size) {
	nvgFontSize(vg, size);
}

void graphics_set_font_name(const char *name) {
	nvgFontFace(vg, name);
}

int* graphics_get_text_size(const char *text) {
	static int size[4];

	float bounds[4];
	nvgTextBounds(vg, 0, 0, text, NULL, bounds);

	fts_gl_log_debug("get text size for %s: %f,%f %f,%f", text, bounds[0], bounds[1], bounds[2], bounds[3]);

	size[0] = bounds[2] - bounds[0];
	size[1] = bounds[3] - bounds[1];
	size[2] = -bounds[1];
	size[3] = bounds[3];
	return size;
}

void graphics_draw_text(int x, int y, const char *text) {
	nvgFillColor(vg, nvgRGBA(r, g, b, a));
	nvgText(vg, x, y, text, NULL);
}

void graphics_view_start(int x, int y, int width, int height) {
	nvgSave(vg);
	nvgScissor(vg, x, y, width, height);
}

void graphics_view_end() {
	nvgRestore(vg);
}

void graphics_done() {
}

int graphics_backbuffer_create(int width, int height) {
	NVGLUframebuffer* fb = nvgluCreateFramebuffer(vg, width, height, 0);
	if (fb == NULL) {
		fts_gl_log_error("cannot create backbuffer of size %dx%d", width, height);
		return 0;
	}

	for(int i=0; i< MAX_FB_SIZE; i++) {
		if (fbs[i] == NULL) {
			fbs[i] = fb;
			return i+1;
		}
	}

	nvgluDeleteFramebuffer(fb);
	fts_gl_log_error("cannot allocate handle for backbuffer of size %dx%d", width, height);
	return 0;
}

void graphics_backbuffer_destroy(int handle) {
	handle--;
	if (handle < 0 || handle >= MAX_FB_SIZE) return;

	nvgluDeleteFramebuffer(fbs[handle]);
	fbs[handle] = NULL;
}

void graphics_backbuffer_bind(int handle) {
	handle--;
	if (handle < 0 || handle >= MAX_FB_SIZE) return;

	int fboWidth, fboHeight;
	NVGLUframebuffer* fb = fbs[handle];
	nvgImageSize(vg, fb->image, &fboWidth, &fboHeight);
	nvgluBindFramebuffer(fb);
	glViewport(0, 0, fboWidth, fboHeight);
	glClearColor(0, 0, 0, 0);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	nvgBeginFrame(vg, fboWidth, fboHeight, 1.0f);
	nvgSave(vg);
}

void graphics_backbuffer_unbind(int handle) {
	nvgRestore(vg);
	nvgEndFrame(vg);
	nvgluBindFramebuffer(NULL);
}

void graphics_backbuffer_draw(int handle, int x, int y, int width, int height) {
	handle--;
	if (handle < 0 || handle >= MAX_FB_SIZE) return;

	NVGLUframebuffer* fb = fbs[handle];
	NVGpaint img = nvgImagePattern(vg, x, y, width, height, 0, fb->image, 1.0f);

	nvgSave(vg);
	nvgBeginPath(vg);
	nvgRect(vg, x, y, width, height);
	nvgFillPaint(vg, img);
	nvgFill(vg);
	nvgRestore(vg);
}
