package fts.ui.widgets;

import fts.ui.Window;
import fts.ui.events.KeyEvent;
import fts.ui.graphics.Align;
import fts.ui.graphics.Align.HAlign;
import fts.ui.graphics.Align.VAlign;

public class ButtonWidget extends TextWidget {

	public ButtonWidget(Window w) {
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
