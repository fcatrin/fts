package fts.widgets;

import fts.core.Container;
import fts.core.LayoutInfo;
import fts.core.Widget;
import fts.core.Widget.Visibility;
import fts.core.NativeWindow;
import fts.graphics.Align;
import fts.graphics.Align.HAlign;
import fts.graphics.Align.VAlign;
import fts.graphics.Point;

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
				contentHeight = Math.max(contentHeight, childSize.y);
			} else if (layoutInfo.height > 0) {
				contentHeight = Math.max(contentHeight, layoutInfo.height);
			}
		}			
		return new Point(contentWidth, contentHeight);
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
