package fts.views;

import fts.core.Window;

public class LinearLayout extends ViewGroup {
	enum Orientation {Vertical, Horizontal};
	
	private Orientation orientation = Orientation.Vertical;

	public LinearLayout(Window w) {
		super(w);
	}
	
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("orientation")) {
			if (value.equals("vertical")) return Orientation.Vertical;
			if (value.equals("horizontal")) return Orientation.Horizontal;
			throw new RuntimeException("Invalida value " + value + " for property " + propertyName);
		}
		return super.resolvePropertyValue(propertyName, value);
	}

	@Override
	public void layout() {
		if (orientation == Orientation.Vertical) {
			layoutVertical();
		}
		if (orientation == Orientation.Horizontal) {
			layoutHorizontal();
		}
		
	}

	private void layoutHorizontal() {
	}

	private void layoutVertical() {
		int x = padding.x;
		int y = padding.y;
		for (View child : children) {
			child.setBounds(x, y, child.measuredWidth, child.measuredHeight);
			y += child.measuredHeight;
		}
	}

}
