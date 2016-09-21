package fts.core;

public abstract class Callback<T> {
	public abstract void onResult(T result);
	public void onError(Exception e){}
	public void onFinally(){};
}
