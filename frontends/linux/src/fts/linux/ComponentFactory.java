package fts.linux;

import fts.gl.GLFactory;

public class ComponentFactory extends GLFactory {

	@Override
	public Window createNativeWindow(String title, int width, int height) {
		return new Window(title, width, height);
	}

}
