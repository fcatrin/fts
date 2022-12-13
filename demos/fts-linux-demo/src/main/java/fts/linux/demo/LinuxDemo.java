package fts.linux.demo;

import java.io.IOException;

import fts.linux.DesktopApplication;
import fts.linux.DesktopWindow;
import fts.linux.NativeInterface;
import fts.ui.Resources;
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
	protected String getAppCode() {
		return "fts.linux.demo";
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
		demo.setIcon("@images/penguin.png");
		demo.run();
	}

}
