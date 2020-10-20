package fts.events;

import fts.core.Widget;

public interface OnProgressChangedListener {
	public abstract void onProgressChanged(Widget w, long progress, long total, boolean userTriggered, boolean tracking);
}
