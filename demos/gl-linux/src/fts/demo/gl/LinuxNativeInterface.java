package fts.demo.gl;

public class LinuxNativeInterface {
	public static native void windowOpen(int width, int height);
	public static native boolean windowSwapBuffers();
	public static native void windowClose();

}
