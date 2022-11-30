package fts.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

import fts.core.FilesCache;
import fts.core.CoreUtils;

public class CachedImageLoader {
    static FilesCache cache = null;
    public static Bitmap load(File cacheDir, String url) throws IOException {
        if (cache == null) {
            cache = new FilesCache(new File(cacheDir, "cached.images"), 100, 0.05f);
        }
        File cachedFile = cache.getFile(CoreUtils.md5(url));
        byte[] bitmapData;
        if (!cachedFile.exists()) {
            cachedFile.getParentFile().mkdirs();

            bitmapData = DownloadManager.download(url);
            CoreUtils.saveBytes(cachedFile, bitmapData);
        } else {
            bitmapData = CoreUtils.loadBytes(cachedFile);
        }
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }
}
