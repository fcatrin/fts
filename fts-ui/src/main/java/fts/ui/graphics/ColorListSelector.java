package fts.ui.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;
import fts.ui.Application;

public class ColorListSelector extends StateListSelector<Color> {

	public ColorListSelector(Color value) {
		super(value);
	}

	public ColorListSelector(Element node) {
		super(node);
	}

	@Override
	protected void destroyItem(Color color) {}

	@Override
	protected Color createItem(Element element) {
		String colorSpec = SimpleXML.getAttribute(element, "color");
		return Application.factory.getColor(colorSpec).getSelectedItem();
	}

}
