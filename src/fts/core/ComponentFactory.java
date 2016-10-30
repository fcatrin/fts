package fts.core;

import org.w3c.dom.Node;

import fts.views.View;

public interface ComponentFactory {
	public Window createWindow();
	public View createView(String name, Node node);
}
