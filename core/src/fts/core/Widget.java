package fts.core;

import java.io.PrintWriter;

import fts.events.KeyEvent;
import fts.events.OnClickListener;
import fts.events.OnFocusChangedListener;
import fts.events.OnStateListener;
import fts.events.PaintEvent;
import fts.events.TouchEvent;
import fts.events.TouchEvent.Action;
import fts.graphics.Align;
import fts.graphics.BackBuffer;
import fts.graphics.Drawable;
import fts.graphics.Point;
import fts.graphics.Rectangle;
import fts.graphics.SelectorDrawable;
import fts.graphics.Sides;

public class Widget extends Component {
	public enum State {Selected, Focused, Enabled, Pressed}
	public enum Visibility {Visible, Invisible, Gone}
	
	protected boolean state[] = new boolean[] {false, false, true, false};
	protected Sides padding = new Sides();
	protected Rectangle bounds  = new Rectangle();
	protected Rectangle paintBounds = new Rectangle();
	protected Rectangle paintInternalBounds = new Rectangle();

	public static final String VALUE_MATCH_PARENT = "match_parent";
	public static final String VALUE_WRAP_CONTENT = "wrap_content";

	String id;
	
	LayoutInfo layoutInfo = new LayoutInfo();
	boolean layoutRequested = false;
	
	NativeWindow window;
	
	private Align containerAlign = new Align();
	protected Visibility visibility = Visibility.Visible;
	
	protected Drawable background;
	
	private boolean isClickable = false;
	private boolean isFocusable = false;
	private boolean isFocusableInTouchMode = false;
	
	private OnClickListener onClickListener;
	private OnStateListener onStateListener;
	private OnFocusChangedListener onFocusChangedListener;
	
	private String focusLeft;
	private String focusRight;
	private String focusUp;
	private String focusDown;
	
	private Widget widgetFocusLeft;
	private Widget widgetFocusRight;
	private Widget widgetFocusUp;
	private Widget widgetFocusDown;
	
	protected BackBuffer backBuffer = null;
	protected boolean isDirty = true;
	
	public Widget(NativeWindow window) {
		this.window = window;
	}
	
	public NativeWindow getWindow() {
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
		if (isFocusable && isFocusableInTouchMode) requestFocus();
	}
	
	protected void onTouchMove(TouchEvent e) {
		// System.out.println("Move " + e.x + ", " + e.y + " " + this);
	}
	
	protected void onTouchUp(TouchEvent e) {
		setPressed(false);
		fireClick();
	}
	
	protected void onTouchExit(TouchEvent e) {
		setPressed(false);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setOnStateListener(OnStateListener onStateListener) {
		this.onStateListener = onStateListener;
	}

	public void setOnFocusChangedListener(OnFocusChangedListener onFocusChangedListener) {
		this.onFocusChangedListener = onFocusChangedListener;
	}

	protected void onKeyPressed(KeyEvent e) {
	}

	protected void onKeyReleased(KeyEvent e) {
	}

	public BackBuffer getBackBuffer() {
		return backBuffer;
	}
	
	protected BackBuffer getBackBuffer(int width, int height) {
		if (backBuffer == null || backBuffer.getWidth() != width || backBuffer.getHeight() != height) {
			backBuffer = Application.createBackBuffer(id, width, height);
		}
		return backBuffer;
	}
	
	public void render(PaintEvent e) {
		if ((isDirty && visibility == Visibility.Visible) || backBuffer == null) {
			getBackBuffer(bounds.width, bounds.height);
			backBuffer.bind();
			if (bounds.width > 0 && bounds.height > 0) onPaint(e);
			backBuffer.unbind();
		}
		isDirty = false;
	}
	
	public void draw(PaintEvent e) {
		if (visibility != Visibility.Visible) return;
		
		backBuffer.draw(e.canvas, bounds.x, bounds.y);
	}
	
	protected void onPaint(PaintEvent e) {
		if (background != null) {
			background.setBounds(getPaintBounds());
			background.draw(e.canvas);
		}
	}
	
	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		if (this.visibility == visibility) return;
		
		this.visibility = visibility;
		requestLayout();
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
		
		if (changed) {
			if (onStateListener!=null) onStateListener.onStateChanged(this, state, value);
			invalidate();
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

	public boolean isFocusable() {
		return isFocusable;
	}

	public void setFocusable(boolean isFocusable) {
		this.isFocusable = isFocusable;
	}

	public boolean isFocusableInTouchMode() {
		return isFocusableInTouchMode;
	}

	public void setFocusableInTouchMode(boolean isFocusableInTouchMode) {
		this.isFocusableInTouchMode = isFocusableInTouchMode;
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
	
	protected boolean isDisposed() {
		return false;
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("width") || propertyName.equals("height")) {
			if (VALUE_MATCH_PARENT.equals(value)) return LayoutInfo.MATCH_PARENT;
			if (VALUE_WRAP_CONTENT.equals(value)) return LayoutInfo.WRAP_CONTENT;
			return resolvePropertyValueDimen(propertyName, value);
		} else if (propertyName.equals("visibility")) {
			if (value.equals("gone")) return Visibility.Gone;
			if (value.equals("visible")) return Visibility.Visible;
			if (value.equals("invisible")) return Visibility.Invisible;
			throw new RuntimeException("Invalid visibility " + value);
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
		
		int maxWidth  = parentWidth  - layoutInfo.margins.left - layoutInfo.margins.right;
		int maxHeight = parentHeight - layoutInfo.margins.top  - layoutInfo.margins.bottom;
		
		if (layoutInfo.width == LayoutInfo.MATCH_PARENT || layoutInfo.width == 0) {
			w.type  = MeasureSpec.Type.Exact;
			w.value = maxWidth;
		} else if (layoutInfo.width == LayoutInfo.WRAP_CONTENT) {
			w.type  = MeasureSpec.Type.AtMost;
			w.value = maxWidth;
		} else {
			w.type  = MeasureSpec.Type.Exact;
			w.value = layoutInfo.width;
		}
		if (layoutInfo.height == LayoutInfo.MATCH_PARENT || layoutInfo.height == 0) {
			h.type  = MeasureSpec.Type.Exact;
			h.value = maxHeight;
		} else if (layoutInfo.height == LayoutInfo.WRAP_CONTENT) {
			h.type  = MeasureSpec.Type.AtMost;
			h.value = maxHeight;
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
			w.value = width;
		default:
			break;
		}
		
		switch (h.type) {
		case Exact:
			height = h.value;
			break;
		case AtMost:
			height = Math.min(height, h.value);
			h.value = height;
			break;
		default:
			break;
		}
		
		layoutInfo.measuredWidth  = width;
		layoutInfo.measuredHeight = height;
	}

	public Point getContentSize(int width, int height) {
		return new Point(0, 0);
	}

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
		
		paintBounds.x = 0;
		paintBounds.y = 0;
		paintBounds.width = bounds.width;
		paintBounds.height = bounds.height;
	}

	public Rectangle getPaintBounds() {
		return paintBounds;
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
	
	public Rectangle getInternalPaintBounds(int width, int height) {
		paintInternalBounds.x = padding.left;
		paintInternalBounds.y = padding.top;
		paintInternalBounds.width  = bounds.width  - (padding.left + padding.right);
		paintInternalBounds.height = bounds.height - (padding.top  + padding.bottom);
		
		return paintInternalBounds;
	}

	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		touchEvent.widget = this;
		
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

	protected boolean dispatchKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.down) return onKeyDown(keyEvent);
		else return onKeyUp(keyEvent);
	}
	
	public boolean onKeyUp(KeyEvent keyEvent) {
		return false;
	}
	
	public boolean onKeyDown(KeyEvent keyEvent) {
		return false;
	}
	
	public void requestFocus() {
		window.requestFocus(this);
	}
	
	public void fireFocusChanged(boolean focused) {
		if (onFocusChangedListener != null) onFocusChangedListener.onFocusChanged(this, focused);
	}
	
	public void fireClick() {
		if (isClickable && onClickListener!=null) {
			onClickListener.onClick(this);
		}
	}

	public String getFocusLeft() {
		return focusLeft;
	}

	public void setFocusLeft(String focusLeft) {
		this.focusLeft = focusLeft;
	}

	public String getFocusRight() {
		return focusRight;
	}

	public void setFocusRight(String focusRight) {
		this.focusRight = focusRight;
	}

	public String getFocusUp() {
		return focusUp;
	}

	public void setFocusUp(String focusUp) {
		this.focusUp = focusUp;
	}

	public String getFocusDown() {
		return focusDown;
	}

	public void setFocusDown(String focusDown) {
		this.focusDown = focusDown;
	}

	public Widget getWidgetFocusLeft() {
		return widgetFocusLeft;
	}

	public void setWidgetFocusLeft(Widget widgetFocusLeft) {
		this.widgetFocusLeft = widgetFocusLeft;
	}

	public Widget getWidgetFocusRight() {
		return widgetFocusRight;
	}

	public void setWidgetFocusRight(Widget widgetFocusRight) {
		this.widgetFocusRight = widgetFocusRight;
	}

	public Widget getWidgetFocusUp() {
		return widgetFocusUp;
	}

	public void setWidgetFocusUp(Widget widgetFocusUp) {
		this.widgetFocusUp = widgetFocusUp;
	}

	public Widget getWidgetFocusDown() {
		return widgetFocusDown;
	}

	public void setWidgetFocusDown(Widget widgetFocusDown) {
		this.widgetFocusDown = widgetFocusDown;
	}
	
	public void dumpLayout(PrintWriter writer) {
		dumpLayout(writer, "");
	}
	
	public void dumpLayout(PrintWriter writer, String spacer) {
		String info = String.format("%s[%s id:%s (%d,%d)-(%d,%d) %dx%d w:%s h:%s mw:%d, mh:%d]",
				spacer,
				getClass().getSimpleName(), getId(),
				bounds.x, bounds.y,
				bounds.x + bounds.width, bounds.y + bounds.height,
				bounds.width, bounds.height,
				getSizeDescriptor(layoutInfo.width), getSizeDescriptor(layoutInfo.height),
				layoutInfo.measuredWidth, layoutInfo.measuredHeight);
		writer.println(info);
	}
	
	private String getSizeDescriptor(int size) {
		if (size == LayoutInfo.MATCH_PARENT) return "match_parent";
		else if (size == LayoutInfo.WRAP_CONTENT) return "wrap_content";
		else if (size == 0) return String.valueOf(size);
		else return "weight " + layoutInfo.weight;
	}
}
