package fts.gl;

import java.io.File;
import java.util.List;

import fts.core.Application;
import fts.core.ComponentFactory;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;

public abstract class GLWindow extends Window {

	boolean running;
	
	@Override
	public void mainLoop() {
		running = true;
		GLNativeInterface.uiInit();
		
		Point size = this.getBounds();
		Canvas canvas = new GLCanvas(size.x, size.y);
		setCanvas(canvas);
		
		layout();
		
		PaintEvent paint = new PaintEvent();
		paint.canvas = canvas;
		paint.clip = null;
		
		while (running) {
			GLNativeInterface.frameStart(size.x, size.y);
			onPaint(paint);
			GLNativeInterface.frameEnd();
			running = sync();
		}
	}
	
	protected abstract boolean sync();
	
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
