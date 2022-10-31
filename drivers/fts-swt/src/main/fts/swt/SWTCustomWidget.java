package fts.swt;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import fts.graphics.Point;

public class SWTCustomWidget extends Canvas {

	public SWTCustomWidget(Composite parent, int style) {
		super(parent, style);
	}

	public Point getTextSize(String s) {
		GC gc = new GC(getDisplay());
		org.eclipse.swt.graphics.Point extents = gc.stringExtent(s);
		return new Point(extents.x, extents.y);
	}

}
