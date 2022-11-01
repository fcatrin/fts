package fts.core;

import java.io.File;
import java.util.List;

import org.w3c.dom.Element;

import fts.graphics.BackBuffer;
import fts.graphics.Color;
import fts.graphics.ColorListSelector;
import fts.graphics.Drawable;
import fts.graphics.Image;

public interface ComponentFactory {
	public NativeWindow createNativeWindow(String title, int width, int height, int flags);
	public Widget createWidget(Element element);
	public Drawable createDrawable(Element element);
	public BackBuffer createBackBuffer(String id, int width, int height);
	public Image createImage(String src);
	public Image createImage(String name, byte data[]);
	
	public void registerFont(String alias, File file);
	public File getFont(String alias);
	public List<String> getAllFontAliases();
	
	public void registerColor(String alias, ColorListSelector color);
	public ColorListSelector getColor(String alias);

	public void registerStyle(String alias, Style style);
	public Style getStyle(String alias);

	public void registerString(String alias, String string);
	public String getString(String string);
	
	public void registerDimen(String alias, String dimen);
	public String getDimen(String alias);

}
