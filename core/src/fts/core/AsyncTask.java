package fts.core;

public abstract class AsyncTask<T> {
	public static AsyncProcessor asyncProcessor;
	
	public abstract T onBackground() throws Exception;
	public abstract void onSuccess(T result);
	public void onFailure(Exception e) {e.printStackTrace();}
	public void onFinally() {}
	
	public void execute() {
		asyncProcessor.exec(this);
	}
}
