package fts.widgets;

import fts.core.Window;

public class ButtonWidget extends TextWidget {

	public ButtonWidget(Window w) {
		super(w);
		setClickable(true);
		setPressed(true);
	}

}
