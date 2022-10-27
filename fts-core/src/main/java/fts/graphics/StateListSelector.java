package fts.graphics;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public abstract class StateListSelector<T> {
	private static final String filterNames [] = {"selected", "focused", "enabled", "pressed"};
	private boolean state[] = new boolean[filterNames.length];

	List<String> filters = new ArrayList<String>();
	List<T> items = new ArrayList<T>();
	
	T singleValue;

	public StateListSelector() {}

	public StateListSelector(T value) {
		this.singleValue = value;
	}

	public StateListSelector(Element node) {
		load(node);
	}
	
	public void load(Element node) {
		List<Element> elements = SimpleXML.getElements(node, "item");
		for(Element element : elements) {
			filters.add(createFilter(element, filterNames));
			items.add(createItem(element));
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
	
	public T getSelectedItem() {
		if (items.isEmpty()) return singleValue;
		
		String mask = createFilterMask();
		int i=0;
		for(String filter : filters) {
			if (filterMatch(filter, mask)) return items.get(i);
			i++;
		}
		return items.get(items.size()-1);
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

	public void destroy() {
		for(T item : items) {
			destroyItem(item);
		}
	}
	
	protected abstract void destroyItem(T item);
	protected abstract T createItem(Element element); 
}
