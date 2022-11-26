package fts.android.gl;

import android.app.Activity;
import android.os.Handler;

import fts.android.AndroidLogger;
import fts.core.AsyncExecutor;
import fts.core.CoreAsyncExecutor;
import fts.core.Application;
import fts.ui.Resources;
import fts.core.CoreBackgroundProcessor;

public class AndroidGLApplication {

    private static Handler handler;

    public static void init(Activity activity) {
        handler = new Handler();

        AsyncExecutor asyncExecutor = new CoreAsyncExecutor();
        Application.init(
                asyncExecutor,
                new CoreBackgroundProcessor(asyncExecutor),
                new AndroidLogger());

        Resources.init(
                new AndroidGLComponentFactory(activity),
                new AndroidGLResourceLocator(activity));
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }

    public static void post(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }
}
