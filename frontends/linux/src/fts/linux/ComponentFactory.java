package fts.linux;

import fts.gl.GLFactory;

public class ComponentFactory extends GLFactory {

	@Override
	public Window createWindow() {
		return new Window(320, 240);
	}

}
