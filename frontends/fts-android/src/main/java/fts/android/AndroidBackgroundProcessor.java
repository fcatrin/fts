package fts.android;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fts.core.BackgroundProcessor;
import fts.core.BackgroundTask;
import fts.core.Log;

public class AndroidBackgroundProcessor implements BackgroundProcessor {
    private static final String LOGTAG = AndroidBackgroundProcessor.class.getSimpleName();

    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    AndroidUIThreadExecutor uiThreadExecutor;

    public AndroidBackgroundProcessor(AndroidUIThreadExecutor uiThreadExecutor) {
        this.uiThreadExecutor = uiThreadExecutor;
    }
    @Override
    public <T> void exec(BackgroundTask<T> task) {
        singleThreadExecutor.execute(new Runnable() {
            T result = null;
            Exception exception = null;

            @Override
            public void run() {
                try {
                    result = task.onBackground();
                } catch (Exception e) {
                    exception = e;
                    Log.e(LOGTAG, "Error processing background task", e);
                }

                uiThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        if (exception == null) {
                            task.onSuccess(result);
                        } else {
                            task.onFailure(exception);
                        }
                        task.onFinally();
                    }
                });
            }
        });
    }

    @Override
    public void start() {}

    @Override
    public void shutdown() {}
}
