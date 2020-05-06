package fts.core;

public abstract class AsyncTask<T> {
	public static AsyncProcessor asyncProcessor;
	
	public abstract T onBackground() throws Exception;
	public abstract void onResult(T result);
	public void onError(Exception e) {e.printStackTrace();}
	
	public void execute() {
		asyncProcessor.exec(this);
	}
}
