package fts.android.core;

import android.util.Log;

import fts.core.Logger;

public class AndroidLogger implements Logger {

	@Override
	public void d(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(tag, message);
	}

	@Override
	public void e(String tag, String message, Throwable t) {
		Log.e(tag, message);
	}

}
