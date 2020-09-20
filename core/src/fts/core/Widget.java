package fts.core;

import fts.events.OnClickListener;
import fts.events.KeyEvent;
import fts.events.TouchEvent;
import fts.events.TouchEvent.Action;
import fts.events.PaintEvent;
import fts.graphics.Align;
import fts.graphics.Drawable;
import fts.graphics.Point;
import fts.graphics.Rectangle;
import fts.graphics.SelectorDrawable;
import fts.graphics.Sides;

public abstract class Widget extends Component {
	public enum State {Selected, Focused, Enabled, Pressed}
	
	protected boolean state[] = new boolean[] {false, false, true, false};
	protected Sides padding = new Sides();
	protected Rectangle bounds  = new Rectangle();
	
	public static final String VALUE_MATCH_PARENT = "match_parent";
	public static final String VALUE_WRAP_CONTENT = "wrap_content";

	String id;
	
	LayoutInfo layoutInfo = new LayoutInfo();
	boolean layoutRequested = false;
	
	NativeView nativeView;
	Window window;
	
	private Align containerAlign = new Align();
	
	protected Drawable background;
	
	private boolean isClickable = false;
	private OnClickListener onClickListener;
	
	protected BackBuffer backBuffer = null;
	protected boolean isDirty = true;
	
	public Widget(Window window) {
		nativeView = Application.createNativeView(window);
		this.window = window;
	}
	
	public Window getWindow() {
		return window;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	Runnable invalidateTask = new Runnable() {
		@Override
		public void run() {
			if (!isDisposed()) invalidate();
		}
	};

	public void invalidate() {
		isDirty = true;
	}
	
	protected void onTouchEvent(TouchEvent e) {
		e.widget = this;
		if (e.action == Action.DOWN) {
			onTouchDown(e);	
		} else if (e.action == Action.MOVE) {
			onTouchMove(e);
		} else {
			onTouchUp(e);
		}
	}

	protected void onTouchDown(TouchEvent e) {
		setPressed(true);
	}
	
	protected void onTouchMove(TouchEvent e) {
		// System.out.println("Move " + e.x + ", " + e.y + " " + this);
	}
	
	protected void onTouchUp(TouchEvent e) {
		setPressed(false);
		if (isClickable && onClickListener!=null) {
			onClickListener.onClick(this);
		}
	}
	
	protected void onTouchExit(TouchEvent e) {
		setPressed(false);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	protected void onKeyPressed(KeyEvent e) {
	}

	protected void onKeyReleased(KeyEvent e) {
	}

	public BackBuffer getBackBuffer() {
		return backBuffer;
	}
	
	protected BackBuffer getBackBuffer(int width, int height) {
		if (backBuffer == null || backBuffer.width != width || backBuffer.height != height) {
			if (backBuffer != null) backBuffer.destroy();
			backBuffer = Application.createBackBuffer(width, height);
		}
		return backBuffer;
	}
	
	protected void render(PaintEvent e) {
		if (isDirty) {
			getBackBuffer(bounds.width, bounds.height);
			backBuffer.bind();
			onPaint(e);
			backBuffer.unbind();
		}
		isDirty = false;
	}
	
	protected void draw(PaintEvent e) {
		backBuffer.draw(e.canvas);
	}
	
	protected void onPaint(PaintEvent e) {
		if (background != null) {
			background.setBounds(bounds);
			background.draw(e.canvas);
		}
	}
	
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
		if (background instanceof SelectorDrawable) {
			SelectorDrawable selectorDrawable = (SelectorDrawable)background;
			selectorDrawable.setState(state.ordinal(), value);
		}
		return changed;
	}
	
	public boolean getState(State state) {
		return this.state[state.ordinal()];
	}

	public boolean isClickable() {
		return isClickable;
	}

	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

	public void setMargin(int margin) {
		layoutInfo.margins.left   = margin;
		layoutInfo.margins.right  = margin;
		layoutInfo.margins.top    = margin;
		layoutInfo.margins.bottom = margin;
	}
	
	public void setMarginLeft(int margin) {
		layoutInfo.margins.left = margin;
	}
	
	public void setMarginRight(int margin) {
		layoutInfo.margins.right = margin;
	}
	
	public void setMarginTop(int margin) {
		layoutInfo.margins.top = margin;
	}
	
	public void setMarginBottom(int margin) {
		layoutInfo.margins.bottom = margin;
	}
	
	public void setPadding(int p) {
		padding.left   = p;
		padding.right  = p;
		padding.top    = p;
		padding.bottom = p;
	}
	
	public void setPadding(int left, int top, int right, int bottom) {
		padding.left = left;
		padding.top = top;
		padding.right = right;
		padding.bottom = bottom;
	}
	
	public void setPaddingLeft(int p) {
		padding.left   = p;
	}

	public void setPaddingRight(int p) {
		padding.right  = p;
	}
	
	public void setPaddingTop(int p) {
		padding.top    = p;
	}

	public void setPaddingBottom(int p) {
		padding.bottom = p;
	}

	public int getInternalWidth() {
		return bounds.width - padding.left - padding.right;
	}
	
	public int getInternalHeight() {
		return bounds.height - padding.top - padding.bottom;
	}
	
	public void postInvalidate() {
		if (!isDisposed()) Context.post(invalidateTask);
	}
	
	public void post(Runnable runnable) {
		if (!isDisposed()) Context.post(runnable);
	}
	
	protected Point getTextSize(String s) {
		return nativeView.getTextSize(s);
	}
	
	protected boolean isDisposed() {
		return false;
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("width") || propertyName.equals("height")) {
			if (VALUE_MATCH_PARENT.equals(value)) return LayoutInfo.MATCH_PARENT;
			if (VALUE_WRAP_CONTENT.equals(value)) return LayoutInfo.WRAP_CONTENT;
			return resolvePropertyValueDimen(propertyName, value);
		}
		return super.resolvePropertyValue(propertyName, value);
	}
	
	public String toString(String s) {
		return String.format("{class: %s, bounds:%s%s}", 
				getClass().getName(),
				bounds.toString(),
				s);
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public void onMeasure(int parentWidth, int parentHeight) {
		MeasureSpec w = new MeasureSpec();
		MeasureSpec h = new MeasureSpec();
		
		if (layoutInfo.width == LayoutInfo.MATCH_PARENT || layoutInfo.width == 0) {
			w.type  = MeasureSpec.Type.Exact;
			w.value = parentWidth;
		} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
			w.type  = MeasureSpec.Type.AtMost;
			w.value = parentWidth;
		} else {
			w.type  = MeasureSpec.Type.Exact;
			w.value = layoutInfo.width;
		}
		if (layoutInfo.height == LayoutInfo.MATCH_PARENT || layoutInfo.height == 0) {
			h.type  = MeasureSpec.Type.Exact;
			h.value = parentHeight;
		} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
			h.type  = MeasureSpec.Type.AtMost;
			h.value = parentHeight;
		} else {
			h.type  = MeasureSpec.Type.Exact;
			h.value = layoutInfo.height;
		}
		onMeasure(w, h);
	}
	
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		Point contentSize = getContentSize(w.value, h.value);
		int width  = contentSize.x;
		int height = contentSize.y;

		switch (w.type) {
		case Exact:
			width = w.value;
			break;
		case AtMost:
			width = Math.min(width, w.value);
		default:
			break;
		}
		
		switch (h.type) {
		case Exact:
			height = h.value;
			break;
		case AtMost:
			height = Math.min(height, h.value);
			break;
		default:
			break;
		}
		
		layoutInfo.measuredWidth  = width;
		layoutInfo.measuredHeight = height;
	}

	public abstract Point getContentSize(int width, int height);

	public static class MeasureSpec {
		public enum Type {Free, Exact, AtMost};
		public Type type = Type.Free;
		public int value = 0;
	}

	public void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width  = width;
		bounds.height = height;
	}

	public Rectangle getBounds() {
		return bounds;
	}
	public LayoutInfo getLayoutInfo() {
		return layoutInfo;
	}
	
	public void setWidth(int width) {
		layoutInfo.width = width;
	}
	
	public void setHeight(int height) {
		layoutInfo.height = height;
	}
	
	public void setBackground(Drawable background) {
		this.background = background;
	}
	
	public Align getContainerAlign() {
		return containerAlign;
	}
	
	public void setContainerAlign(Align align) {
		this.containerAlign = align;
	}
	
	public void layout() {}
	
	public void setLayoutDone() {
		layoutRequested = false;
	}
	
	public void requestLayout() {
		layoutRequested = true;
	}
	
	public boolean needsLayout() {
		return layoutRequested;
	}
	
	public Point getPaddingSize() {
		return new Point(padding.left + padding.right, padding.top + padding.bottom);
	}
	
	public Rectangle getInternalBounds(int width, int height) {
		Point paddingSize = getPaddingSize();
		return new Rectangle( 
				bounds.x + padding.left,
				bounds.y + padding.top,
				bounds.width - paddingSize.x,
				bounds.height - paddingSize.y);
	}

	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		onTouchEvent(touchEvent);
		return true;
	}

	public Widget findWidget(String id) {
		if (id!=null && id.equals(this.id)) return this;
		return null;
	}
	
	public void destroy() {
		if (backBuffer!=null) {
			backBuffer.destroy();
			backBuffer = null;
		}
	}
}
