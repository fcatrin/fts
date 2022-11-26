package fts.core;

import java.io.File;

public class FilesCache {
    private final static String LOGTAG = FilesCache.class.getSimpleName();
    private final static long TRIM_DELAY = 5000;

    private final File dir;
    private float percentage;
    private int megabytes;
    private static long lastTrimTime = 0;

    public FilesCache(File dir, int megabytes, float percentage) {
        this.dir = dir;
        this.percentage = percentage;
        this.megabytes = megabytes;
    }

    public File getFile(String filename) {
        trim();
        return new File(dir, filename);
    }

    private void trim() {
        long now = System.currentTimeMillis();
        long delta = now - lastTrimTime;
        if (delta < TRIM_DELAY) return;
        lastTrimTime = now;

        // check max size first
        float factor = 1.0f;

        long size;
        do {
            long olderModified = Long.MAX_VALUE;
            File olderFile = null;
            size = 0;

            File[] files = dir.listFiles();
            if (files == null) return;

            for (File file : files) {
                long lastModified = file.lastModified();
                if (lastModified < olderModified) {
                    olderModified = lastModified;
                    olderFile = file;
                }
                size += file.length();
            }
            if (isTrimmed(size, factor)) return;
            if (olderFile!=null) {
                Log.d(LOGTAG, "remove " + olderFile);
                olderFile.delete();
            }
            factor = 0.75f; // make more headroom space on the next cycle
        } while (size > 0);
    }

    private boolean isTrimmed(long size, float factor) {
        long maxSize1 = (long)(megabytes * 1024 * 1024 * factor);
        long maxSize2 = (long)(dir.getFreeSpace() * percentage * factor);
        Log.d(LOGTAG, String.format("isTrimmed mb:%d p:%f (%d) check %d factor:%f",
            maxSize1, percentage, maxSize2, size, factor));
        return size <= maxSize1 && size <= maxSize2;
    }
}
