package fts.events;

import fts.core.Widget;
import fts.core.Widget.State;

public interface OnStateListener {
	public abstract void onStateChanged(Widget widget, State state, boolean value);
}
