package fts.android;

import fts.core.AsyncExecutor;

public class AndroidAsyncExecutor extends AsyncExecutor {
    private final AndroidUIThreadExecutor uiThreadExecutor;

    public AndroidAsyncExecutor(AndroidUIThreadExecutor uiThreadExecutor) {
        this.uiThreadExecutor = uiThreadExecutor;
    }
    @Override
    public void asyncExec(Runnable runnable, long delay) {
        uiThreadExecutor.post(runnable, delay);
    }

    @Override
    public void process() {}
}
