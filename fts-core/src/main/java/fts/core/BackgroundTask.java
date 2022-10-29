package fts.core;

public abstract class BackgroundTask<T> {
	public abstract T onBackground() throws Exception;
	public abstract void onSuccess(T result);
	public void onFailure(Exception e) {e.printStackTrace();}
	public void onFinally() {}
	
	public void execute() {
		AppContext.backgroundProcessor.exec(this);
	}
}
