package fts.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fts.core.LayoutInfo;
import fts.core.ListAdapter;
import fts.core.NativeWindow;
import fts.core.Widget;
import fts.events.PaintEvent;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public class ListWidget<T> extends Widget {
	ListAdapter<T> adapter;
	
	private int itemHeight = -1;
	private int offsetY = -1;
	
	Map<Integer, Widget> knownWidgets = new HashMap<Integer, Widget>();
	List<Widget> unusedWidgets = new ArrayList<Widget>();

	public ListWidget(NativeWindow window) {
		super(window);
	}
	
	public void setAdapter(ListAdapter<T> adapter) {
		this.adapter = adapter;
		offsetY = 0;
		itemHeight = -1;
		destroyAllWidgets();
		
		layoutItems();
	}
	
	private void layoutItems() {
		if (adapter.getCount() == 0) return;
		
		int internalHeight = getInternalHeight();
		
		// assume all items have the same height
		if (offsetY < 0) { // we need to initialize the height
			Widget w = adapter.getWidget(null, 0, this);
			layoutWidget(w);
			itemHeight = w.getLayoutInfo().measuredHeight;
			offsetY = 0;
			knownWidgets.put(0,  w);
		}

		Set<Integer> usedWidgetIndexes = new HashSet<Integer>();
		
		int y = offsetY;
		int nItems = (internalHeight + itemHeight - 1) / itemHeight;
		int firstItem = offsetY / itemHeight;
		for(int i=0; i < nItems && (i + firstItem) < adapter.getCount(); i++) {
			int index = i + firstItem;
			
			Widget w = knownWidgets.get(index);
			if (w == null) {
				if (unusedWidgets.size() > 0) {
					w = unusedWidgets.get(0);
					unusedWidgets.remove(0);
				}
				w = adapter.getWidget(w, index, this);
				knownWidgets.put(index,  w);
			}
			layoutWidget(w);
			
			Rectangle childBounds = w.getBounds();
			childBounds.height = itemHeight;
			childBounds.y = y;
			
			usedWidgetIndexes.add(index);
			
			y += itemHeight;
		}
		
		for(Integer knownWidgetIndex : knownWidgets.keySet()) {
			if (usedWidgetIndexes.contains(knownWidgetIndex)) continue;
			unusedWidgets.add(knownWidgets.get(knownWidgetIndex));
			knownWidgets.remove(knownWidgetIndex);
		}
		
		invalidate();
	}
	
	private void layoutWidget(Widget w) {
		LayoutInfo wLayoutInfo = w.getLayoutInfo();
		wLayoutInfo.width = LayoutInfo.MATCH_PARENT;
		wLayoutInfo.height = itemHeight < 0 ? LayoutInfo.MATCH_PARENT : itemHeight;
		
		w.onMeasure(getInternalWidth(), itemHeight < 0 ? getInternalHeight() : itemHeight);
		w.layout();
	}
	
	@Override
	public void render(PaintEvent e) {
		for(Widget child : knownWidgets.values()) {
			if (isDirty) child.invalidate();
			child.render(e);
		}
		super.render(e);
	}
	
	@Override
	public void draw(PaintEvent e) {
		if (getVisibility() != Visibility.Visible) return;
		backBuffer.draw(e.canvas, bounds.x, bounds.y);
		for (Widget child : knownWidgets.values()) {
			child.draw(e);
		}
	}
	
	@Override
	public Point getContentSize(int width, int height) {
		return new Point (0, 0);
	}

	@Override
	public void destroy() {
		super.destroy();
		destroyAllWidgets();
	}
	
	private void destroyAllWidgets() {
		for(Widget knownWidget : knownWidgets.values()) {
			knownWidget.destroy();
		}
		
		for(Widget unusedWidget : unusedWidgets) {
			unusedWidget.destroy();
		}
		
		knownWidgets.clear();
		unusedWidgets.clear();
	}

}
