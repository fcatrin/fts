package fts.gl;

import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Point;

public abstract class GLWindow extends Window {

	boolean running;
	
	@Override
	public void mainLoop() {
		running = true;
		GLNativeInterface.uiInit();
		
		PaintEvent paint = new PaintEvent();
		Point size = this.getBounds();
		paint.canvas = new GLCanvas(size.x, size.y);
		paint.clip = null;
		
		while (running) {
			GLNativeInterface.frameStart(size.x, size.y);
			onPaint(paint);
			GLNativeInterface.frameEnd();
			running = sync();
		}
	}
	
	protected abstract boolean sync();

}
