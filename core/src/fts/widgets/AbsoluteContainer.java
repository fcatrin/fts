package fts.widgets;

import fts.core.Container;
import fts.core.LayoutInfo;
import fts.core.Widget;
import fts.core.NativeWindow;
import fts.graphics.Align;
import fts.graphics.Point;

public class AbsoluteContainer extends Container {

	public AbsoluteContainer(NativeWindow w) {
		super(w);
	}

	@Override
	public void layout() {
		Point paddingSize = getPaddingSize();
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			Align containerAlign = child.getContainerAlign();
			
			int left   = layoutInfo.x;
			int top    = layoutInfo.y;
			int width  = layoutInfo.measuredWidth;
			int height = layoutInfo.measuredHeight;
			
			switch(containerAlign.h) {
			case Center : left = (bounds.width - paddingSize.x - width ) / 2; break;
			case Left   : left = padding.left; break;
			case Right  : left = (bounds.width - padding.right - width); break;
			case Undefined : break;
			}
			
			switch(containerAlign.v) {
			case Center : top = (bounds.height - paddingSize.y - height) / 2; break;
			case Top    : top = padding.top;
			case Bottom : top = bounds.height - padding.bottom - height;
			case Undefined : break;
			}
			
			child.setBounds(left, top, width, height);
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
		for (Widget child : getChildren()) {
			child.onMeasure(wspec.value, hspec.value);
		}
	}

}
