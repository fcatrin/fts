package fts.linux;

import fts.core.AsyncExecutor;
import fts.core.CoreAsyncExecutor;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;
import fts.core.Application;
import fts.ui.Resources;
import fts.ui.CoreBackgroundProcessor;

public class LinuxApplication {
    public static void init() {
        AsyncExecutor asyncExecutor = new CoreAsyncExecutor();
        Application.init(
                asyncExecutor,
                new CoreBackgroundProcessor(asyncExecutor),
                new DesktopLogger());

        Resources.init(new ComponentFactory(), new DesktopResourceLocator());
    }
}
