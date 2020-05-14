package fts.widgets;

import fts.core.Container;
import fts.core.LayoutInfo;
import fts.core.Widget;
import fts.core.Window;
import fts.graphics.Point;

public class LinearContainer extends Container {
	enum Orientation {Vertical, Horizontal};
	
	private Orientation orientation = Orientation.Vertical;

	public LinearContainer(Window w) {
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
			throw new RuntimeException("Invalid value " + value + " for property " + propertyName);
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
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			child.setBounds(x, y, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			y += layoutInfo.measuredHeight;
			
			child.layout();
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		int contentWidth = 0;
		int contentHeight = 0;
		
		if (orientation == Orientation.Vertical) {
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
					contentWidth = width;
					break;
				} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(width, height);
					contentWidth = Math.max(contentWidth, childSize.x);
				} else {
					contentWidth = Math.max(contentWidth, layoutInfo.width);
				}
			}
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					contentHeight = height;
					break;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(contentWidth, height);
					contentHeight += childSize.y;
				} else if (layoutInfo.height > 0) {
					contentHeight += layoutInfo.height;
				}
			}			
		}
		return new Point(contentWidth, contentHeight);
	}

	@Override
	public void onMeasureChildren(MeasureSpec wspec, MeasureSpec hspec) {
		if (orientation == Orientation.Vertical) {
			int totalWeight = 0;
			int height = 0;
			for (Widget child : getChildren()) {
				child.onMeasure(wspec.value, hspec.value);
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					height += hspec.value;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					height += layoutInfo.measuredHeight;
				} else if (layoutInfo.height > 0) {
					height += layoutInfo.height;
				} else if (layoutInfo.height == 0) {
					totalWeight += layoutInfo.weight < 1 ? 1 : layoutInfo.weight;
				}
			}
			
			int availableHeight = hspec.value - height;
			if (availableHeight <= 0) availableHeight = 0;
			
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.height == 0) {
					int weight = layoutInfo.weight < 1 ? 1 : layoutInfo.weight;
					layoutInfo.measuredHeight = (availableHeight / totalWeight) * weight;
				}
			}
		}
	}

}
