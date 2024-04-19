package fts.core;

public abstract class LoadingTask<T> extends BackgroundTask<Exception> {
	private static final String LOGTAG = LoadingTask.class.getSimpleName();
	public enum ProgressType {Progressive, Infinite}
	
	private final String onSuccessMessage;
	private final String onFailureMessage;
	private final LoadingTaskHost host;
	private final ProgressType progressType;
	
	T result = null;

	public LoadingTask(LoadingTaskHost host) {
		this(host, null, null);
	}

	public LoadingTask(LoadingTaskHost host, String onFailureMessage) {
		this(host, null, onFailureMessage);
	}

	public LoadingTask(LoadingTaskHost host, ProgressType progressType) {
		this(host, null, null, progressType);
	}

	public LoadingTask(LoadingTaskHost host, String onSuccessMessage, String onFailureMessage) {
		this(host, onSuccessMessage, onFailureMessage, ProgressType.Infinite);
	}
	
	public LoadingTask(LoadingTaskHost host, String onSuccessMessage, String onFailureMessage, ProgressType progressType) {
		this.onSuccessMessage = onSuccessMessage;
		this.onFailureMessage = onFailureMessage;
		this.host = host;
		this.progressType = progressType;
	}
	
	@Override
	final public Exception onBackground() {
		try {
			if (progressType == ProgressType.Infinite) host.showLoadingStart();
			result = onLoading();
			if (progressType == ProgressType.Infinite) host.showLoadingEnd();
			return null;
		} catch (Exception e) {
			host.showLoadingEnd(onFailureMessage!=null?null:e);
			Log.e(LOGTAG, "Error processing background task", e);
			return e;
		}
	}

	@Override
	final public void onSuccess(final Exception exception) {
		if (exception == null) {
			onLoadingSuccess(result);
			if (onSuccessMessage!=null) host.showLoadingSuccess(onSuccessMessage);
		} else {
			if (onFailureMessage!=null) {
				String exceptionMessage = exception.getMessage() == null ? "null" : exception.getMessage();
				String msg = onFailureMessage.replace("{error}", exceptionMessage);
				host.showLoadingAlert("Error", msg, new SimpleCallback(){
					@Override
					public void onResult() {
						onFailure(exception);
					}
				});
			} else onFailure(exception);
		}
		onFinally();
	}
	
	public abstract T onLoading() throws Exception;
	public abstract void onLoadingSuccess(T result);
	public void onFailure(Exception e) {}
	public void onFinally() {}
	
}
