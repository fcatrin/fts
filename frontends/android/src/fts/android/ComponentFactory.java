package fts.android;

import fts.core.NativeWindow;
import fts.gl.GLFactory;
import fts.graphics.BackBuffer;

public class ComponentFactory extends GLFactory {

	@Override
	public NativeWindow createNativeWindow(String title, int width, int height, int flags) {
		return new AndroidWindow();
	}

	@Override
	public BackBuffer createBackBuffer(int width, int height) {
		return super.createBackBuffer(width, height);
	}

}
