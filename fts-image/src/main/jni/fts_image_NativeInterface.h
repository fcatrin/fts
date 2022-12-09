/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class fts_image_NativeInterface */

#ifndef _Included_fts_image_NativeInterface
#define _Included_fts_image_NativeInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     fts_image_NativeInterface
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_init
  (JNIEnv *, jclass);

/*
 * Class:     fts_image_NativeInterface
 * Method:    done
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_done
  (JNIEnv *, jclass);

/*
 * Class:     fts_image_NativeInterface
 * Method:    create
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_create
  (JNIEnv *, jclass);

/*
 * Class:     fts_image_NativeInterface
 * Method:    destroy
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_destroy
  (JNIEnv *, jclass, jint);

/*
 * Class:     fts_image_NativeInterface
 * Method:    readImage
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_readImage
  (JNIEnv *, jclass, jint, jstring);

/*
 * Class:     fts_image_NativeInterface
 * Method:    readImageData
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_readImageData
  (JNIEnv *, jclass, jint, jbyteArray);

/*
 * Class:     fts_image_NativeInterface
 * Method:    writeImage
 * Signature: (ILjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_writeImage
  (JNIEnv *, jclass, jint, jstring, jint);

/*
 * Class:     fts_image_NativeInterface
 * Method:    resize
 * Signature: (IIIID)V
 */
JNIEXPORT void JNICALL Java_fts_image_NativeInterface_resize
  (JNIEnv *, jclass, jint, jint, jint, jint, jdouble);

/*
 * Class:     fts_image_NativeInterface
 * Method:    getWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_getWidth
  (JNIEnv *, jclass, jint);

/*
 * Class:     fts_image_NativeInterface
 * Method:    getHeight
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_fts_image_NativeInterface_getHeight
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
