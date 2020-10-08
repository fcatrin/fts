package fts.widgets;

import java.util.ArrayList;
import java.util.List;

import fts.core.Container;
import fts.core.LayoutInfo;
import fts.core.Widget;
import fts.core.NativeWindow;
import fts.graphics.Align.HAlign;
import fts.graphics.Align.VAlign;
import fts.graphics.Point;

public class LinearContainer extends Container {
	enum Orientation {Vertical, Horizontal};
	
	private Orientation orientation = Orientation.Vertical;

	public LinearContainer(NativeWindow w) {
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

	private int getChildrenWidth() {
		int totalWidth = 0;
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			LayoutInfo wl = widget.getLayoutInfo();
			totalWidth += wl.margins.left + wl.margins.right + wl.measuredWidth;
		}
		return totalWidth;
	}

	private int getChildrenMaxWidth() {
		int maxWidth = 0;
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			LayoutInfo wl = widget.getLayoutInfo();
			int width = wl.margins.left + wl.margins.right + wl.measuredWidth;
			maxWidth = Math.max(maxWidth, width);
		}
		return maxWidth;
	}

	private int getChildrenHeight() {
		int totalHeight = 0;
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			LayoutInfo wl = widget.getLayoutInfo();
			totalHeight += wl.margins.top + wl.margins.bottom + wl.measuredHeight;
		}
		return totalHeight;
	}

	private int getChildrenMaxHeight() {
		int maxHeight = 0;
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			LayoutInfo wl = widget.getLayoutInfo();
			int height = wl.margins.top + wl.margins.bottom + wl.measuredHeight;
			maxHeight = Math.max(maxHeight, height);
		}
		return maxHeight;
	}

	private void layoutHorizontal() {
		int left = padding.left + bounds.x;
		int top  = padding.top  + bounds.y;
		int containerTop  = top;

		HAlign hAlign = getAlign().h;
		if (hAlign == HAlign.Center) {
			left += (getInternalWidth() - getChildrenWidth()) / 2;
		} else if (hAlign == HAlign.Right) {
			left += (getInternalWidth() - getChildrenWidth());
		}
		
		VAlign vAlign = getAlign().v;		
		if (vAlign == VAlign.Center) {
			top += (getInternalHeight() - getChildrenMaxHeight()) / 2;
		} else if (vAlign == VAlign.Bottom) {
			top += (getInternalHeight() - getChildrenMaxHeight());
		}

		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			
			LayoutInfo layoutInfo = widget.getLayoutInfo();

			int width  = layoutInfo.measuredWidth  + layoutInfo.margins.left + layoutInfo.margins.right;
			int height = layoutInfo.measuredHeight + layoutInfo.margins.top  + layoutInfo.margins.bottom;
			
			switch (widget.getContainerAlign().v) {
			case Center : top = containerTop + (getInternalHeight() - height) / 2; break;
			case Bottom : top = containerTop + (getInternalHeight() - height); break;
			default     : break;
			}
			
			left += layoutInfo.margins.left;
			widget.setBounds(left, top + layoutInfo.margins.top, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			left += width + layoutInfo.margins.right;
			
			widget.layout();
		}
	}

	private void layoutVertical() {
		int left = padding.left + bounds.x;
		int top  = padding.top  + bounds.y;
		int containerLeft = left;

		HAlign hAlign = getAlign().h;
		if (hAlign == HAlign.Center) {
			left += (getInternalWidth() - getChildrenMaxWidth()) / 2;
		} else if (hAlign == HAlign.Right) {
			left += (getInternalWidth() - getChildrenMaxWidth());
		}
		
		VAlign vAlign = getAlign().v;		
		if (vAlign == VAlign.Center) {
			top += (getInternalHeight() - getChildrenHeight()) / 2;
		} else if (vAlign == VAlign.Bottom) {
			top += (getInternalHeight() - getChildrenHeight());
		}

		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;

			LayoutInfo layoutInfo = widget.getLayoutInfo();
			int width  = layoutInfo.measuredWidth  + layoutInfo.margins.left + layoutInfo.margins.right;
			int height = layoutInfo.measuredHeight + layoutInfo.margins.top  + layoutInfo.margins.bottom;
			
			switch (widget.getContainerAlign().h) {
			case Center : left = containerLeft + (getInternalWidth() - width) / 2; break;
			case Right  : left = containerLeft + (getInternalWidth() - width); break;
			default     : break;
			}
			
			top += layoutInfo.margins.top;
			widget.setBounds(left + layoutInfo.margins.left, top, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			top += height + layoutInfo.margins.bottom;
			
			widget.layout();
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		int contentWidth = 0;
		int contentHeight = 0;
		Point paddingSize = getPaddingSize();
		int availableWidth  = width  - paddingSize.x;
		int availableHeight = height - paddingSize.y;

		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			layoutInfo.weight = layoutInfo.weight < 1 ? 1 : layoutInfo.weight;
		}
		
		if (orientation == Orientation.Vertical) {
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT || layoutInfo.width == 0) {
					contentWidth = width;
					break;
				} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(availableWidth, availableHeight);
					contentWidth = Math.max(contentWidth, childSize.x + marginSize.x);
				} else {
					contentWidth = Math.max(contentWidth, layoutInfo.width + marginSize.x);
				}
			}
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					contentHeight = height;
					break;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(contentWidth, availableHeight); // TODO not all height is available
					contentHeight += childSize.y + marginSize.y;
				} else if (layoutInfo.height > 0) {
					contentHeight += layoutInfo.height + marginSize.y;
				}
			}			
		} else {
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
					size.x = width - paddingSize.x;
					contentWidth = width - paddingSize.x;
					availableWidth = 0;
				} else if (layoutInfo.width > 0) {
					int childWidth = layoutInfo.width + layoutInfo.margins.left + layoutInfo.margins.right; 
					size.x = childWidth;
					contentWidth += childWidth;
					availableWidth -= childWidth;
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
					childSize.x += layoutInfo.margins.left + layoutInfo.margins.right;
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
				int marginSize = layoutInfo.margins.top + layoutInfo.margins.bottom;
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT || layoutInfo.height == 0) {
					contentHeight = height;
					break;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(size.x, height);
					contentHeight = Math.max(contentHeight, childSize.y + marginSize);
				} else if (layoutInfo.height>0) {
					contentHeight = Math.max(contentHeight, layoutInfo.height + marginSize);
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

		Point paddingSize = getPaddingSize();

		if (orientation == Orientation.Vertical) {
			int totalWeight = 0;
			int height = 0;
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				child.onMeasure(wspec.value - paddingSize.x - marginSize.x, hspec.value - paddingSize.y - marginSize.y);
				
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					height += hspec.value - paddingSize.y - marginSize.y;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					height += layoutInfo.measuredHeight + marginSize.y;
				} else if (layoutInfo.height > 0) {
					height += layoutInfo.height + marginSize.y;
				} else if (layoutInfo.height == 0) {
					totalWeight += layoutInfo.weight;
				}
			}
			
			int availableHeight = hspec.value - height - paddingSize.y;
			if (availableHeight <= 0) availableHeight = 0;
			
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.height == 0) {
					int weight = layoutInfo.weight;
					layoutInfo.measuredHeight = (availableHeight / totalWeight) * weight - marginSize.y;
				}
			}
		} else {
			int totalWeight = 0;
			int width = 0;
			
			// first processs all fixed size children
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
					child.onMeasure(wspec.value - paddingSize.x - marginSize.x, hspec.value - paddingSize.y - marginSize.y);
					width += wspec.value - paddingSize.x;
				} else 	if (layoutInfo.width > 0) {
					child.onMeasure(layoutInfo.width, hspec.value - marginSize.y - paddingSize.y);
					width += layoutInfo.width + marginSize.x;
				} else if (layoutInfo.width == 0) {
					totalWeight += layoutInfo.weight;
				}
			}
			
			int availableWidth = wspec.value - width - paddingSize.x;
			availableWidth = Math.max(0,  availableWidth);
			
			// now process all wrap_content with the available space
			for (Widget child : getChildren()) {
				if (availableWidth < 0) availableWidth = 0;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					child.onMeasure(availableWidth - marginSize.x, hspec.value - marginSize.y);
					width += layoutInfo.measuredWidth + marginSize.x;
					availableWidth -= layoutInfo.measuredWidth + marginSize.x;
				}
			}

			if (availableWidth < 0) availableWidth = 0;
			
			for (Widget child : getChildren()) {
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.width == 0) {
					int childWidth = (availableWidth / totalWeight) * layoutInfo.weight;
					child.onMeasure(childWidth - marginSize.x, hspec.value - marginSize.y);
				}
			}
		}
	}

}
