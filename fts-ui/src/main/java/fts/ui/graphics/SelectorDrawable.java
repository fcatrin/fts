package fts.graphics;

import java.util.List;

import org.w3c.dom.Element;

import fts.ui.Application;
import fts.core.xml.SimpleXML;

public class SelectorDrawable extends Drawable {
	private final StateSelectorDrawable drawableSelector = new StateSelectorDrawable();

	public SelectorDrawable() {}
	
	public SelectorDrawable(Element node) {
		load(node);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Drawable selectedDrawable = drawableSelector.getSelectedItem();
		if (selectedDrawable == null) return;
		
		selectedDrawable.setBounds(bounds);
		selectedDrawable.draw(canvas);
	}
	
	static class StateSelectorDrawable extends StateListSelector<Drawable> {

		@Override
		protected void destroyItem(Drawable drawable) {
			drawable.destroy();
		}

		@Override
		protected Drawable createItem(Element element) {
			String drawable = SimpleXML.getAttribute(element, "drawable");
			if (drawable!=null) {
				return Application.loadDrawable(drawable);
			} else {
				List<Element> childElements = SimpleXML.getElements(element);
				if (childElements.size() != 1) throw new RuntimeException("Wrong number of drawable elements on selector " + element);
				
				return Application.createDrawable(childElements.get(0)); 
			}
		}
	}

	@Override
	public void load(Element element) {
		drawableSelector.load(element);
	}

	public void setState(int ordinal, boolean value) {
		drawableSelector.setState(ordinal, value);
	}
	
}
