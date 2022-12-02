package fts.core;

public class Application {
	private static final String LOGTAG = Application.class.getSimpleName();

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

	public static void setCrashHandler(final Callback<Throwable> handler) {
		final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				Log.e(LOGTAG, "Uncaught Exception. Sending Trace", throwable);
				sendTrace(handler, throwable);
				if (oldHandler != null) {
					oldHandler.uncaughtException(thread, throwable);
				} else {
					System.exit(2);
				}
			}
		});
	}

	private static void sendTrace(final Callback<Throwable> handler, final Throwable t) {
		Thread sendTraceThread = new Thread() {
			@Override
			public void run() {
				handler.onResult(t);
			}
		};
		sendTraceThread.start();
		CoreUtils.sleep(200); // allow this thread to be started
	}

}
