package fts.core;

public abstract class Callback<T> {
	public abstract void onResult(T result);
	public void onCancel(){}
	public void onFailure(Exception e){}
	public void onFinally(){};
}
