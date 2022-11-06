package fts.linux;

import fts.core.AppContext;
import fts.core.Application;
import fts.core.CoreBackgroundProcessor;
import fts.core.CoreAsyncExecutor;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;

public class LinuxApplication {
    public static void init() {
        AppContext.asyncExecutor = new CoreAsyncExecutor();
        AppContext.backgroundProcessor = new CoreBackgroundProcessor(AppContext.asyncExecutor);
        Application.init(new ComponentFactory(), new DesktopResourceLocator(), new DesktopLogger(), new AppContext());
    }
}
