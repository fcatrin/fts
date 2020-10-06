package fts.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fts.core.Widget.Visibility;
import fts.events.PaintEvent;
import fts.events.TouchEvent;
import fts.graphics.Align;
import fts.graphics.Rectangle;

public abstract class Container extends Widget {
	List<Widget> children = new ArrayList<Widget>();
	Map<String, Widget> knownWidgets = new HashMap<String, Widget>();
	Align align = new Align();

	public Container(NativeWindow w) {
		super(w);
	}

	@Override
	public void render(PaintEvent e) {
		for(Widget child : children) {
			if (isDirty) child.invalidate();
			child.render(e);
		}
		super.render(e);
	}
	
	@Override
	public void draw(PaintEvent e) {
		if (getVisibility() != Visibility.Visible) return;
		backBuffer.draw(e.canvas, bounds.x, bounds.y);
		for (Widget child : children) {
			child.draw(e);
		}
	}
	
	public abstract void layout();
	
	@Override
	public void setLayoutDone() {
		super.setLayoutDone();
		for(Widget child : children) {
			child.setLayoutDone();
		}
	}

	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		return super.resolvePropertyValue(propertyName, value);
	}

	public void add(Widget view) {
		children.add(view);
		String id = view.getId();
		if (id!=null && !knownWidgets.containsKey(id)) {
			knownWidgets.put(id,  view);
		}
	}
	
	public void removeAllChildren() {
		for(Widget child : children) {
			child.destroy();
		}
		children.clear();
	}
	
	@Override
	public Widget findWidget(String id) {
		Widget w = super.findWidget(id);
		if (w!=null) return w;
		
		w = knownWidgets.get(id);
		if (w!=null) return w;
		
		for(Widget child : children) {
			w = child.findWidget(id);
			if (w!=null) return w;
		}
		
		return null;
	}

	public List<Widget> getChildren() {
		return children;
	}

	@Override
	public String toString(String s) {
		return super.toString(String.format(", children;%s%s", children.toString(), s));
	}
	
	public abstract void onMeasureChildren(MeasureSpec w, MeasureSpec h);
	
	@Override
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		super.onMeasure(w, h);
		onMeasureChildren(w, h);
	}

	@Override
	public boolean needsLayout() {
		if (super.needsLayout()) return true;
		for(Widget child : children) {
			if (child.needsLayout()) return true;
		}
		return false;
	}
	
	@Override
	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		for(Widget child : children) {
			Rectangle childBounds = child.getBounds();
			if (childBounds.contains(touchEvent.x, touchEvent.y)) {
				if (child.dispatchTouchEvent(touchEvent)) return true;
			}
		}
		return super.dispatchTouchEvent(touchEvent);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		for(Widget child : children) {
			child.destroy();
		}
	}
	
	public Widget findFocusedView() {
		Widget focusedView = null;
		for(Widget child : children) {
			if (child instanceof Container) {
				Container container = (Container)child;
				focusedView = container.findFocusedView();
			} else {
				if (child.isFocused()) focusedView = child;
			}
			if (focusedView != null) break;
		}
		if (focusedView != null) return focusedView;
		return isFocused() ? this : null;
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

}
