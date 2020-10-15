package fts.android;

import android.os.AsyncTask;
import android.util.Log;

public abstract class BackgroundTask<T> extends AsyncTask<Void, Void, Exception>{
	private static final String LOGTAG = BackgroundTask.class.getSimpleName();
	
	T result = null;

	public BackgroundTask() {}
	
	@Override
	final protected Exception doInBackground(Void... params) {
		try {
			result = onBackground();
			return null;
		} catch (Exception e) {
			Log.e(LOGTAG, "Error processing background task", e);
			return e;
		}
	}

	@Override
	final protected void onPostExecute(final Exception exception) {
		if (exception == null) {
			onSuccess(result);
		} else {
			onFailure(exception);
		}
		onFinally();
	}
	
	public abstract T onBackground() throws Exception;
	public abstract void onSuccess(T result);
	public void onFailure(Exception e) {}
	public void onFinally() {}
	
}
