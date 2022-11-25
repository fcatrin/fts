package fts.android;

import android.app.Activity;
import android.os.Handler;

import fts.core.AsyncExecutor;
import fts.core.CoreAsyncExecutor;
import fts.ui.Application;
import fts.ui.Resources;
import fts.ui.CoreBackgroundProcessor;

public class AndroidApplication {

    private static Handler handler;

    public static void init(Activity activity) {
        handler = new Handler();

        AsyncExecutor asyncExecutor = new CoreAsyncExecutor();
        Application.init(
                asyncExecutor,
                new CoreBackgroundProcessor(asyncExecutor),
                new AndroidLogger());

        Resources.init(
                new AndroidComponentFactory(activity),
                new AndroidResourceLocator(activity));
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }

    public static void post(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }
}
