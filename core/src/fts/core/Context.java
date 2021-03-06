package fts.core;

public class Context {
	public static float pointsPerPixel = 1;
	public static AsyncExecutor asyncExecutor = null;
	public static BackgroundProcessor backgroundProcessor = null;
	
	public static Context getInstance() {
		return Application.context;
	}
	

	public static void post(Runnable runnable) {
		post(runnable, 0);
	}
	
	public static void post(Runnable runnable, long delay) {
		asyncExecutor.asyncExec(runnable, delay);
	}

	public int pt2px(int value) {
		return (int)(value * pointsPerPixel);
	}
	
	public int px2pt(int value) {
		return (int)(value / pointsPerPixel);
	}
}
