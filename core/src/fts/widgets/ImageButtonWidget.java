package fts.widgets;

import fts.core.NativeWindow;
import fts.events.KeyEvent;

public class ImageButtonWidget extends TextWidget {

	public ImageButtonWidget(NativeWindow w) {
		super(w);
		setClickable(true);
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
