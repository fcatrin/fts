package fts.ui.events;

import fts.ui.Widget;
import fts.ui.Widget.State;

public interface OnStateListener {
	public abstract void onStateChanged(Widget widget, State state, boolean value);
}
