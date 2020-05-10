#ifdef __cplusplus
extern "C" {
#endif

#include "fts_demo_gl_LinuxNativeInterface.h"

#include "window.h"

JNIEXPORT void JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowOpen
  (JNIEnv *env , jclass thiz, jint width, jint height) {
	window_open(width, height);
}

JNIEXPORT jboolean JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowSwapBuffers
  (JNIEnv *env, jclass thiz) {
	return window_swap_buffers();
}

JNIEXPORT void JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowClose
  (JNIEnv *env, jclass thiz) {
	window_close();
}

#ifdef __cplusplus
}
#endif


