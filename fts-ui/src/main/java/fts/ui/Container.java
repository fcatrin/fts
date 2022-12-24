package fts.ui;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fts.ui.events.PaintEvent;
import fts.ui.events.TouchEvent;
import fts.ui.graphics.Align;
import fts.ui.graphics.Rectangle;

public abstract class Container extends Widget {
	List<Widget> children = new ArrayList<Widget>();
	Map<String, Widget> knownWidgets = new HashMap<>();
	Align align = new Align();

	public Container(Window w) {
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
		e.canvas.viewStart(bounds.x, bounds.y, bounds.width, bounds.height);
		backBuffer.draw(e.canvas, bounds.x, bounds.y);
		for (Widget child : children) {
			child.draw(e);
		}
		e.canvas.viewEnd();
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

	public <T extends Widget> void add(T view) {
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
	public <W extends Widget> W findWidget(String id) {
		W w = super.findWidget(id);
		if (w!=null) return w;
		
		w = (W)knownWidgets.get(id);
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
	
	
	// subclasses must fill touchEvent.widget when handling the event
	// otherwise onTouchExit will not work
	
	@Override
	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		for(int i=children.size()-1; i>=0; i--) {
			Widget child = children.get(i);
			if (child.getVisibility() != Visibility.Visible) continue;
			
			Rectangle childBounds = child.getBounds();
			if (childBounds.contains(touchEvent.x, touchEvent.y)) {
				if (child.dispatchTouchEvent(touchEvent)) {
					if (touchEvent.widget == null) touchEvent.widget = this;
					return true;
				}
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
	
	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	@Override
	public void dumpLayout(PrintWriter writer, String spacer) {
		super.dumpLayout(writer, spacer);
		for(Widget widget : getChildren()) {
			widget.dumpLayout(writer, spacer + "  ");
		}
	}
}
