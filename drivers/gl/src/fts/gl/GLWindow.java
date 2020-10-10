package fts.gl;

import java.io.File;
import java.util.List;

import fts.core.Application;
import fts.core.ComponentFactory;
import fts.core.NativeWindow;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;

public class GLWindow extends NativeWindow {

	PaintEvent paintEvent = new PaintEvent();
	boolean running;
	
	public void init() {
		running = true;
		GLNativeInterface.uiInit();
		
		Point size = this.getBounds();
		Canvas canvas = new GLCanvas(size.x, size.y);
		setCanvas(canvas);
		
		createAllFonts();
	}
	
	public void render() {
		paintEvent.canvas = getCanvas();
		paintEvent.clip = null;

		updateFocus();
		doPendingLayout();
		doRender(paintEvent);
		
		Point size = this.getBounds();
		GLNativeInterface.frameStart(size.x, size.y);
		doPaint(paintEvent);
		GLNativeInterface.frameEnd();
		running = sync();
		
		windowListener.onFrame();
	}
	
	@Override
	public void mainLoop() {
		requestLayout();
		while (running) {
			render();
		}
	}
	
	protected boolean sync() {
		return ((GLWindowListener)windowListener).sync();
	}
	
	protected void createAllFonts() {
		ComponentFactory factory = Application.getFactory();
		List<String> aliases = factory.getAllFontAliases();
		GLCanvas canvas = (GLCanvas)getCanvas();
		for(String alias : aliases) {
			File fontFile = factory.getFont(alias);
			canvas.createFont(alias, fontFile);
		}
	}

}
