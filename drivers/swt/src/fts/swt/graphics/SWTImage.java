package fts.swt.graphics;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import fts.graphics.Image;

public class SWTImage extends Image {

	private org.eclipse.swt.graphics.Image image;

	public SWTImage(int width, int height) {
		super(width, height);
		this.image = new org.eclipse.swt.graphics.Image(Display.getCurrent(), width, height);
	}
	
	public GC createGC() {
		return new GC(image);
	}

	@Override
	public void dispose() {
		if (image!=null) {
			image.dispose();
			image = null;
		}
	}
	
	public org.eclipse.swt.graphics.Image getNativeImage() {
		return image;
	}

}
