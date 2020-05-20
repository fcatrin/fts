#ifdef __cplusplus
extern "C" {
#endif

#include <stdlib.h>
#include <string.h>
#include "fts_demo_gl_LinuxNativeInterface.h"

#include "window.h"

JNIEXPORT void JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowOpen
  (JNIEnv *env , jclass thiz, jint width, jint height) {
	window_open(width, height);
}

JNIEXPORT void JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowSwapBuffers
  (JNIEnv *env, jclass thiz) {
	window_swap_buffers();
}

JNIEXPORT void JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowClose
  (JNIEnv *env, jclass thiz) {
	window_close();
}

JNIEXPORT jintArray JNICALL Java_fts_demo_gl_LinuxNativeInterface_windowGetEvents
  (JNIEnv *env, jclass thiz) {
	int n_events = window_process_events();
	if (n_events == 0) return NULL;

	struct event *events = window_get_events();

	int *event_values = (int *)calloc(n_events, event_size_int * sizeof(int));
	memcpy(event_values, events, n_events * event_size_int * sizeof(int));

	jintArray result = env->NewIntArray(n_events * event_size_int);
	env->SetIntArrayRegion(result, 0, n_events * event_size_int, event_values);

	free(event_values);
	return result;
}


#ifdef __cplusplus
}
#endif


