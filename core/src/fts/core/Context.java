package fts.core;

public class Context {
	static Context instance = null;
	public static float pointsPerPixel = 1;
	
	public static Context getInstance() {
		if (instance!=null) return instance;
		instance = new Context().createInstance();
		return instance;
	}
	
	public static AsyncExecutor getAsyncExecutor() {
		return getInstance().getAsyncExecutorImpl();
	}
	
	private AsyncExecutor getAsyncExecutorImpl() {
		return null;
	}

	protected Context createInstance() {
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
