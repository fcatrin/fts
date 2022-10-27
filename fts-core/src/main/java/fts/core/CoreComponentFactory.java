package fts.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import fts.graphics.Color;
import fts.graphics.ColorListSelector;
import fts.graphics.Drawable;
import fts.graphics.SelectorDrawable;
import fts.graphics.Shape;
import fts.graphics.TextDrawable;

public abstract class CoreComponentFactory implements ComponentFactory {

	private static Map<String, File>   fonts   = new HashMap<String, File>();
	private static Map<String, ColorListSelector>  colors  = new HashMap<String, ColorListSelector>();
	private static Map<String, String> strings = new HashMap<String, String>();
	private static Map<String, String> dimen   = new HashMap<String, String>();
	private static Map<String, Style>  styles  = new HashMap<String, Style>();

	@Override
	public Widget createWidget(Element node) {
		return null;
	}

	@Override
	public Drawable createDrawable(Element node) {
		String name = node.getNodeName();
		if (name.equals("shape")) {
			return new Shape(node);
		} else if (name.equals("selector")) {
			return new SelectorDrawable(node);
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
	public void registerColor(String alias, ColorListSelector color) {
		colors.put(alias, color);
	}

	@Override
	public ColorListSelector getColor(String value) {
		if (value.startsWith("#")) {
			return new ColorListSelector(new Color(value));
		} else if (value.startsWith("@color/")) {
			String alias = value.substring("@color/".length());
			if (!colors.containsKey(alias)) {
				ColorListSelector colorResource = Application.loadColorResource(alias);
				if (colorResource!=null) return colorResource;
			} else {
				return colors.get(alias);
			}
		}
		throw new RuntimeException("Invalid color value " + value);
	}

	@Override
	public void registerStyle(String alias, Style style) {
		styles.put(alias, style);
	}

	@Override
	public Style getStyle(String alias) {
		if (!styles.containsKey(alias)) {
			throw new RuntimeException("Style alias " + alias + " not found");
		}
		return styles.get(alias);
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
	
	@Override
	public void registerDimen(String alias, String value) {
		dimen.put(alias,  value);
	}

	@Override
	public String getDimen(String alias) {
		if (!dimen.containsKey(alias)) {
			throw new RuntimeException("Dimen " + alias + " not found");
		}
		return dimen.get(alias);
	}
	
}
