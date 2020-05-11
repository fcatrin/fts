package fts.core;

import java.io.File;
import java.util.List;

import org.w3c.dom.Element;

import fts.graphics.Drawable;

public interface ComponentFactory {
	public Window createWindow();
	public Widget createWidget(Element element);
	public NativeView createNativeView(Window w);
	public Drawable createDrawable(Element element);
	public void registerFont(String alias, File file);
	public File getFont(String alias);
	public List<String> getAllFontAliases();
}
