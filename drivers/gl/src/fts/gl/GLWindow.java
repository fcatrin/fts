package fts.gl;

import fts.core.Window;
import fts.graphics.Point;

public abstract class GLWindow extends Window {

	boolean running;
	
	@Override
	public void mainLoop() {
		running = true;
		while (running) {
			Point size = this.getBounds();
			GLNativeInterface.frameStart(size.x, size.y);
			GLNativeInterface.frameEnd();
			sync();
		}
	}
	
	protected abstract void sync();

}
