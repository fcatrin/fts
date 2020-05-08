package fts.gl;

import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Point;

public abstract class Widget extends fts.core.Widget {

	public Widget(Window w) {
		super(w);
	}

	@Override
	public void redraw() {
	}

	@Override
	protected abstract void onPaint(PaintEvent e);
	
	@Override
	public abstract Point computeSize(int x, int y);

}
