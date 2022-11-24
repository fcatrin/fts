package fts.gl;

import java.io.File;
import java.util.List;

import fts.ui.AppContext;
import fts.ui.Application;
import fts.ui.ComponentFactory;
import fts.ui.Log;
import fts.ui.NativeWindow;
import fts.ui.events.PaintEvent;
import fts.ui.graphics.Canvas;
import fts.ui.graphics.Point;

public class GLWindow extends NativeWindow {
	private static final String LOGTAG = GLWindow.class.getSimpleName();

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
		
		AppContext.asyncExecutor.process();
	}
	
	@Override
	public void mainLoop() {
		AppContext.backgroundProcessor.start();
		
		requestLayout();
		while (running) {
			render();
		}
		
		AppContext.backgroundProcessor.shutdown();
	}
	
	protected boolean sync() {
		return ((GLWindowListener)windowListener).sync();
	}
	
	protected void createAllFonts() {
		ComponentFactory factory = Application.getFactory();
		List<String> aliases = factory.getAllFontAliases();
		if (aliases.isEmpty()) {
			Log.e(LOGTAG, "No fonts found on resources (fonts/ values/fonts.xml)");
		}
		GLCanvas canvas = (GLCanvas)getCanvas();
		for(String alias : aliases) {
			File fontFile = factory.getFont(alias);
			canvas.createFont(alias, fontFile);
		}
	}

}
