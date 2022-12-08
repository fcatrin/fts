package fts.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import fts.core.Log;
import fts.ui.events.KeyEvent;
import fts.ui.events.PaintEvent;
import fts.ui.events.TouchEvent;
import fts.ui.graphics.Canvas;
import fts.ui.graphics.Point;
import fts.ui.widgets.AbsoluteContainer;
import fts.utils.dialogs.DialogContext;

public abstract class Window implements DialogContext {
	private static final String LOGTAG = Window.class.getSimpleName();
	
	public static final int FLAGS_BORDERLESS = 1;
	public static final int FLAGS_CENTER = 2;
	public static final int FLAGS_FULLSCREEN = 4;
	public static final int FLAGS_CAN_RESIZE = 8;
	public static final int FLAGS_CAN_MINIMIZE = 16;
	public static final int FLAGS_CAN_MAXIMIZE = 32;
	
	private Widget view;
	private Widget contentView;
	
	private Canvas canvas;
	
	protected WindowListener windowListener;
	
	private Widget focusedWidgetRequest = null;
	private Widget focusedWidget = null;

	private Widget lastTouchedWidget = null;
	
	public abstract void mainLoop();
	
	public void setContentView(Widget view) {
		Container container = new AbsoluteContainer(this);
		container.add(view);
		contentView = view;
		
		LayoutInfo layoutInfo = container.getLayoutInfo();
		layoutInfo.width = LayoutInfo.MATCH_PARENT;
		layoutInfo.height = LayoutInfo.MATCH_PARENT;
		
		this.view = container;
	}
	
	public Widget getContentView() {
		return contentView;
	}
	
	public <W extends Widget> W findWidget(String id) {
		return view.findWidget(id);
	}
	
	public void requestLayout() {
		view.requestLayout();
	}
	
	public void measure() {
		Point bounds = getBounds();
		view.onMeasure(bounds.x, bounds.y);
		LayoutInfo layoutInfo = view.getLayoutInfo();
		view.setBounds(0, 0, layoutInfo.measuredWidth, layoutInfo.measuredHeight);
	}
	
	public void doPendingLayout() {
		if (view.needsLayout()) {
			measure();
			view.layout();
			view.setLayoutDone();
			view.invalidate();
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

	public Widget inflate(String layoutName) {
		return Resources.inflate(this, layoutName);
	}

	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		touchEvent.widget = null;
		
		if (!view.dispatchTouchEvent(touchEvent)) {
			onTouchEvent(touchEvent);
		}
		
		if (lastTouchedWidget != touchEvent.widget && lastTouchedWidget!=null) lastTouchedWidget.onTouchExit(touchEvent);
		lastTouchedWidget = touchEvent.widget;
		
		return true;
	}
	
	/*
	 * if the focused widget doesn't handle the event, it is redirected
	 * to the main window key up / down handlers
	 * Those handlers may call this native window key up / down handlers
	 * if the events are not managed by the window
	 */
	
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		if (focusedWidget != null) {
			if (focusedWidget.dispatchKeyEvent(keyEvent)) return true;
		}
		
		if (keyEvent.down) return windowListener.onKeyDown(keyEvent);
		else return windowListener.onKeyUp(keyEvent);
	}
	
	public boolean onKeyDown(KeyEvent keyEvent) {
		return false;
	}
	
	public boolean onKeyUp(KeyEvent keyEvent) {
		switch (keyEvent.keyCode) {
		case KeyEvent.KEY_DPAD_LEFT  : focusLeft(); return true;
		case KeyEvent.KEY_DPAD_RIGHT : focusRight(); return true;
		case KeyEvent.KEY_DPAD_UP    : focusUp(); return true;
		case KeyEvent.KEY_DPAD_DOWN  : focusDown(); return true;
		default: return false;
		}
	}
	
	private void focusLeft() {
		if (focusedWidget!=null && focusedWidget.getWidgetFocusLeft()!=null) {
			requestFocus(focusedWidget.getWidgetFocusLeft());
		}
	}

	private void focusRight() {
		if (focusedWidget!=null && focusedWidget.getWidgetFocusRight()!=null) {
			requestFocus(focusedWidget.getWidgetFocusRight());
		}
	}

	private void focusUp() {
		if (focusedWidget!=null && focusedWidget.getWidgetFocusUp()!=null) {
			requestFocus(focusedWidget.getWidgetFocusUp());
		}
	}

	private void focusDown() {
		if (focusedWidget!=null && focusedWidget.getWidgetFocusDown()!=null) {
			requestFocus(focusedWidget.getWidgetFocusDown());
		}
	}

	protected void onTouchEvent(TouchEvent touchEvent) {}
	
	public void destroy() {
		if (view!=null) view.destroy();
	}
	
	public void setWindowListener(WindowListener windowListener) {
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
	
	public void requestFocus(Widget widget) {
		focusedWidgetRequest = widget;
	}
	
	public void updateFocus() {
		if (focusedWidgetRequest != null) {
			if (focusedWidget != null) {
				focusedWidget.setFocused(false);
				focusedWidget.fireFocusChanged(false);
			}
			focusedWidgetRequest.setFocused(true);
			focusedWidgetRequest.fireFocusChanged(true);
			focusedWidget = focusedWidgetRequest;
		}
		
		focusedWidgetRequest = null;
	}
	
	public void dumpLayout() {
		File dumpFile = new File(windowListener.getDataDir(), "fts_layout_dump.txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(dumpFile));
			view.dumpLayout(pw);
			
			Log.d(LOGTAG, "FTS layout dump on " + dumpFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw!=null) pw.close();
		}
	}

}
