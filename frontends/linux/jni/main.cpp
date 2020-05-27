#ifdef __cplusplus
extern "C" {
#endif

#include <stdlib.h>
#include <string.h>
#include "fts_linux_NativeInterface.h"

#include "window.h"

JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowOpen
  (JNIEnv *env , jclass thiz, jstring sTitle, jint width, jint height) {

	const char* title = env->GetStringUTFChars(sTitle, 0);
	window_open(title, width, height);
	env->ReleaseStringUTFChars(sTitle, title);
}

JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowSwapBuffers
  (JNIEnv *env, jclass thiz) {
	window_swap_buffers();
}

JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowClose
  (JNIEnv *env, jclass thiz) {
	window_close();
}

JNIEXPORT jintArray JNICALL Java_fts_linux_NativeInterface_windowGetEvents
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


