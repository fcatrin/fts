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

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawText
  (JNIEnv *env, jclass clazz, jint x, jint y, jstring sText) {
	const char* text = env->GetStringUTFChars(sText,0);
	graphics_draw_text(x, y, text);
	env->ReleaseStringUTFChars(sText, text);
}


JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height, jint radius, jint strokeWidth) {
	graphics_draw_rect(x, y, width, height, radius, strokeWidth);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawFilledRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height, jint radius) {
	graphics_draw_filled_rect(x, y, width, height, radius);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawGraidentRect
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height, jint radius,
		  jint angle,
		  jint r_start, jint g_start, jint b_start, jint a_start,
		  jint r_end, jint g_end, jint b_end, jint a_end) {

	graphics_set_color_start(r_start, g_start, b_start, a_start);
	graphics_set_color_end(r_end, g_end, b_end, a_end);
	graphics_draw_gradient_rect(x, y, width, height, radius, angle);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawLine
  (JNIEnv *env, jclass thiz, jint x, jint y, jint dx, jint dy) {
	graphics_draw_line(x, y, dx, dy);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_frameStart
  (JNIEnv *env, jclass thiz, jint width, jint height) {
	graphics_frame_begin(width, height);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_frameEnd
  (JNIEnv *env, jclass thiz) {
	 graphics_frame_end();
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_viewStart
  (JNIEnv *env, jclass thiz, jint x, jint y, jint width, jint height) {
	graphics_view_start(x, y, width, height);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_viewEnd
  (JNIEnv *env, jclass thiz) {
	graphics_view_end();
}

JNIEXPORT jintArray JNICALL Java_fts_gl_GLNativeInterface_getTextSize
  (JNIEnv *env, jclass thiz, jstring sText) {

	const char* text = env->GetStringUTFChars(sText,0);

	int *size = graphics_get_text_size(text);

	jintArray result = env->NewIntArray(4);
	env->SetIntArrayRegion(result, 0, 4, size);

	env->ReleaseStringUTFChars(sText, text);
	return result;
}

JNIEXPORT jboolean JNICALL Java_fts_gl_GLNativeInterface_createFont
  (JNIEnv *env, jclass thiz, jstring sAlias, jstring sPath) {
	const char* alias = env->GetStringUTFChars(sAlias, 0);
	const char* path  = env->GetStringUTFChars(sPath,  0);

	bool result = graphics_create_font(alias, path);

	env->ReleaseStringUTFChars(sAlias, alias);
	env->ReleaseStringUTFChars(sPath,  path);

	return result;
}


JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_setFontSize
  (JNIEnv *env, jclass thiz, jint size) {
	graphics_set_font_size(size);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_setFontName
  (JNIEnv *env, jclass thiz, jstring sName) {
	const char* name = env->GetStringUTFChars(sName,0);
	graphics_set_font_name(name);
	env->ReleaseStringUTFChars(sName, name);

}

JNIEXPORT jint JNICALL Java_fts_gl_GLNativeInterface_createBackBuffer
  (JNIEnv *env, jclass clazz, jint width, jint height) {
	return graphics_backbuffer_create(width, height);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_destroyBackBuffer
  (JNIEnv *env, jclass clazz, jint handle) {
	graphics_backbuffer_destroy(handle);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_bindBackBuffer
  (JNIEnv *env, jclass clazz, jint handle) {
	graphics_backbuffer_bind(handle);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_unbindBackBuffer
  (JNIEnv *env, jclass clazz, jint handle) {
	graphics_backbuffer_unbind(handle);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawBackBuffer
  (JNIEnv *env, jclass clazz, jint handle, jint x, jint y, jint width, jint height){
	graphics_backbuffer_draw(handle, x, y, width, height);
}

JNIEXPORT jint JNICALL Java_fts_gl_GLNativeInterface_createImage
  (JNIEnv *env, jclass clazz, jbyteArray jData) {

	int size = env->GetArrayLength(jData);
	jbyte *data  = env->GetByteArrayElements(jData, NULL);

	int handle = graphics_image_create((unsigned char *)data, size);

	env->ReleaseByteArrayElements(jData, data, 0);

	return handle;
}

JNIEXPORT jintArray JNICALL Java_fts_gl_GLNativeInterface_getImageSize
  (JNIEnv *env, jclass clazz, jint handle) {
	int size[2];
	graphics_image_get_size(handle, &size[0], &size[1]);

	jintArray result = env->NewIntArray(2);
	env->SetIntArrayRegion(result, 0, 2, size);
	return result;
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_destroyImage
  (JNIEnv *env, jclass clazz, jint handle){
	graphics_image_destroy(handle);
}

JNIEXPORT void JNICALL Java_fts_gl_GLNativeInterface_drawImage
  (JNIEnv *env, jclass clazz, jint handle, jint x, jint y, jint width, jint height){
	graphics_image_draw(handle, x, y, width, height);
}

#ifdef __cplusplus
}
#endif
