package fts.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Image;
import fts.swt.SWTUtils;

public class SWTCanvas extends Canvas {
	private GC gc = null;
	private SWTImage image = null;

	public SWTCanvas(int width, int height) {
		super(width, height);
	}

	@Override
	protected Image createImage(int width, int height) {
		image =  new SWTImage(width, height);
		gc = image.createGC();
		return image;
	}

	@Override
	public void drawImage(Image srcImage, int x, int y) {
		if (srcImage instanceof SWTImage) {
			SWTImage swtImage = (SWTImage) srcImage;
			gc.drawImage(swtImage.getNativeImage(), x, y);
		}
	}
	
	@Override
	public void dispose() {
		if (gc!=null) gc.dispose();
		gc = null;
		
		if (image!=null) image.dispose();
		image = null;
	}

	@Override
	public void setForeground(Color color) {
		if (gc == null) return;
		gc.setForeground(new org.eclipse.swt.graphics.Color(null, color.r, color.g, color.b));
		gc.setAlpha(color.a);
	}

	@Override
	public void setBackground(Color color) {
		if (gc == null) return;
		gc.setBackground(new org.eclipse.swt.graphics.Color(null, color.r, color.g, color.b));
	}

	@Override
	public void setLineWidth(int lineWidth) {
		if (gc == null) return;
		gc.setLineWidth(lineWidth);
		
	}

	@Override
	public void setAntialias(boolean antialias) {
		if (gc == null) return;
		gc.setAntialias(antialias ? SWT.ON : SWT.OFF);
	}

	@Override
	public void setFont(Font font) {
		if (gc == null) return;
		org.eclipse.swt.graphics.Font swtFont = SWTUtils.buildFont(font.name, font.size, SWT.NORMAL);
		gc.setFont(swtFont);
	}

}
