package fts.ui;

import java.util.ArrayList;
import java.util.List;

import fts.core.AsyncExecutor;
import fts.core.BackgroundProcessor;
import fts.core.BackgroundTask;

@SuppressWarnings("rawtypes")
public class CoreBackgroundProcessor extends Thread implements BackgroundProcessor {
	List<BackgroundTask> tasks = new ArrayList<BackgroundTask>();
	boolean isRunning = true;
	private AsyncExecutor executor;
	
	public CoreBackgroundProcessor(AsyncExecutor executor) {
		this.executor = executor;
	}
	
	public <T> void exec(BackgroundTask<T> task) {
		synchronized (tasks) {
			tasks.add(task);
			tasks.notify();
		}
	}
	
	@Override
	public void run() {
		while (isRunning) {
			synchronized(tasks) {
				if (tasks.size() == 0)
					try {
						tasks.wait();
					} catch (InterruptedException e) {
						break;
					}
				if (isRunning && tasks.size() >= 0) {
					final BackgroundTask task = tasks.get(0);
					tasks.remove(0);

					try {
						final Object result = task.onBackground();
						executor.asyncExec(new Runnable() {
							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								try {
									task.onSuccess(result);
									task.onFinally();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});

					} catch (Exception e) {
						final Exception fe = e;
						executor.asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									task.onFailure(fe);
									task.onFinally();
								} catch (Exception ne) {
									ne.printStackTrace();
								}
							}
						});
					}
				}
			}
		}
	}

	@Override
	public void shutdown() {
		synchronized(tasks) {
			isRunning = false;
			interrupt();
		}
	}
}
