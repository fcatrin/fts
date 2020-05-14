package fts.widgets;

import fts.core.Container;
import fts.core.LayoutInfo;
import fts.core.Widget;
import fts.core.Window;
import fts.graphics.Point;

public class AbsoluteContainer extends Container {

	public AbsoluteContainer(Window w) {
		super(w);
	}

	@Override
	public void layout() {
		for (Widget child : getChildren()) {
			LayoutInfo layoutInfo = child.getLayoutInfo();
			child.setBounds(layoutInfo.x, layoutInfo.y, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
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
