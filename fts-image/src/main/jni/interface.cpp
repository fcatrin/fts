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

#ifdef __cplusplus
}
#endif
