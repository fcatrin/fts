package fts.demo.gl;

import fts.core.Window;
import fts.gl.GLFactory;

public class LinuxGLFactory extends GLFactory {

	@Override
	public Window createWindow() {
		return new LinuxWindow(320, 240);
	}

}
