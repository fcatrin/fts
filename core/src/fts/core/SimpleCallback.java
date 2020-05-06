package fts.core;

public abstract class SimpleCallback extends Callback<Void> {

	@Override
	public final void onResult(Void result) {
		onResult();
	}
	
	public abstract void onResult();

}
