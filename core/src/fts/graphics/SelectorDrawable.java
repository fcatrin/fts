package fts.graphics;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.w3c.dom.Element;

import fts.core.Application;
import fts.core.xml.SimpleXML;

public class SelectorDrawable extends Drawable {
	List<String> filters = new ArrayList<String>();
	List<Drawable> drawables = new ArrayList<Drawable>();

	public void load(Element node) throws JSONException {
		List<Element> elements = SimpleXML.getElements(node);
		for(Element element : elements) {
			filters.add(createFilter(element, new String[] {"selected", "focused", "enabled", "pressed"}));
			drawables.add(Application.createDrawable(SimpleXML.getElement(element, "drawable")));
		}
	}
	
	private String createFilter(Element node, String names[]) throws JSONException {
		String descriptor = "";
		for(String name : names) {
			descriptor = descriptor + createFilter(node, name);
		}
		return descriptor;
	}
	
	private String createFilter(Element element, String name) {
		if (SimpleXML.getAttribute(element, name) == null) return "#";
		else return SimpleXML.getBoolAttribute(element, name)?"1":"0";
	}
	
	private Drawable selectDrawable() {
		String mask = createFilterMask();
		int i=0;
		for(String filter : filters) {
			if (filterMatch(filter, mask)) return drawables.get(i);
			i++;
		}
		return drawables.get(drawables.size()-1);
	}
	

	@Override
	public void draw(Canvas canvas) {
		Drawable selectedDrawable = selectDrawable();
		if (selectedDrawable == null) return;
		
		selectedDrawable.setBounds(bounds);
		selectedDrawable.draw(canvas);
	}

	@Override
	public void destroy() {
		for(Drawable drawable : drawables) {
			drawable.destroy();
		}
	}
	
}
