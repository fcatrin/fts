package fts.gl;

import fts.ui.graphics.Image;

public class GLImage extends Image {
	private int handle;
	
	public GLImage(String name, byte []data) {
		super(name);
		
		handle = GLNativeInterface.createImage(data);
		int[] imageSize = GLNativeInterface.getImageSize(handle);
		setWidth(imageSize[0]);
		setHeight(imageSize[1]);
	}
	
	@Override
	public void destroy() {
		GLNativeInterface.destroyImage(handle);
		handle = 0;
	}

	public boolean isValid() {
		return handle > 0;
	}

	public int getHandle() {
		return handle;
	}

}
