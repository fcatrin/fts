package fts.core;

public class Application {
	ComponentFactory factory;
	
	public Application(ComponentFactory factory) {
		this.factory = factory;
	}
	
	public Window createWindow() {
		return factory.createWindow();
	}
}
