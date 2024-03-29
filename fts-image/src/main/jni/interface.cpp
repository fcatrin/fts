#ifdef __cplusplus
extern "C" {
#endif

#include <string.h>
#include <wand/magick_wand.h>
#include "utils.h"
#include "fts_image_NativeInterface.h"

#define MAX_HANDLES 128
MagickWand *handles[MAX_HANDLES];

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_init
(JNIEnv *env, jclass clazz) {
	MagickWandGenesis();
	memset(handles, 0, sizeof(handles));
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_done
(JNIEnv *env, jclass clazz) {
	MagickWandTerminus();
}

static int findFreeHandle() {
	for(int i=0; i<MAX_HANDLES; i++) {
		if (!handles[i]) return i;
	}
	fts_log_fatal("No free image handles");
	return -1;
}

static MagickWand *getMagickWand(int handle) {
	if (handle < 0 || handle >= MAX_HANDLES) {
		fts_log_fatal("Invalid image handle %d", handle);
	}
	MagickWand *m_wand = handles[handle];
	if (!m_wand) {
		fts_log_fatal("Unknown handle %d", handle);
	}
	return m_wand;
}

JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_create
		(JNIEnv * env, jclass clazz) {
	int handle = findFreeHandle();
	handles[handle] = NewMagickWand();
	return handle;
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_destroy
(JNIEnv *env, jclass clazz, jint handle) {
	MagickWand *m_wand = getMagickWand(handle);
	DestroyMagickWand(m_wand);
	handles[handle] = NULL;
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_readImage
(JNIEnv *env, jclass clazz, jint handle, jstring jPath) {
	MagickWand *m_wand = getMagickWand(handle);

	const char* path = env->GetStringUTFChars(jPath,0);
	MagickReadImage(m_wand, path);
	env->ReleaseStringUTFChars(jPath, path);
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_readImageData
(JNIEnv *env, jclass clazz, jint handle, jbyteArray jData) {
	MagickWand *m_wand = getMagickWand(handle);

	jsize  size = env->GetArrayLength(jData);
	jbyte* data = env->GetByteArrayElements(jData, NULL);
	MagickReadImageBlob(m_wand, data, size);

	env->ReleaseByteArrayElements(jData, data, 0);
}


JNIEXPORT void JNICALL Java_fts_image_NativeInterface_writeImage
(JNIEnv *env, jclass clazz, jint handle, jstring jPath, jint compression) {
	MagickWand *m_wand = getMagickWand(handle);

	MagickSetImageCompressionQuality(m_wand, compression);

	const char *path = env->GetStringUTFChars(jPath, 0);
	MagickWriteImage(m_wand, path);
	env->ReleaseStringUTFChars(jPath, path);
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_resize
(JNIEnv *env, jclass clazz, jint handle, jint width, jint height, jint interpolation, jdouble blur) {
	// TODO select interpolation filter
	MagickWand *m_wand = getMagickWand(handle);
	MagickResizeImage(m_wand, width, height, LanczosFilter, blur);
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_crop
(JNIEnv *env, jclass clazz, jint handle, jint x, jint y, jint width, jint height) {
	MagickWand *m_wand = getMagickWand(handle);
	MagickCropImage(m_wand, width, height, x, y);
}

JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_getWidth
(JNIEnv *env, jclass clazz, jint handle) {
	MagickWand *m_wand = getMagickWand(handle);
	return MagickGetImageWidth(m_wand);
}

JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_getHeight
(JNIEnv *env, jclass clazz, jint handle) {
	MagickWand *m_wand = getMagickWand(handle);
	return MagickGetImageHeight(m_wand);
}

JNIEXPORT jbyteArray JNICALL Java_fts_image_NativeInterface_getImage
(JNIEnv *env, jclass clazz, jint handle, jstring jFormat) {
	MagickWand *m_wand = getMagickWand(handle);

	const char *format = env->GetStringUTFChars(jFormat, 0);
	MagickSetImageFormat(m_wand, format);
	env->ReleaseStringUTFChars(jFormat, format);

	size_t size;
	unsigned char *data = MagickGetImageBlob(m_wand, &size);

	jbyteArray result = env->NewByteArray(size);
	env->SetByteArrayRegion(result, 0, size, (const jbyte *)data);
	return result;
}

JNIEXPORT void JNICALL Java_fts_image_NativeInterface_makeRound
(JNIEnv *env, jclass clazz, jint handle, jint radius) {
	MagickWand *m_wand = getMagickWand(handle);
	size_t width  = MagickGetImageWidth(m_wand);
	size_t height = MagickGetImageHeight(m_wand);

	PixelWand *p_wand = NewPixelWand();
	PixelSetColor(p_wand, "none");

	MagickWand *s_wand = NewMagickWand();
	MagickNewImage(s_wand, width, height, p_wand);

	PixelSetColor(p_wand, "white");
	DrawingWand *d_wand = NewDrawingWand();
	DrawSetFillColor(d_wand, p_wand);
	DrawRoundRectangle(d_wand, radius, radius, width - 1 - radius, height - 1 - radius, radius, radius);
	MagickDrawImage(s_wand, d_wand);

	MagickCompositeImage(m_wand, s_wand, DstInCompositeOp, 0, 0);

	DestroyMagickWand(s_wand);
	DestroyDrawingWand(d_wand);
	DestroyPixelWand(p_wand);
}


#ifdef __cplusplus
}
#endif
