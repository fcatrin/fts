package fts.core;

import java.io.File;
import java.util.List;

import org.w3c.dom.Element;

import fts.graphics.BackBuffer;
import fts.graphics.Color;
import fts.graphics.Drawable;

public interface ComponentFactory {
	public NativeWindow createNativeWindow(String title, int width, int height, int flags);
	public Widget createWidget(Element element);
	public Drawable createDrawable(Element element);
	public BackBuffer createBackBuffer(int width, int height);
	
	public void registerFont(String alias, File file);
	public File getFont(String alias);
	public List<String> getAllFontAliases();
	
	public void registerColor(String alias, Color color);
	public Color getColor(String alias);
	
	public void registerString(String alias, String string);
	public String getString(String string);
	
	public void registerDimen(String alias, String dimen);
	public String getDimen(String alias);

}
