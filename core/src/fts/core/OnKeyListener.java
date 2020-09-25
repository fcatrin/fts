package fts.core;

import fts.events.KeyEvent;

public interface OnKeyListener {
	public boolean onKeyDown(KeyEvent event);
	public boolean onKeyUp(KeyEvent event);
}
