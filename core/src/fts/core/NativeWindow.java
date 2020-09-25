package fts.core;

import fts.events.KeyEvent;
import fts.events.PaintEvent;
import fts.events.TouchEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;
import fts.widgets.AbsoluteContainer;

public abstract class NativeWindow {
	private Widget view;
	private Canvas canvas;
	private SimpleCallback onFrameCallback;
	private OnKeyListener onKeyListener;
	
	protected NativeWindowListener windowListener;

	public abstract void mainLoop();
	
	public void setContentView(Widget view) {
		Container container = new AbsoluteContainer(this);
		container.add(view);
		
		LayoutInfo layoutInfo = container.getLayoutInfo();
		layoutInfo.width = LayoutInfo.MATCH_PARENT;
		layoutInfo.height = LayoutInfo.MATCH_PARENT;
		
		this.view = container;
	}
	
	public Widget findWidget(String id) {
		return view.findWidget(id);
	}
	
	public void layout() {
		Point bounds = getBounds();
		view.onMeasure(bounds.x, bounds.y);
		LayoutInfo layoutInfo = view.getLayoutInfo();
		view.setBounds(0, 0, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
		view.requestLayout();
	}
	
	public void doPendingLayout() {
		if (view.needsLayout()) {
			view.layout();
			view.setLayoutDone();
		}
	}
	
	public void doRender(PaintEvent e) {
		view.render(e);
	}
	
	public void doPaint(PaintEvent e) {
		view.draw(e);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		if (view.dispatchTouchEvent(touchEvent)) return true;
		onTouchEvent(touchEvent);
		return true;
	}
	
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		Widget focusedView;
		
		if (view instanceof Container) {
			Container containerView = (Container)view;
			focusedView = containerView.findFocusedView();
		} else {
			focusedView = view.isFocused() ? view : null;
		}
		
		if (focusedView!=null) {
			if (view.dispatchKeyEvent(keyEvent)) return true;
		}
		
		if (onKeyListener!=null) {
			if (keyEvent.down) return onKeyListener.onKeyDown(keyEvent);
			else return onKeyListener.onKeyUp(keyEvent);
		}
		return false;
	}
	
	public void setOnKeyListener(OnKeyListener onKeyListener) {
		this.onKeyListener = onKeyListener;
	}
	protected void onTouchEvent(TouchEvent touchEvent) {}
	
	public void setOnFrameCallback(SimpleCallback onFrameCallback) {
		this.onFrameCallback = onFrameCallback;
	}
	
	public SimpleCallback getOnFrameCallback() {
		return onFrameCallback;
	}
	
	public void destroy() {
		if (view!=null) view.destroy();
	}
	
	public void setWindowListener(NativeWindowListener windowListener) {
		this.windowListener = windowListener;
	}
	
	public void setTitle(String title) {
		windowListener.setTitle(title);
	}

	public void open() {
		windowListener.open();
	}

	public Point getBounds() {
		return windowListener.getBounds();
	}

}
