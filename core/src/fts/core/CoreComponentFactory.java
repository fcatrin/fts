package fts.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import fts.graphics.Drawable;
import fts.graphics.Shape;
import fts.graphics.TextDrawable;

public abstract class CoreComponentFactory implements ComponentFactory {

	private static Map<String, File> fonts = new HashMap<String, File>();

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
		if (fonts.containsKey(alias)) {
			throw new RuntimeException("Font alias not found " + alias);
		}
		
		return fonts.get(alias);
	}
	
	@Override
	public List<String> getAllFontAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.addAll(fonts.keySet());
		return aliases;
	}
}
