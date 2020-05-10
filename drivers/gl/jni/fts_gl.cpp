#ifdef __cplusplus
extern "C" {
#endif

#include "fts_gl_GLNativeInterface.h"
#include "graphics.h"

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_uiInit
  (JNIEnv *env, jclass thiz) {
	graphics_init();
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_setColor
  (JNIEnv *env, jclass thiz, jint r, jint g, jint b, jint a) {
	graphics_set_color(r, g, b, a);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height, int radius) {
	graphics_draw_rect(x, y, width, height, radius);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawFilledRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height, int radius) {
	graphics_draw_filled_rect(x, y, width, height, radius);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_frameStart
  (JNIEnv *env, jclass thiz, jint width, jint height) {
	graphics_frame_begin(width, height);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_frameEnd
  (JNIEnv *env, jclass thiz) {
	 graphics_frame_end();
 }

#ifdef __cplusplus
}
#endif
