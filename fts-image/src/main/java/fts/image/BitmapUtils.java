package fts.image;

public class BitmapUtils {
    public enum ResizeMethod {KeepAspect, Fit, Crop}

    private BitmapUtils(){}

    public static void resize(Bitmap bitmap, int width, int height, ResizeMethod resizeMethod) {
        int srcWidth  = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        int dstWidth  = width;
        int dstHeight = height;
        float srcRatio = (float)srcWidth / srcHeight;
        float dstRatio = (float)width / height;

        if (resizeMethod == ResizeMethod.KeepAspect) {
            if (srcRatio > dstRatio) {
                dstHeight = (int)(dstWidth / srcRatio);
            } else {
                dstWidth = (int)(dstHeight * srcRatio);
            }
        } else if (resizeMethod == ResizeMethod.Crop) {
            if (srcRatio > dstRatio) {
                dstWidth = (int)(dstHeight * srcRatio);
            } else {
                dstHeight = (int)(dstWidth / srcRatio);
            }
        }

        bitmap.resize(dstWidth, dstHeight);

        int cropX = (dstWidth - width) / 2;
        int cropY = (dstHeight - height) / 2;
        if (cropX != 0 || cropY != 0) {
            bitmap.crop(cropX, cropY, width, height);
        }
    }

}
