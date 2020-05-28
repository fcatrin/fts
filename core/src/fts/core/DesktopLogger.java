package fts.core;

public class DesktopLogger implements Logger {

	private String build(String tag, String message) {
		return "[" + tag + "] " + message;
	}
	
	@Override
	public void d(String tag, String message) {
		System.out.println(build(tag, message));
	}

	@Override
	public void e(String tag, String message) {
		System.err.println(build(tag, message));
	}

	@Override
	public void i(String tag, String message) {
		System.out.println(build(tag, message));
	}

	@Override
	public void e(String tag, String message, Throwable t) {
		e(tag, message);
		t.printStackTrace();
	}

}
