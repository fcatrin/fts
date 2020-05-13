package fts.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import fts.graphics.Color;
import fts.graphics.Drawable;
import fts.graphics.Shape;
import fts.graphics.TextDrawable;

public abstract class CoreComponentFactory implements ComponentFactory {

	private static Map<String, File>  fonts = new HashMap<String, File>();
	private static Map<String, Color> colors = new HashMap<String, Color>();
	private static Map<String, String> strings = new HashMap<String, String>();

	@Override
	public Widget createWidget(Element node) {
		return null;
	}

	@Override
	public Drawable createDrawable(Element node) {
		String name = node.getNodeName();
		if (name.equals("shape")) {
			return new Shape(node);
		} else if (name.equals("text")) {
			return new TextDrawable(node);
		}
		return null;
	}

	@Override
	public void registerFont(String alias, File file) {
		fonts.put(alias, file);
	}
	@Override
	public File getFont(String alias) {
		if (!fonts.containsKey(alias)) {
			throw new RuntimeException("Font alias " + alias + " not found");
		}
		
		return fonts.get(alias);
	}
	
	@Override
	public List<String> getAllFontAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.addAll(fonts.keySet());
		return aliases;
	}

	@Override
	public void registerColor(String alias, Color color) {
		colors.put(alias, color);
	}

	@Override
	public Color getColor(String alias) {
		if (!colors.containsKey(alias)) {
			throw new RuntimeException("Color alias " + alias + " not found");
		}
		return colors.get(alias);
	}

	@Override
	public void registerString(String alias, String string) {
		strings.put(alias,  string);
	}

	@Override
	public String getString(String alias) {
		if (!strings.containsKey(alias)) {
			throw new RuntimeException("String alias " + alias + " not found");
		}
		return strings.get(alias);
	}
	
	
}
