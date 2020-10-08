package fts.gl;

import fts.graphics.Font;

public class GLNativeInterface {
	
	static {
		System.loadLibrary("fts-gl");
	}
	
	public static native void uiInit();
	
	public static native void frameStart(int width, int height);
	public static native void frameEnd();
	
	public static native void viewStart(int x, int y, int width, int height);
	public static native void viewEnd();

	public static native void setColor(int r, int g, int b, int a);
	public static native void drawText(int x, int y, String text);
	public static native void drawRect(int x, int y, int width, int height, int radius, int strokeWidth);
	public static native void drawFilledRect(int x, int y, int width, int height, int radius);
	public static native void drawGraidentRect(int x, int y, int width, int height, int radius,
			int angle,
			int r_start, int g_start, int b_start, int a_start,
			int r_end, int g_end, int b_end, int a_end);
	
	public static native void drawLine(int x, int y, int dx, int dy);

	public static native int[] getTextSize(String text);
	
	public static native int createBackBuffer(int width, int height);
	public static native void destroyBackBuffer(int handle);
	public static native void bindBackBuffer(int handle);
	public static native void unbindBackBuffer(int handle);
	public static native void drawBackBuffer(int handle, int x, int y, int width, int height);
	
	public static native boolean createFont(String alias, String path);
	
	public static native void setFontSize(int size);
	public static native void setFontName(String name);
	
	public static void setFont(Font font) {
		setFontSize(font.size);
		setFontName(font.name);
	}
	
}
