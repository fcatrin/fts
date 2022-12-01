package fts.android.bitmap;

import android.graphics.Bitmap;

public abstract class BitmapLoaderCallback {
    public Bitmap onBackground(Bitmap bitmap) {
        return bitmap;
    }
    public abstract void onSuccess(Bitmap bitmap);
}
