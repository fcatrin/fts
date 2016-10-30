package fts.core;

import fts.views.View;

public interface ComponentFactory {
	public Window createWindow();
	public View createComponent(String name);
}
