package fts.ui.events;

import fts.ui.Widget;

public interface OnProgressChangedListener {
	public abstract void onProgressChanged(Widget w, long progress, long total, boolean userTriggered, boolean tracking);
}
