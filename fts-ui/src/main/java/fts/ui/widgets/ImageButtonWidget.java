package fts.ui.widgets;

import fts.ui.NativeWindow;
import fts.ui.events.KeyEvent;

public class ImageButtonWidget extends ImageWidget {

	public ImageButtonWidget(NativeWindow w) {
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
