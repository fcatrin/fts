package fts.core;

import org.w3c.dom.Element;

import fts.graphics.Drawable;

public interface ComponentFactory {
	public Window createWindow();
	public Widget createWidget(Element element);
	public NativeView createNativeView(Window w);
	public Drawable createDrawable(Element element);
}
