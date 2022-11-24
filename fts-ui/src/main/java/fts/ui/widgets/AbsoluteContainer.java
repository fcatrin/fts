package fts.ui.widgets;

import fts.ui.Container;
import fts.ui.LayoutInfo;
import fts.ui.NativeWindow;
import fts.ui.Widget;
import fts.ui.graphics.Align;
import fts.ui.graphics.Align.HAlign;
import fts.ui.graphics.Align.VAlign;
import fts.ui.graphics.Point;

public class AbsoluteContainer extends Container {

	public AbsoluteContainer(NativeWindow w) {
		super(w);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void layout() {
		int baseLeft = padding.left + bounds.x;
		int baseTop  = padding.top  + bounds.y;
		
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			Align containerAlign = child.getContainerAlign();
			
			int left   = baseLeft + layoutInfo.x;
			int top    = baseTop  + layoutInfo.y;
			int width  = layoutInfo.measuredWidth  + layoutInfo.margins.left + layoutInfo.margins.right;
			int height = layoutInfo.measuredHeight + layoutInfo.margins.top  + layoutInfo.margins.bottom;
			
			HAlign hAlign = containerAlign.h;
			if (hAlign == HAlign.Undefined) hAlign = getAlign().h;

			VAlign vAlign = containerAlign.v;
			if (vAlign == VAlign.Undefined) vAlign = getAlign().v;

			switch(hAlign) {
			case Center : left += (getInternalWidth() - width ) / 2; break;
			case Right  : left += (getInternalWidth() - width); break;
			case Undefined : break;
			}
			
			switch(vAlign) {
			case Center : top += (getInternalHeight() - height) / 2; break;
			case Bottom : top += (getInternalHeight() - height); break;
			case Undefined : break;
			}
			
			child.setBounds(left + layoutInfo.margins.left, top + layoutInfo.margins.top, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
			child.layout();
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		int contentWidth = 0;
		int contentHeight = 0;
		
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			Point marginSize = layoutInfo.getMarginSize();
			if (layoutInfo.width == LayoutInfo.MATCH_PARENT) {
				contentWidth = width;
				break;
			} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
				Point childSize = child.getContentSize(width - marginSize.x - layoutInfo.x, height - marginSize.y - layoutInfo.y);
				contentWidth = Math.max(contentWidth, childSize.x);
			} else {
				contentWidth = Math.max(contentWidth, layoutInfo.width + layoutInfo.x + marginSize.x);
			}
		}
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			Point marginSize = layoutInfo.getMarginSize();
			if (layoutInfo.height == LayoutInfo.MATCH_PARENT) {
				contentHeight = height;
				break;
			} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
				Point childSize = child.getContentSize(contentWidth, height - marginSize.y - layoutInfo.y);
				contentHeight = Math.max(contentHeight, childSize.y);
			} else if (layoutInfo.height > 0) {
				contentHeight = Math.max(contentHeight, layoutInfo.height + marginSize.y + layoutInfo.y);
			}
		}
		Point paddingSize = getPaddingSize();
		return new Point(contentWidth + paddingSize.x, contentHeight + paddingSize.y);
	}

	@Override
	public void onMeasureChildren(MeasureSpec wspec, MeasureSpec hspec) {
		Point paddingSize = getPaddingSize();
		int width = wspec.value - paddingSize.x;
		int height = hspec.value - paddingSize.y;
		if (width <= 0 || height <= 0) return;
		
		for (Widget child : getChildren()) {
			if (child.getVisibility() == Visibility.Gone) continue;
			child.onMeasure(width , height);
		}
	}

}
