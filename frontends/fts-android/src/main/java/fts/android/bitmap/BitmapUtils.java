package fts.android.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import fts.core.CoreUtils;

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

    public static void saveBitmap(File f, Bitmap bitmap, int quality) throws IOException {
        Bitmap.CompressFormat format = f.getName().endsWith(".jpg")? Bitmap.CompressFormat.JPEG: Bitmap.CompressFormat.PNG;
        saveBitmap(f, bitmap, format, quality);
    }

    public static void saveBitmap(File f, Bitmap bitmap, Bitmap.CompressFormat format, int quality) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, stream);
        CoreUtils.saveBytes(f, stream.toByteArray());
    }

    // use optimized memory size from https://developer.android.com/topic/performance/graphics/load-bitmap.html
    public static Bitmap loadBitmap(File f, int reqWidth, int reqHeight) throws IOException {
        byte raw[] = CoreUtils.loadBytes(f);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(raw , 0, raw .length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        return BitmapFactory.decodeByteArray(raw , 0, raw .length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
