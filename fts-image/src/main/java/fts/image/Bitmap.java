package fts.image;

import java.io.File;

public class Bitmap {
    private int handle;
    private int compressionLevel = 95;
    private int blurLevel = 1;

    public Bitmap() {
        init();
    }

    public Bitmap(File file) {
        init();
        load(file);
    }

    public Bitmap(byte[] data) {
        init();
        load(data);
    }

    private void init() {
        handle = NativeInterface.create();
    }

    public void load(File file) {
        NativeInterface.readImage(handle, file.getAbsolutePath());
    }

    public void load(byte[] data) {
        NativeInterface.readImageData(handle, data);
    }

    public void save(File file) {
        NativeInterface.writeImage(handle, file.getAbsolutePath(), compressionLevel);
    }

    public void resize(int width, int height) {
        NativeInterface.resize(handle, width, height, 0, blurLevel);
    }

    public void crop(int x, int y, int width, int height) {
        NativeInterface.crop(handle, x, y, width, height);
    }

    public int getWidth() {
        return NativeInterface.getWidth(handle);
    }

    public int getHeight() {
        return NativeInterface.getHeight(handle);
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public void setResizeBlurLevel(int blurLevel) {
        this.blurLevel = blurLevel;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() {
        if (handle == 0) return;
        NativeInterface.destroy(handle);
        handle = 0;
    }
}
