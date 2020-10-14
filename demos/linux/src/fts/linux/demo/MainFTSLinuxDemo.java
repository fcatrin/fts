package fts.linux.demo;

import fts.core.Widget;
import fts.linux.Window;

public class MainFTSLinuxDemo extends Window {

	public MainFTSLinuxDemo(String title, int width, int height) {
		super(title, width, height);
	}
	
	@Override
	public void onWindowCreate() {
		Widget rootView = inflate("main");
		setContentView(rootView);
	}

	public static void main(String[] args) {
		MainFTSLinuxDemo window = new MainFTSLinuxDemo("Linux FTS window", 820, 240);
		
		window.run();
	}

}
