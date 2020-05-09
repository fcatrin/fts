package fts.gl;

import fts.core.Window;

public abstract class GLWindow extends Window {

	boolean running;
	
	@Override
	public void mainLoop() {
		running = true;
		GLNativeInterface.uiInit();
		while (running) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void sync();

}
