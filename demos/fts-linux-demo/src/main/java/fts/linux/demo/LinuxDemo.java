package fts.linux.demo;

import java.io.IOException;

import fts.linux.DesktopApplication;
import fts.linux.DesktopWindow;
import fts.ui.Widget;

public class LinuxDemo extends DesktopWindow {

	public LinuxDemo(String title, int width, int height) {
		super(title, width, height);
	}
	
	@Override
	public void onWindowCreate() {
		Widget rootView = inflate("main");
		setContentView(rootView);
	}
	
	@Override
	public void onFrame() {
	}
	
	@Override
	public void onWindowStop() {
	}

	@Override
	public void onWindowStart() {
	}

	public static void main(String[] args) throws IOException {
		DesktopApplication.init();

		LinuxDemo demo = new LinuxDemo("FTS Demo", 640, 480);
		demo.run();
	}

}
