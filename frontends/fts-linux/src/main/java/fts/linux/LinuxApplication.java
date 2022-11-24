package fts.linux;

import fts.core.CoreAsyncExecutor;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;
import fts.ui.AppContext;
import fts.ui.Application;
import fts.ui.CoreBackgroundProcessor;

public class LinuxApplication {
    public static void init() {
        AppContext.asyncExecutor = new CoreAsyncExecutor();
        AppContext.backgroundProcessor = new CoreBackgroundProcessor(AppContext.asyncExecutor);
        Application.init(new ComponentFactory(), new DesktopResourceLocator(), new DesktopLogger(), new AppContext());
    }
}
