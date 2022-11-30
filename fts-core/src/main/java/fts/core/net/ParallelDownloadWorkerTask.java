package fts.core.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fts.core.BackgroundTask;

@SuppressWarnings("unused")
public abstract class ParallelDownloadWorkerTask extends BackgroundTask<byte[]> {
	private static final String LOGTAG = ParallelDownloadWorkerTask.class.getSimpleName();
	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(16);
	private final String location;

	public ParallelDownloadWorkerTask(String location) {
		this.location = location;
	}

	@Override
	public byte[] onBackground() throws Exception {
		return NetworkUtils.httpGet(location);
	}

	@Override
	public void execute() {
		throw new RuntimeException("Not Implemented. Check retroxlibs/core/net");
		// AppContext.asyncExecutor.execute(this, EXECUTOR);
	}
}
