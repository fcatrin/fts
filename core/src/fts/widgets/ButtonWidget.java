package fts.widgets;

import fts.core.NativeWindow;
import fts.events.KeyEvent;

public class ButtonWidget extends TextWidget {

	public ButtonWidget(NativeWindow w) {
		super(w);
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		if (keyEvent.keyCode == KeyEvent.KEY_ENTER) {
			fireClick();
			return true;
		}
		return super.onKeyUp(keyEvent);
	}

}
