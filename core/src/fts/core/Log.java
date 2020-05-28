package fts.core;

public class Log {

	public static void d(String tag, String message) {
		Application.logger.d(tag, message);
	}

	public static void e(String tag, String message) {
		Application.logger.e(tag, message);
	}

	public static void i(String tag, String message) {
		Application.logger.i(tag, message);
	}

	public static void e(String tag, String message, Throwable t) {
		Application.logger.e(tag, message, t);
	}
}
