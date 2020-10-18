package fts.core;

public abstract class  AsyncExecutor {
	public void asyncExec(Runnable runnable) {
		asyncExec(runnable, 0);
	}
	public abstract void asyncExec(Runnable runnable, long delay);
	public abstract void process(); 
}
