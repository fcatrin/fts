package fts.android;

import fts.core.NativeWindow;
import fts.gl.GLFactory;
import fts.gl.GLWindow;
import fts.graphics.BackBuffer;

public class ComponentFactory extends GLFactory {

	@Override
	public NativeWindow createNativeWindow(String title, int width, int height, int flags) {
		return new GLWindow();
	}

	@Override
	public BackBuffer createBackBuffer(String id, int width, int height) {
		return super.createBackBuffer(id, width, height);
	}

}
