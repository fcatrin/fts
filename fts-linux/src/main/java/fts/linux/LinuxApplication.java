package fts.linux;

import fts.core.AppContext;
import fts.core.Application;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;

public class LinuxApplication {
    public static void init() {
        Application.init(new ComponentFactory(), new DesktopResourceLocator(), new DesktopLogger(), new AppContext());
    }
}
