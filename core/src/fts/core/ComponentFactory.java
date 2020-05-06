package fts.core;

import org.w3c.dom.Node;

import fts.graphics.Canvas;
import fts.widgets.Widget;

public interface ComponentFactory {
	public Window createWindow();
	public Widget createWidget(Node node);
	public NativeView createNativeView(Window w);
}
