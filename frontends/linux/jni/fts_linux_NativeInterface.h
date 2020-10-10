/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class fts_linux_NativeInterface */

#ifndef _Included_fts_linux_NativeInterface
#define _Included_fts_linux_NativeInterface
#ifdef __cplusplus
extern "C" {
#endif
#undef fts_linux_NativeInterface_FTS_WINDOW_EVENT
#define fts_linux_NativeInterface_FTS_WINDOW_EVENT 1L
#undef fts_linux_NativeInterface_FTS_TOUCH_EVENT
#define fts_linux_NativeInterface_FTS_TOUCH_EVENT 2L
#undef fts_linux_NativeInterface_FTS_KEY_EVENT
#define fts_linux_NativeInterface_FTS_KEY_EVENT 3L
#undef fts_linux_NativeInterface_FTS_WINDOW_CLOSE
#define fts_linux_NativeInterface_FTS_WINDOW_CLOSE 1L
#undef fts_linux_NativeInterface_FTS_MOUSE_DOWN
#define fts_linux_NativeInterface_FTS_MOUSE_DOWN 2L
#undef fts_linux_NativeInterface_FTS_MOUSE_UP
#define fts_linux_NativeInterface_FTS_MOUSE_UP 3L
#undef fts_linux_NativeInterface_FTS_MOUSE_MOVE
#define fts_linux_NativeInterface_FTS_MOUSE_MOVE 4L
#undef fts_linux_NativeInterface_FTS_KEY_DOWN
#define fts_linux_NativeInterface_FTS_KEY_DOWN 5L
#undef fts_linux_NativeInterface_FTS_KEY_UP
#define fts_linux_NativeInterface_FTS_KEY_UP 6L
/*
 * Class:     fts_linux_NativeInterface
 * Method:    windowOpen
 * Signature: (Ljava/lang/String;IIIII)V
 */
JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowOpen
  (JNIEnv *, jclass, jstring, jint, jint, jint, jint, jint);

/*
 * Class:     fts_linux_NativeInterface
 * Method:    windowSwapBuffers
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowSwapBuffers
  (JNIEnv *, jclass);

/*
 * Class:     fts_linux_NativeInterface
 * Method:    windowClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_fts_linux_NativeInterface_windowClose
  (JNIEnv *, jclass);

/*
 * Class:     fts_linux_NativeInterface
 * Method:    windowGetEvents
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_fts_linux_NativeInterface_windowGetEvents
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
