package fts.gl;

import fts.graphics.Font;

public class GLNativeInterface {
	
	static {
		System.loadLibrary("fts-gl");
	}
	
	public static native void uiInit();
	
	public static native void frameStart(int width, int height);
	public static native void frameEnd();

	public static native void setColor(int r, int g, int b, int a);
	public static native void drawRect(int x, int y, int width, int height, int radius);
	public static native void drawFilledRect(int x, int y, int width, int height, int radius);
	
	public static native int[] getTextSize(String text);
	
	public static native boolean createFont(String alias, String path);
	
	public static native void setFontSize(int size);
	public static native void setFontName(String name);
	
	public static void setFont(Font font) {
		setFontSize(font.size);
		setFontName(font.name);
	}
	
}
