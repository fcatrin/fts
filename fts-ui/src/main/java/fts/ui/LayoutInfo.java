package fts.ui;

import fts.ui.graphics.Point;
import fts.ui.graphics.Sides;

public class LayoutInfo {
	public static final int MATCH_PARENT = -1;
	public static final int WRAP_CONTENT = -2;
	
	public int x;
	public int y;
	public int width;
	public int height;
	public int weight;
	
	public int measuredWidth;
	public int measuredHeight;
	
	public Sides margins = new Sides();
	public Point getMarginSize() {
		return new Point(
			margins.left + margins.right,
			margins.top + margins.bottom);
	}

	@Override
	public String toString() {
		return String.format("LayoutInfo (%d, %d) - (%s, %s) w:%d mw:%d mh:%d",
				x, y,
				width == MATCH_PARENT ? "MATCH" : (width == WRAP_CONTENT ? "WRAP" : width),
				height == MATCH_PARENT ? "MATCH" : (height == WRAP_CONTENT ? "WRAP" : height),
				weight,
				measuredWidth, measuredHeight);
	}
}
