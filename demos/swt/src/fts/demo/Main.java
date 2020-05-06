package fts.demo;

import fts.core.Application;
import fts.core.Window;
import fts.swt.SWTFactory;
import fts.widgets.Widget;

public class Main {
	public static void main(String[] args) {
		Application app = new Application(new SWTFactory());
		Window window = Application.createWindow();
		window.setTitle("First FTS window");
		
		Widget rootView = app.inflateView(window, "main");
		window.setContentView(rootView);
		window.open();
		window.layout();
		window.mainLoop();
		System.out.println(rootView);

	}
}
