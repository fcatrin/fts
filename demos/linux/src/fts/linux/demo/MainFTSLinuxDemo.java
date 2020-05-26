package fts.linux.demo;

import fts.core.Application;
import fts.core.Context;
import fts.core.DesktopResourceLocator;
import fts.core.Widget;
import fts.core.Window;
import fts.linux.ComponentFactory;

public class MainFTSLinuxDemo {

	public static void main(String[] args) {
		Application app = new Application(new ComponentFactory(), new DesktopResourceLocator(), new Context());
		Window window = Application.createWindow();
		window.setTitle("First FTS window");
		
		Widget rootView = app.inflate(window, "main");
		window.setContentView(rootView);
		window.open();
		window.mainLoop();	}

}
