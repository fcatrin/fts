package fts.core;

import java.util.ArrayList;
import java.util.List;

public class CoreAsyncExecutor extends AsyncExecutor {
	List<RunnableAt> runnables = new ArrayList<RunnableAt>();
	String lock = "";
	
	@Override
	public void asyncExec(Runnable runnable, long delay) {
		RunnableAt runnableAt = new RunnableAt(runnable, System.currentTimeMillis() + delay);
		runnables.add(runnableAt);
	}
	
	@Override
	public void process() {
		long t0 = System.currentTimeMillis();
		for(int i=0; i<runnables.size(); i++) {
			RunnableAt runnableAt = runnables.get(i);
			if (runnableAt.at > t0) continue;
			
			runnableAt.runnable.run();
			runnables.remove(i);
			i = 0;
		}
	}
	
	private class RunnableAt {
		Runnable runnable;
		long at;
		
		RunnableAt(Runnable runnable, long at) {
			this.runnable = runnable;
			this.at = at;
		}
	}

}
