package fts.views;

import fts.core.Application;
import fts.core.Component;
import fts.core.Context;
import fts.core.NativeView;
import fts.core.Window;
import fts.events.KeyEvent;
import fts.events.MouseEvent;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public abstract class View extends Component {
	public enum State {Selected, Focused, Enabled, Pressed}
	private static final int DEFAULT_WIDTH  = 100;
	private static final int DEFAULT_HEIGHT = 100;
	
	private static final long CLICK_TIME = 200;
	protected boolean state[] = new boolean[] {false, false, true, false};
	protected Rectangle padding = new Rectangle();
	protected Rectangle bounds  = new Rectangle();
	
	private long mouseDownTime = 0;
	private Point mouseDownPosition = new Point();
	
	public static final String VALUE_MATCH_PARENT = "match_parent";
	public static final String VALUE_WRAP_CONTENT = "wrap_content";
	
	public final int MATCH_PARENT = -1;
	public final int WRAP_CONTENT = -2;
	
	int layoutWidth;
	int layoutHeight;
	
	int measuredWidth;
	int measuredHeight;
	
	NativeView nativeView;
	
	public View(Window w) {
		nativeView = Application.createNativeView(w);
	}
	
	Runnable invalidateTask = new Runnable() {
		@Override
		public void run() {
			if (!isDisposed()) redraw();
		}
	};

	public abstract void redraw();
	
	protected final void onMouseUpGesture(MouseEvent e) {
		long t = System.currentTimeMillis() - mouseDownTime;
		if (t < CLICK_TIME) {
			onMouseClick(e);
		}
		onMouseUp(e);
	}

	protected final void onMouseDownGesture(MouseEvent e) {
		mouseDownTime = System.currentTimeMillis();
		mouseDownPosition.x = e.x;
		mouseDownPosition.y = e.y;
		onMouseDown(e);
	}

	protected void onKeyPressed(KeyEvent e) {
	}

	protected void onKeyReleased(KeyEvent e) {
	}

	protected void onMouseMove(MouseEvent e) {
	}

	protected void onMouseUp(MouseEvent e) {
		setPressed(false);
	}

	protected void onMouseDown(MouseEvent e) {
		setPressed(true);
	}

	protected void onMouseClick(MouseEvent e) {}
	
	protected void onMouseDoubleClick(MouseEvent e) {}

	protected abstract void onPaint(PaintEvent e);
	protected void onDispose() {}

	public boolean isSelected() {
		return getState(State.Selected);
	}

	public void setSelected(boolean selected) {
		setState(State.Selected, selected);
	}

	public boolean isFocused() {
		return getState(State.Focused);
	}

	public void setFocused(boolean focused) {
		setState(State.Focused, focused);
	}

	public boolean isEnabled() {
		return getState(State.Enabled);
	}

	public void setEnabled(boolean enabled) {
		setState(State.Enabled, enabled);
	}

	public boolean isPressed() {
		return getState(State.Pressed);
	}

	public void setPressed(boolean pressed) {
		setState(State.Pressed, pressed);
	}
	
	public boolean setState(State state, boolean value) {
		boolean changed = this.state[state.ordinal()] != value;
		this.state[state.ordinal()] = value;
		return changed;
	}
	
	public boolean getState(State state) {
		return this.state[state.ordinal()];
	}

	public void setPadding(int p) {
		padding.x = p;
		padding.y = p;
		padding.width = p;
		padding.height = p;
	}
	
	public void setPadding(int top, int left, int bottom, int right) {
		padding.x = left;
		padding.y = top;
		padding.width = right;
		padding.height = bottom;
	}
	
	public int getInternalWidth() {
		return bounds.width - padding.x - padding.width;
	}
	
	public int getInternalHeight() {
		return bounds.height - padding.y - padding.height;
	}
	
	public void postInvalidate() {
		if (!isDisposed()) Context.post(invalidateTask);
	}
	
	public void post(Runnable runnable) {
		if (!isDisposed()) Context.post(runnable);
	}
	
	public int getRequiredHeight() {
		Point requiredSize = computeSize(0, 0);
		return requiredSize.y;
	}
	
	public int getRequiredWidth() {
		Point requiredSize = computeSize(0, 0);
		return requiredSize.x;
	}
	
	protected Point getTextSize(String s) {
		return nativeView.getTextSize(s);
	}
	
	public abstract Point computeSize(int x, int y);
	
	protected boolean isDisposed() {
		return false;
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("layoutWidth") || propertyName.equals("layoutHeight")) {
			if (VALUE_MATCH_PARENT.equals(value)) return MATCH_PARENT;
			if (VALUE_WRAP_CONTENT.equals(value)) return WRAP_CONTENT;
			return resolvePropertyValueDimen(propertyName, value);
		}
		return super.resolvePropertyValue(propertyName, value);
	}

	
	public int getLayoutWidth() {
		return layoutWidth;
	}

	public void setLayoutWidth(int layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	public int getLayoutHeight() {
		return layoutHeight;
	}

	public void setLayoutHeight(int layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

	public String toString(String s) {
		return String.format("{class: %s, width: %d, height; %d, mw: %d, mh:%d%s}", 
				getClass().getName(),
				layoutWidth,
				layoutHeight,
				measuredWidth,
				measuredHeight,
				s);
		
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public void onMeasure(int parentWidth, int parentHeight) {
		MeasureSpec w = new MeasureSpec();
		MeasureSpec h = new MeasureSpec();
		if (layoutWidth == MATCH_PARENT) {
			w.type  = MeasureSpec.Type.Exact;
			w.value = parentWidth;
		} else if (layoutWidth == WRAP_CONTENT) {
			w.type  = MeasureSpec.Type.AtMost;
			w.value = parentWidth;
		} else {
			w.type  = MeasureSpec.Type.Exact;
			w.value = layoutWidth;
		}
		if (layoutHeight == MATCH_PARENT) {
			h.type  = MeasureSpec.Type.Exact;
			h.value = parentHeight;
		} else if (layoutHeight == WRAP_CONTENT) {
			h.type  = MeasureSpec.Type.AtMost;
			h.value = parentHeight;
		} else {
			h.type  = MeasureSpec.Type.Exact;
			h.value = layoutHeight;
		}
		onMeasure(w, h);
	}
	
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		int width;
		int height;

		switch (w.type) {
		case Exact:
			width = w.value;
			break;
		case AtMost:
			width = Math.min(DEFAULT_WIDTH, w.value);
		default:
			width = DEFAULT_WIDTH;
			break;
		}
		
		switch (h.type) {
		case Exact:
			height = h.value;
			break;
		case AtMost:
			height = Math.min(DEFAULT_HEIGHT, h.value);
		default:
			height = DEFAULT_HEIGHT;
			break;
		}
		
		setMeasuredDimensions(width, height);
	}

			
	private void setMeasuredDimensions(int width, int height) {
		this.measuredWidth = width;
		this.measuredHeight = height;
		
	}


	public static class MeasureSpec {
		public enum Type {Free, Exact, AtMost};
		public Type type = Type.Free;
		int value = 0;
	}
}
