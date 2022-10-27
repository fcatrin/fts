package fts.core;

public abstract class SimpleBackgroundTask extends BackgroundTask<Void> {

	@Override
	final public Void onBackground() throws Exception {
		onBackgroundTask();
		return null;
	}

	@Override
	final public void onSuccess(Void result) {
		onSuccess();
	}
	
	public abstract void onBackgroundTask() throws Exception;
	public void onSuccess() {};

}
