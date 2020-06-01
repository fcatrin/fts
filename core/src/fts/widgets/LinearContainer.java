package fts.widgets;

import java.util.ArrayList;
import java.util.List;

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
		} else {
			layoutHorizontal();
		}
	}

	private void layoutHorizontal() {
		int x = padding.left + bounds.x;
		int y = padding.top  + bounds.y;
		
		List<Widget> widgets = getChildren();
		for (int i=0; i<widgets.size(); i++) {
			Widget child = widgets.get(i);
			LayoutInfo layoutInfo = child.getLayoutInfo();
			
			int width = layoutInfo.measuredWidth;
			/*
			if (i+1 == widgets.size()) {
				width = bounds.width - padding.left - padding.right;
			}
			*/
			child.setBounds(x, y, width, layoutInfo.measuredHeight);
			x += layoutInfo.measuredWidth;
			
			child.layout();
		}
	}

	private void layoutVertical() {
		int x = padding.left + bounds.x;
		int y = padding.top  + bounds.y;

		List<Widget> widgets = getChildren();
		for (int i=0; i<widgets.size(); i++) {
			Widget child = widgets.get(i);
			LayoutInfo layoutInfo = child.getLayoutInfo();
			
			int height = layoutInfo.measuredHeight;
			if (i+1 == widgets.size()) {
				height = bounds.height - padding.top - padding.bottom;
			}
			
			child.setBounds(x, y, layoutInfo.measuredWidth, height);
			y += layoutInfo.measuredHeight;
			
			child.layout();
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		int contentWidth = 0;
		int contentHeight = 0;
		
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			layoutInfo.weight = layoutInfo.weight < 1 ? 1 : layoutInfo.weight;
		}
		
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
		} else {
			int availableWidth = width;
			int totalWeight = 0;
			
			List<Point> sizeInfo = new ArrayList<Point>();
			for(int i=0; i<getChildren().size(); i++) {
				sizeInfo.add(new Point());
			}
			
			// first check: match parent and fixed width
			int i=0;
			for (Widget child : getChildren()) {
				Point size = sizeInfo.get(i++);
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
					size.x = width;
					contentWidth = width;
					availableWidth = 0;
				} else if (layoutInfo.width > 0) {
					size.x = layoutInfo.width;
					contentWidth = layoutInfo.width;
					availableWidth -= layoutInfo.width;
				} else if (layoutInfo.width == 0) {
					totalWeight += layoutInfo.weight;
				}
			}
			
			// now check wrap content elements
			i=0;
			for (Widget child : getChildren()) {
				if (availableWidth <= 0) break;

				Point size = sizeInfo.get(i++);
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(availableWidth, height);
					size.x = childSize.x;
					
					contentWidth += childSize.x;
					availableWidth -= childSize.x;
				}
			}			
			
			availableWidth = Math.max(0,  availableWidth);

			// use max width if there is any proportional width
			i=0;
			for (Widget child : getChildren()) {
				Point size = sizeInfo.get(i++);
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == 0) {
					contentWidth = width;
					size.x = availableWidth * layoutInfo.weight / totalWeight;
				}				
			}
			
			// now check height 
			i=0;
			for (Widget child : getChildren()) {
				Point size = sizeInfo.get(i++);
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT || layoutInfo.height == 0) {
					contentHeight = height;
					break;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(size.x, height);
					contentHeight = Math.max(contentHeight, childSize.y);
				} else if (layoutInfo.height>0) {
					contentHeight = Math.max(contentHeight, layoutInfo.height);
				}
			}			
		}
		return new Point(contentWidth, contentHeight);
	}

	@Override
	public void onMeasureChildren(MeasureSpec wspec, MeasureSpec hspec) {
		// quickly fix weight
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			layoutInfo.weight = Math.min(1, layoutInfo.weight);
		}
		
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
					totalWeight += layoutInfo.weight;
				}
			}
			
			int availableHeight = hspec.value - height;
			if (availableHeight <= 0) availableHeight = 0;
			
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.height == 0) {
					int weight = layoutInfo.weight;
					layoutInfo.measuredHeight = (availableHeight / totalWeight) * weight;
				}
			}
		} else {
			int totalWeight = 0;
			int width = 0;
			
			// first processs all fixed size children
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
					child.onMeasure(wspec.value, hspec.value);
					width += wspec.value;
				} else 	if (layoutInfo.width > 0) {
					child.onMeasure(layoutInfo.width, hspec.value);
					width += layoutInfo.width;
				} else if (layoutInfo.width == 0) {
					totalWeight += layoutInfo.weight;
				}
			}
			
			int availableWidth = wspec.value - width;
			availableWidth = Math.max(0,  availableWidth);
			
			// now process all wrap_content with the available space
			for (Widget child : getChildren()) {
				if (availableWidth < 0) availableWidth = 0;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					child.onMeasure(availableWidth, hspec.value);
					width += layoutInfo.measuredWidth;
					availableWidth -= layoutInfo.measuredWidth;
				}
			}

			if (availableWidth < 0) availableWidth = 0;
			
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == 0) {
					int childWidth = (availableWidth / totalWeight) * layoutInfo.weight;
					child.onMeasure(childWidth, hspec.value);
				}
			}
		}
	}

}
