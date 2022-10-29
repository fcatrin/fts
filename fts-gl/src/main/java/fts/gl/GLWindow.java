package fts.gl;

import java.io.File;
import java.util.List;

import fts.core.Application;
import fts.core.BackgroundProcessor;
import fts.core.ComponentFactory;
import fts.core.AppContext;
import fts.core.CoreAsyncExecutor;
import fts.core.NativeWindow;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;

public class GLWindow extends NativeWindow {

	PaintEvent paintEvent = new PaintEvent();
	boolean running;
	
	public void init() {
		AppContext.asyncExecutor = new CoreAsyncExecutor();
		AppContext.backgroundProcessor = new BackgroundProcessor(AppContext.asyncExecutor);
		
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
		GLCanvas canvas = (GLCanvas)getCanvas();
		for(String alias : aliases) {
			File fontFile = factory.getFont(alias);
			canvas.createFont(alias, fontFile);
		}
	}

}
