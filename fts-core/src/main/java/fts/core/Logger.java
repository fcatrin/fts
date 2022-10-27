package fts.core;

public interface Logger {
	public void d(String tag, String message);
	public void e(String tag, String message);
	public void i(String tag, String message);
	public void e(String tag, String message, Throwable t);
}
