package fts.android.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

import fts.android.AndroidWindow;
import fts.android.CachedImageLoader;
import fts.android.R;
import fts.core.BackgroundTask;
import fts.core.Callback;

public class BitmapLoader {
    private AndroidWindow activity;
    private ImageView imageView;
    private static long lastDownloadInstance;

    public BitmapLoader(AndroidWindow activity, ImageView imageView) {
        init(activity, imageView);
    }

    public BitmapLoader(AndroidWindow activity) {
        init(activity, null);
    }

    private void init(AndroidWindow activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    public void load(String url) {
        load(url, new BitmapLoaderCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (imageView != null) imageView.setImageBitmap(bitmap);
            }
        });
    }
    
    public void load(String url, BitmapLoaderCallback callback) {
        final long loadingInstance = ++lastDownloadInstance;
        if (imageView != null) imageView.setTag(R.string.image_loading_id, loadingInstance);

        BackgroundTask<Bitmap> task = new BackgroundTask<Bitmap>() {
            @Override
            public Bitmap onBackground() throws Exception {
                if (!isStillValid(loadingInstance)) return null;
                return callback.onBackground(CachedImageLoader.load(activity.getCacheDir(), url));
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                if (bitmap == null || !isStillValid(loadingInstance)) return;
                callback.onSuccess(bitmap);
            }
        };
        task.execute();
    }

    private boolean isStillValid(long loadingInstance) {
        return imageView == null || (long)imageView.getTag(R.string.image_loading_id) == loadingInstance;
    }

}
