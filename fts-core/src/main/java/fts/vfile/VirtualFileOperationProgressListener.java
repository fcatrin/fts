package fts.vfile;

public abstract class VirtualFileOperationProgressListener {
	private boolean isCancelled = false;
	
	public abstract void updateProgress(long progress, long max) throws VirtualFileOperationCancelledException;
	public void updateProgress(String info, long progress, long max) throws VirtualFileOperationCancelledException {};
	public void onStart() {}
	public void onEnd() {}
	
	public boolean isCancelled() {
		return isCancelled;
	}
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
}
