package fts.android;

import android.app.Activity;
import android.os.Handler;

import fts.core.AppContext;
import fts.core.Application;
import fts.core.BackgroundProcessor;
import fts.core.CoreAsyncExecutor;

public class AndroidApplication {

    private static Handler handler;

    public static void init(Activity activity) {
        handler = new Handler();
        
        AppContext.asyncExecutor = new CoreAsyncExecutor();
        AppContext.backgroundProcessor = new BackgroundProcessor(AppContext.asyncExecutor);

        Application.init(
                new AndroidComponentFactory(activity),
                new AndroidResourceLocator(activity),
                new AndroidLogger(),
                new AppContext());
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }

    public static void post(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }
}
