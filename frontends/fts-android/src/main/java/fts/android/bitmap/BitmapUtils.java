package fts.android.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapUtils {
    public static Bitmap makeSquare(Bitmap bitmap) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height * width == 0) return bitmap;

        float ratio = (float)width / height;
        if (ratio >= 0.95f && ratio < 1.05f) return bitmap; // square enough

        int newWidth = width;
        int newHeight = height;
        if (ratio > 1) {
            newWidth = height;
        } else {
            newHeight = width;
        }
        int left = (width - newWidth) / 2;
        int top  = (height - newHeight) / 2;

        Rect srcRect = new Rect(left, top, left + newWidth, top + newHeight);
        Rect dstRect = new Rect(0, 0, newWidth, newHeight);

        Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        bitmap.recycle();

        return newBitmap;
    }

}
