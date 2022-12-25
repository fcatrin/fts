package fts.ui;

import fts.core.SharedPreferences;

public class Snippet<T extends Window> {

	protected final T window;

	public Snippet(T window) {
		this.window = window;
	}
	
	public void loadPreferences(SharedPreferences prefs) {}
	public void savePreferences(SharedPreferences prefs) {}
	
	protected <W extends Widget> W findWidget(String id) {
		return window.findWidget(id);
	}
	
	protected Widget inflate(String id) {
		return window.inflate(id);
	}

	public Widget getRootView() {
		return null;
	}

	public void open() {
		getRootView().setVisibility(Widget.Visibility.Visible);
	}

	public void close() {
		getRootView().setVisibility(Widget.Visibility.Gone);
	}
}
