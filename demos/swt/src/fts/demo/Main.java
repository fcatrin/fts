package fts.demo;

import fts.core.Application;
import fts.core.Widget;
import fts.core.Window;
import fts.swt.SWTFactory;

public class Main {
	public static void main(String[] args) {
		Application app = new Application(new SWTFactory());
		Window window = Application.createWindow();
		window.setTitle("First FTS window");
		
		Widget rootView = app.inflate(window, "main");
		window.setContentView(rootView);
		window.open();
		window.layout();
		window.mainLoop();
		System.out.println(rootView);

	}
}
