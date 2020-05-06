package fts.core;

import org.w3c.dom.Node;

import fts.graphics.Canvas;
import fts.widgets.View;

public interface ComponentFactory {
	public Window createWindow();
	public View createView(Node node);
	public NativeView createNativeView(Window w);
}
