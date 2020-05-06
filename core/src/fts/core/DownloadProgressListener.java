package fts.core;

public abstract class DownloadProgressListener {
	public abstract boolean updateProgress(int progress, int max);
	public void onDownloadStart() {}
	public void onDownloadEnd() {}
	public int getBufferSize(int max) {return 0;}
}
