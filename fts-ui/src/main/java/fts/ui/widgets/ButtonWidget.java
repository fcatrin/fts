package fts.widgets;

import fts.core.NativeWindow;
import fts.events.KeyEvent;
import fts.graphics.Align;
import fts.graphics.Align.HAlign;
import fts.graphics.Align.VAlign;

public class ButtonWidget extends TextWidget {

	public ButtonWidget(NativeWindow w) {
		super(w);
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		Align center = new Align();
		center.h = HAlign.Center;
		center.v = VAlign.Center;
		setAlign(center);
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
