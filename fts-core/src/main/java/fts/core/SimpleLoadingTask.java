package fts.core;

public abstract class SimpleLoadingTask extends LoadingTask<Void> {

	public SimpleLoadingTask(LoadingTaskHost host) {
		this(host, null, null);
	}

	public SimpleLoadingTask(LoadingTaskHost host, String onSuccessMessage, String onFailureMessage) {
		super(host, onSuccessMessage, onFailureMessage);
	}

	@Override
	final public Void onLoading() throws Exception {
		onLoadingTask();
		return null;
	}

	@Override
	final public void onLoadingSuccess(Void result) {
		onSuccess();
	}
	
	public abstract void onLoadingTask() throws Exception;
	public void onSuccess() {}

}
