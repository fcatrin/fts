package fts.android;

import android.os.Handler;

public class AndroidUIThreadExecutor {
    final Handler handler;

    public AndroidUIThreadExecutor(Handler handler) {
        this.handler = handler;
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    public void post(Runnable runnable, long ms) {
        handler.postDelayed(runnable, ms);
    }
}
