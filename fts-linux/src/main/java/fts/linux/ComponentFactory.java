package fts.linux;

import fts.core.NativeWindow;
import fts.gl.GLFactory;
import fts.gl.GLWindow;

public class ComponentFactory extends GLFactory {

	@Override
	public NativeWindow createNativeWindow(String title, int width, int height, int flags) {
		return new GLWindow();
	}

}
