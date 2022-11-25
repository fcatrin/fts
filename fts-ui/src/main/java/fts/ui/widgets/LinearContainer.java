package fts.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import fts.ui.Container;
import fts.ui.LayoutInfo;
import fts.ui.Window;
import fts.ui.Widget;
import fts.ui.graphics.Align.HAlign;
import fts.ui.graphics.Align.VAlign;
import fts.ui.graphics.Point;

public class LinearContainer extends Container {
	enum Orientation {Vertical, Horizontal};
	
	private Orientation orientation = Orientation.Vertical;
	int separator = 0;

	public LinearContainer(Window w) {
		super(w);
	}
	
	public void setSeparator(int separator) {
		this.separator = separator;
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
		bindFocusWidgets();
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

	private int getChildrenHeight() {
		int totalHeight = 0;
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			LayoutInfo wl = widget.getLayoutInfo();
			totalHeight += wl.margins.top + wl.margins.bottom + wl.measuredHeight;
		}
		return totalHeight;
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
		
		for (Widget widget : getChildren()) {
			if (widget.getVisibility() == Visibility.Gone) continue;
			
			LayoutInfo layoutInfo = widget.getLayoutInfo();
			int height = layoutInfo.measuredHeight + layoutInfo.margins.top  + layoutInfo.margins.bottom;
			
			VAlign vAlign = getAlign().v;
			VAlign vAlignWidget = widget.getContainerAlign().v;
			if (vAlignWidget != VAlign.Undefined) vAlign = vAlignWidget;
			
			switch (vAlign) {
			case Center : top = containerTop + (getInternalHeight() - height) / 2; break;
			case Bottom : top = containerTop + (getInternalHeight() - height); break;
			default     : break;
			}
			
			left += layoutInfo.margins.left;
			widget.setBounds(left, top + layoutInfo.margins.top, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			left += layoutInfo.measuredWidth + layoutInfo.margins.right + separator;
			
			widget.layout();
		}
	}

	private void layoutVertical() {
		int left = padding.left + bounds.x;
		int top  = padding.top  + bounds.y;
		int containerLeft = left;

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
			
			HAlign hAlign = getAlign().h;
			HAlign hAlignWidget = widget.getContainerAlign().h; 
			if (hAlignWidget != HAlign.Undefined) hAlign = hAlignWidget;
			
			switch (hAlign) {
			case Center : left = containerLeft + (getInternalWidth() - width) / 2; break;
			case Right  : left = containerLeft + (getInternalWidth() - width); break;
			default     : break;
			}
			
			top += layoutInfo.margins.top;
			widget.setBounds(left + layoutInfo.margins.left, top, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			top += layoutInfo.measuredHeight + layoutInfo.margins.bottom + separator;

			widget.layout();
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		int contentWidth  = 0;
		int contentHeight = 0;

		int separatorWidth  = orientation == Orientation.Vertical   ? 0 : getSeparatorSize();
		int separatorHeight = orientation == Orientation.Horizontal ? 0 : getSeparatorSize();
		
		Point paddingSize = getPaddingSize();
		int availableWidth  = width  - paddingSize.x - separatorWidth;
		int availableHeight = height - paddingSize.y - separatorHeight;

		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			layoutInfo.weight = layoutInfo.weight < 1 ? 1 : layoutInfo.weight;
		}
		
		if (orientation == Orientation.Vertical) {
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT || layoutInfo.width == 0) {
					contentWidth = availableWidth;
					break;
				} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(availableWidth, availableHeight);
					contentWidth = Math.max(contentWidth, childSize.x + marginSize.x);
				} else {
					contentWidth = Math.max(contentWidth, layoutInfo.width + marginSize.x);
				}
			}
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					contentHeight = availableHeight;
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
				if (child.getVisibility() == Visibility.Gone) continue;
				
				Point size = sizeInfo.get(i++);
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
					size.x = availableWidth;
					contentWidth = availableWidth;
					availableWidth = 0;
				} else if (layoutInfo.width > 0) {
					int childWidth = layoutInfo.width + layoutInfo.margins.left + layoutInfo.margins.right; 
					size.x = childWidth;
					contentWidth   += childWidth;
					availableWidth -= childWidth;
				} else if (layoutInfo.width == 0) {
					totalWeight += layoutInfo.weight;
				}
			}
			
			// now check wrap content elements
			i=0;
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				if (availableWidth <= 0) break;

				Point size = sizeInfo.get(i++);
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(availableWidth, availableHeight);
					childSize.x += layoutInfo.margins.left + layoutInfo.margins.right;
					size.x = childSize.x;
					
					contentWidth   += childSize.x;
					availableWidth -= childSize.x;
				}
			}			
			
			availableWidth = Math.max(0,  availableWidth);

			// use max width if there is any proportional width
			i=0;
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				Point size = sizeInfo.get(i++);
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == 0) {
					contentWidth = availableWidth;
					size.x = availableWidth * layoutInfo.weight / totalWeight;
				}				
			}

			// now check height 
			LayoutInfo layoutInfo = getLayoutInfo();

			i=0;
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				Point size = sizeInfo.get(i++);
				LayoutInfo childLayoutInfo = child.getLayoutInfo();
				int marginSize = childLayoutInfo.margins.top + childLayoutInfo.margins.bottom;
				if ((childLayoutInfo.height == LayoutInfo.MATCH_PARENT || childLayoutInfo.height == 0) && layoutInfo.height != LayoutInfo.WRAP_CONTENT) {
					// if this container has wrap content as height, don't consider childs matching parent
					contentHeight = availableHeight;
					break;
				} else if (childLayoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					Point childSize = child.getContentSize(size.x, availableHeight);
					contentHeight = Math.max(contentHeight, childSize.y + marginSize);
				} else if (childLayoutInfo.height>0) {
					contentHeight = Math.max(contentHeight, childLayoutInfo.height + marginSize);
				}
			}			
		}
		return new Point(contentWidth + paddingSize.x + separatorWidth, contentHeight + paddingSize.y + separatorHeight);
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
			int availableHeight = hspec.value - paddingSize.y;
			int autoHeight = 0;
			int totalWeight = 0;
			
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				child.onMeasure(wspec.value - paddingSize.x, availableHeight);
				
				if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
					availableHeight = 0;
				} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
					availableHeight -= layoutInfo.measuredHeight + marginSize.y - separator;
				} else if (layoutInfo.height > 0) {
					availableHeight -= layoutInfo.height + marginSize.y - separator;
				} else if (layoutInfo.height == 0) {
					totalWeight += layoutInfo.weight;
					autoHeight++;
				}
				availableHeight = Math.max(0,  availableHeight);
			}
			
			if (autoHeight > 1) {
				availableHeight -= separator * (autoHeight -1);
				availableHeight = Math.max(0,  availableHeight);
			}
			
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				Point marginSize = layoutInfo.getMarginSize();
				if (layoutInfo.height == 0) {
					int weight = layoutInfo.weight;
					layoutInfo.measuredHeight = (availableHeight / totalWeight) * weight - marginSize.y;
				}
			}
		} else {
			int availableWidth  = wspec.value - paddingSize.x;
			int availableHeight = hspec.value - paddingSize.y;
			int totalWeight = 0;
			int autoWidth = 0;
			
			// first processs all fixed size children
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo childLayoutInfo = child.getLayoutInfo();
				Point marginSize = childLayoutInfo.getMarginSize();
				
				if (childLayoutInfo.width == LayoutInfo.MATCH_PARENT) {
					child.onMeasure(availableWidth, availableHeight);
					availableWidth = 0;
				} else 	if (childLayoutInfo.width > 0) {
					child.onMeasure(childLayoutInfo.width + marginSize.x, availableHeight);
					availableWidth -= childLayoutInfo.width + marginSize.x - separator;
				} else if (childLayoutInfo.width == 0) {
					totalWeight += childLayoutInfo.weight;
					autoWidth++;
				}
				
				availableWidth = Math.max(0,  availableWidth);
			}
			
			// now process all wrap_content with the available space
			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;

				LayoutInfo childLayoutInfo = child.getLayoutInfo();
				if (childLayoutInfo.width != LayoutInfo.WRAP_CONTENT) continue;
				
				Point marginSize = childLayoutInfo.getMarginSize();
				child.onMeasure(availableWidth, availableHeight);
				availableWidth -= childLayoutInfo.measuredWidth + marginSize.x + separator;
				
				availableWidth = Math.max(0,  availableWidth);
			}
			
			if (autoWidth > 1) {
				availableWidth -= separator * (autoWidth -1);
				availableWidth = Math.max(0,  availableWidth);
			}

			for (Widget child : getChildren()) {
				if (child.getVisibility() == Visibility.Gone) continue;
				
				LayoutInfo layoutInfo = child.getLayoutInfo();
				if (layoutInfo.width == 0) {
					int childWidth = (availableWidth / totalWeight) * layoutInfo.weight;
					child.onMeasure(childWidth, availableHeight);
				}
			}
		}
	}
	
	private int getSeparatorSize() {
		if (separator == 0) return 0;
		
		int childrenCount = 0;
		for (Widget child : getChildren()) {
			if (child.getVisibility() == Visibility.Gone) continue;
			childrenCount++;
		}
		
		if (childrenCount <= 1) return 0;
		return separator * (childrenCount - 1);
	}
	
	private void bindFocusWidgets() {
		for (Widget child : getChildren()) {
			child.setWidgetFocusLeft(null);
			child.setWidgetFocusRight(null);
			child.setWidgetFocusUp(null);
			child.setWidgetFocusDown(null);
		}

		// build list of visible children
		List<Widget> visible = new ArrayList<Widget>();
		for(Widget child : getChildren()) {
			if (child.getVisibility() == Visibility.Visible) visible.add(child);
		}

		if (orientation == Orientation.Vertical) {
			for(int i=0; i<visible.size(); i++) {
				Widget child = visible.get(i);
				if (i>0) {
					child.setWidgetFocusUp(visible.get(i-1));
				}
				if (i<visible.size()-1) {
					child.setWidgetFocusDown(visible.get(i+1));
				}
			}
		}
		
		if (orientation == Orientation.Horizontal) {
			for(int i=0; i<visible.size(); i++) {
				Widget child = visible.get(i);
				if (i>0) {
					child.setWidgetFocusLeft(visible.get(i-1));
				}
				if (i<visible.size()-1) {
					child.setWidgetFocusRight(visible.get(i+1));
				}
			}
		}

	}

}
