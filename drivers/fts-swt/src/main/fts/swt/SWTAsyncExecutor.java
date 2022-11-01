package fts.swt;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import fts.core.AsyncExecutor;

public class SWTAsyncExecutor implements AsyncExecutor {
	
	private Display display;

	public SWTAsyncExecutor(Display display) {
		this.display = display;
	}
	
	@Override
	public void asyncExec(Runnable runnable) {
		try {
			display.asyncExec(runnable);
		} catch (SWTException e) {
			e.printStackTrace();
			throw new RuntimeException("Error on asyncExec", e);
		}
	}

}
