package fts.core;

public abstract class ProgressListener {
	public abstract boolean onProgress(int progress, int max);
	public void onStart() {}
	public void onEnd() {}
}
