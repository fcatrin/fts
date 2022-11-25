package fts.ui.widgets;

import fts.ui.Window;
import fts.ui.events.KeyEvent;

public class ImageButtonWidget extends ImageWidget {

	public ImageButtonWidget(Window w) {
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
