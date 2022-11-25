package fts.core;

public class Log {

	public static void d(String tag, String message) {
		Application.getLogger().d(tag, message);
	}

	public static void e(String tag, String message) {
		Application.getLogger().e(tag, message);
	}

	public static void i(String tag, String message) {
		Application.getLogger().i(tag, message);
	}

	public static void e(String tag, String message, Throwable t) {
		Application.getLogger().e(tag, message, t);
	}
}
