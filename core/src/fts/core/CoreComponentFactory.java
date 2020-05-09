package fts.core;

import org.w3c.dom.Element;

import fts.graphics.Drawable;
import fts.graphics.Shape;
import fts.graphics.TextDrawable;

public abstract class CoreComponentFactory implements ComponentFactory {


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

}
