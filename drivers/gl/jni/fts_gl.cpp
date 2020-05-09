#ifdef __cplusplus
extern "C" {
#endif

#include "fts_gl_GLNativeInterface.h"
#include "graphics.h"

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height) {

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
