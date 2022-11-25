package fts.ui;

import fts.core.AsyncExecutor;
import fts.core.Logger;

public class Application {
	public static float pointsPerPixel = 1;
	private static AsyncExecutor asyncExecutor = null;
	private static BackgroundProcessor backgroundProcessor = null;
	private static Logger logger;

	public static void init(AsyncExecutor asyncExecutor, BackgroundProcessor backgroundProcessor, Logger logger) {
		Application.asyncExecutor = asyncExecutor;
		Application.backgroundProcessor = backgroundProcessor;
		Application.logger = logger;
	}

	public static AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public static BackgroundProcessor getBackgroundProcessor() {
		return backgroundProcessor;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void post(Runnable runnable) {
		post(runnable, 0);
	}
	
	public static void post(Runnable runnable, long delay) {
		asyncExecutor.asyncExec(runnable, delay);
	}

	public static int pt2px(int value) {
		return (int)(value * pointsPerPixel);
	}
	
	public static int px2pt(int value) {
		return (int)(value / pointsPerPixel);
	}
}
