package fts.gl;

public class GLNativeInterface {
	
	public static native void frameStart(int width, int height);
	public static native void frameEnd();

	public static native void drawRect(int x, int y, int width, int height);
	
}
