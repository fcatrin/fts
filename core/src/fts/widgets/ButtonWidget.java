package fts.widgets;

import fts.core.NativeWindow;

public class ButtonWidget extends TextWidget {

	public ButtonWidget(NativeWindow w) {
		super(w);
		setClickable(true);
		setPressed(true);
	}

}
