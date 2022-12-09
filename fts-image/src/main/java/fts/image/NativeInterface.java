package fts.image;

public class NativeInterface {
    static {
        System.loadLibrary("fts-image");
        init();
    }

    public static native void init();
    public static native void done();

    public static native int  create();
    public static native void destroy(int handle);

    public static native void readImage(int handle, String filename);
    public static native void writeImage(int handle, String filename, int compression);
    public static native void resize(int handle, int width, int height, int filter, double blur);
    public static native int  getWidth(int handle);
    public static native int  getHeight(int handle);

}
