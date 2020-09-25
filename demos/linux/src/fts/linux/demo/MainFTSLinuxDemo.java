package fts.linux.demo;

import fts.core.Application;
import fts.core.Context;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;
import fts.core.Widget;
import fts.core.NativeWindow;
import fts.linux.ComponentFactory;

public class MainFTSLinuxDemo {

	public static void main(String[] args) {
		Application app = new Application(new ComponentFactory(), new DesktopResourceLocator(), new DesktopLogger(), new Context());
		NativeWindow window = Application.createWindow("Linux FTS window", 820, 240);
		
		Widget rootView = app.inflate(window, "main");
		window.setContentView(rootView);
		window.open();
		window.mainLoop();
	}

}
