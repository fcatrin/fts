package fts.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fts.core.LayoutInfo;
import fts.core.ListAdapter;
import fts.core.NativeWindow;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.events.PaintEvent;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public class ListWidget<T> extends Widget {
	private static final String LOGTAG = ListWidget.class.getSimpleName();

	ListAdapter<T> adapter;
	
	private int itemHeight = -1;
	private int offsetY = -1;
	private int selectedIndex = -1;
	
	Map<Integer, Widget> knownWidgets = new HashMap<Integer, Widget>();
	List<Widget> unusedWidgets = new ArrayList<Widget>();
	
	int separator = 1;

	public ListWidget(NativeWindow window) {
		super(window);
	}
	
	public void setAdapter(ListAdapter<T> adapter) {
		this.adapter = adapter;
		offsetY = 0;
		itemHeight = -1;
		destroyAllWidgets();
		
		if (adapter == null || adapter.getCount() == 0) {
			selectedIndex = -1;
		} else {
			selectedIndex = 0;
		}
		
		requestLayout();
	}
	
	@Override
	public void layout() {
		if (adapter == null || adapter.getCount() == 0) return;
		
		measureItemHeight();
		
		int internalHeight = getInternalHeight();
		
		Set<Integer> usedWidgetIndexes = new HashSet<Integer>();
		int baseTop  = padding.top  + bounds.y;
		int baseLeft = padding.left + bounds.x;
		
		int lineHeight = itemHeight + separator;
		int y = offsetY + separator;
		int nItems = (internalHeight + lineHeight - 1 - separator) / lineHeight;
		int firstItem = offsetY / lineHeight;
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
			
			w.setState(State.Selected, selectedIndex == index);
			
			layoutWidget(w);
			
			Rectangle childBounds = w.getBounds();
			childBounds.width = getInternalWidth();
			childBounds.height = itemHeight;
			childBounds.y = y + baseTop;
			childBounds.x = baseLeft;
			w.setBounds(childBounds.x, childBounds.y, childBounds.width, childBounds.height);
			w.layout();

			usedWidgetIndexes.add(index);

			y += itemHeight + separator;
		}
		
		for(Integer knownWidgetIndex : knownWidgets.keySet()) {
			if (usedWidgetIndexes.contains(knownWidgetIndex)) continue;
			unusedWidgets.add(knownWidgets.get(knownWidgetIndex));
			knownWidgets.remove(knownWidgetIndex);
		}
		
		updateSelected();
	}
	
	private void updateSelected() {
		for(Entry<Integer, Widget> entry : knownWidgets.entrySet()) {
			int index = entry.getKey();
			Widget widget = entry.getValue();
			widget.setState(State.Selected, selectedIndex == index);
		}
		invalidate();
	}
	
	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		if (keyEvent.keyCode == KeyEvent.KEY_DPAD_UP) {
			selectUp();
			return true;
		} else if (keyEvent.keyCode == KeyEvent.KEY_DPAD_DOWN) {
			selectDown();
			return true;
		}
		return super.onKeyUp(keyEvent);
	}
	
	private void selectUp() {
		if (adapter == null) return;
		
		selectedIndex--;
		if (selectedIndex < 0) selectedIndex = adapter.getCount() - 1;
		updateSelected();
	}
	
	private void selectDown() {
		if (adapter == null) return;

		selectedIndex++;
		if (selectedIndex >= adapter.getCount()) selectedIndex = 0;
		updateSelected();
	}

	private void measureItemHeight() {
		if (itemHeight > 0) return;
		
		// assume all items have the same height
		Widget w = knownWidgets.get(0);
		if (w == null) {
			w = adapter.getWidget(null, 0, this);
		}
		layoutWidget(w);
		itemHeight = w.getLayoutInfo().measuredHeight;
		knownWidgets.put(0,  w);
	}
	
	private void layoutWidget(Widget w) {
		LayoutInfo wLayoutInfo = w.getLayoutInfo();
		wLayoutInfo.width = LayoutInfo.MATCH_PARENT;
		wLayoutInfo.height = wLayoutInfo.height > 0 ? wLayoutInfo.height :
			(itemHeight < 0 ? LayoutInfo.WRAP_CONTENT : itemHeight);
		
		w.onMeasure(getInternalWidth(), itemHeight < 0 ? getInternalHeight() : itemHeight);
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
		if (adapter != null) {
			measureItemHeight();
			int nItems = adapter.getCount();
			int lineHeight = itemHeight + separator;
			int itemsHeight = nItems * lineHeight + separator;
			int requiredHeight = Math.min(itemsHeight,  height);
			return new Point(width, requiredHeight);
		}
		
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
