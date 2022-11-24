package fts.linux.demo;

import java.io.IOException;

import fts.linux.LinuxApplication;
import fts.linux.Window;
import fts.ui.Widget;

public class LinuxDemo extends Window {

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
		LinuxApplication.init();

		LinuxDemo demo = new LinuxDemo("FTS Demo", 640, 480);
		demo.run();
	}

}
