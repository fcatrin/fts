package fts.core;

import org.w3c.dom.Node;

import fts.graphics.Drawable;

public interface ComponentFactory {
	public Window createWindow();
	public Widget createWidget(Node node);
	public NativeView createNativeView(Window w);
	public Drawable createDrawable(Node node);
}
