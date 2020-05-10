package fts.demo.gl;

import fts.core.Application;
import fts.core.Widget;
import fts.core.Window;

public class Main {
	static {
		System.loadLibrary("fts-demo");
	}

	public static void main(String[] args) {
		Application app = new Application(new LinuxGLFactory());
		Window window = Application.createWindow();
		window.setTitle("First FTS window");
		
		Widget rootView = app.inflate(window, "main");
		window.setContentView(rootView);
		window.open();
		window.layout();
		window.mainLoop();
		//System.out.println(rootView);
	}

}
