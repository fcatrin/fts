package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Font {
	public String name;
	public int size;
	
	public static Font load(Element element) {
		Font font = new Font();
		font.name = SimpleXML.getAttribute(element, "font");
		font.size = Dimension.parse(SimpleXML.getAttribute(element, "font-size"));
		return font;
	}
}
