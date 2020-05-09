package fts.gl;

public class GLNativeInterface {
	
	static {
		System.loadLibrary("fts-gl");
	}
	
	public static native void uiInit();
	
	public static native void frameStart(int width, int height);
	public static native void frameEnd();

	public static native void drawRect(int x, int y, int width, int height);
	
}
