package fts.core;

public class Context {
	public static float pointsPerPixel = 1;
	
	public static Context getInstance() {
		return Application.context;
	}
	
	public static AsyncExecutor getAsyncExecutor() {
		return getInstance().getAsyncExecutorImpl();
	}
	
	private AsyncExecutor getAsyncExecutorImpl() {
		return null;
	}

	public static void post(Runnable runnable) {
		AsyncExecutor asyncExecutor = getAsyncExecutor();
		asyncExecutor.asyncExec(runnable);
	}

	public int pt2px(int value) {
		return (int)(value * pointsPerPixel);
	}
	
	public int px2pt(int value) {
		return (int)(value / pointsPerPixel);
	}
}
