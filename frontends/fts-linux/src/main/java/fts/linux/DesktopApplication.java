package fts.linux;

import fts.core.Application;
import fts.core.AsyncExecutor;
import fts.core.CoreAsyncExecutor;
import fts.core.CoreBackgroundProcessor;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;
import fts.ui.Resources;
import fts.ui.dialogs.SimpleDialogs;
import fts.utils.dialogs.DialogUtils;

public class DesktopApplication {
    public static void init() {
        AsyncExecutor asyncExecutor = new CoreAsyncExecutor();
        Application.init(
                asyncExecutor,
                new CoreBackgroundProcessor(asyncExecutor),
                new DesktopLogger());

        DialogUtils.factory = new SimpleDialogs();

        Resources.init(new ComponentFactory(), new DesktopResourceLocator());
    }
}
