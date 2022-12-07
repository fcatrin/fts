package fts.linux;

import fts.gl.GLFactory;
import fts.gl.GLWindow;
import fts.ui.Window;

public class ComponentFactory extends GLFactory {

	@Override
	public Window createNativeWindow(String title, int width, int height, int flags) {
		throw new RuntimeException("Not required in this implementation. You need to extend DesktopWindow");
	}

}
