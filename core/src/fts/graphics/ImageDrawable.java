package fts.graphics;

import org.w3c.dom.Element;

import fts.core.Application;
import fts.core.xml.SimpleXML;

public class ImageDrawable extends Drawable {
	Image image;
	
	public ImageDrawable() {}

	public ImageDrawable(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		setProperty("src", SimpleXML.getAttribute(element, "src"));
	}

	public void setSrc(String src) {
		image = Application.createImage(src);
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public Image getImage() {
		return image;
	}
	
	@Override
	public void destroy() {
		image.destroy();
	}
	
}
