package fts.linux;

public class NativeInterface {
	static {
		System.loadLibrary("fts-linux");
	}

	public static final int FTS_WINDOW_EVENT = 1;
	public static final int FTS_TOUCH_EVENT = 2;
	public static final int FTS_KEY_EVENT = 3;

	public static final int FTS_WINDOW_CLOSE = 1;
	public static final int FTS_MOUSE_DOWN   = 2;
	public static final int FTS_MOUSE_UP     = 3;
	public static final int FTS_MOUSE_MOVE   = 4;
	public static final int FTS_KEY_DOWN   = 5;
	public static final int FTS_KEY_UP     = 6;
	
	public static native void windowOpen(String title, int width, int height);
	public static native void windowSwapBuffers();
	public static native void windowClose();
	public static native int[] windowGetEvents();

}
