package fts.graphics;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import fts.core.Application;
import fts.core.xml.SimpleXML;

public class SelectorDrawable extends Drawable {
	private static final String filterNames [] = {"selected", "focused", "enabled", "pressed"};
	private boolean state[] = new boolean[filterNames.length];

	List<String> filters = new ArrayList<String>();
	List<Drawable> drawables = new ArrayList<Drawable>();

	public SelectorDrawable() {}
	
	public SelectorDrawable(Element node) {
		load(node);
	}
	
	public void load(Element node) {
		List<Element> elements = SimpleXML.getElements(node, "item");
		for(Element element : elements) {
			filters.add(createFilter(element, filterNames));
			String drawable = SimpleXML.getAttribute(element, "drawable");
			if (drawable!=null) {
				drawables.add(Application.loadDrawable(drawable));
			} else {
				List<Element> childElements = SimpleXML.getElements(element);
				if (childElements.size() != 1) throw new RuntimeException("Wrong number of drawable elements on selector " + element);
				
				drawables.add(Application.createDrawable(childElements.get(0))); 
			}
		}
	}
	
	private String createFilter(Element node, String names[]) {
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

	public void setState(int index, boolean value) {
		state[index] = value;
	}

	public void setState(boolean[] state) {
		for(int i=0; i<this.state.length && i<state.length; i++) {
			this.state[i] = state[i];
		}
	}

	private String createFilterMask() {
		String mask = "";
		for (boolean stateValue : state) {
			mask = mask + (stateValue?"1":0);
		}
		return mask;
	}
	
	private boolean filterMatch(String filter, String mask) {
		for(int i=0; i<filter.length() && i<mask.length(); i++) {
			char f = filter.charAt(i);
			if (f == '#') continue;
			
			char m = mask.charAt(i);
			if (f!=m) return false;
			
		}
		return true;
	}

	@Override
	public void destroy() {
		for(Drawable drawable : drawables) {
			drawable.destroy();
		}
	}
	
}
