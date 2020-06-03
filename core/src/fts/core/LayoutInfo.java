package fts.core;

import fts.graphics.Point;
import fts.graphics.Sides;

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
}
