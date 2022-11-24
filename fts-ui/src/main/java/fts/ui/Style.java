package fts.ui;

import java.util.HashMap;
import java.util.Map;

public class Style {
	String parent;
	String name;
	Map<String, String> attributes = new HashMap<String, String>();
	
	public Style(String name) {
		this.name = name;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public void setAttribute(String name, String value) {
		attributes.put(name,  value);
	}
	
	public Map<String, String> getAttributes() {
		Map<String, String> result = new HashMap<String, String>();
		fillAttributes(result);
		return result;
	}

	private void fillAttributes(Map<String, String> result) {
		if (parent!=null) {
			Style parentStyle = Application.getFactory().getStyle(parent);
			parentStyle.fillAttributes(result);
		}
		result.putAll(attributes);
	}
	
}
