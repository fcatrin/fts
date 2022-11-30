package fts.android;

import android.os.Handler;

import fts.core.Application;
import fts.core.AsyncExecutor;
import fts.core.BackgroundProcessor;
import fts.utils.dialogs.DialogUtils;

public class AndroidApplication {
    public static void init() {
        final AndroidUIThreadExecutor androidUIThreadExecutor = new AndroidUIThreadExecutor(new Handler());

        AsyncExecutor asyncExecutor = new AndroidAsyncExecutor(androidUIThreadExecutor);
        BackgroundProcessor backgroundProcessor = new AndroidBackgroundProcessor(androidUIThreadExecutor);

        Application.init(asyncExecutor, backgroundProcessor, new AndroidLogger());
        DialogUtils.factory = new AndroidDialogFactory();
    }
}
