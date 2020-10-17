package fts.graphics;

import org.w3c.dom.Element;

import fts.core.Application;
import fts.core.xml.SimpleXML;

public class ImageDrawable extends Drawable {
	public enum ScaleType {fitCenter, cropCenter, fitXY}
	Image image;
	
	ScaleType scaleType = ScaleType.cropCenter;
	
	public ImageDrawable() {}

	public ImageDrawable(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		setProperty("src", SimpleXML.getAttribute(element, "src"));
	}

	public void setSrc(String src) {
		destroyCurrentImage();
		image = Application.createImage(src);
	}
	
	public void setImage(Image image) {
		destroyCurrentImage();
		this.image = image;
	}
	
	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (image == null) return;
		
		int width = bounds.width;
		int height = bounds.height;
		if (width == 0 || height == 0) return;
		
		int x = bounds.x;
		int y = bounds.y;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		if (scaleType == ScaleType.fitCenter) {
			float imageRatio = (float)imageWidth / imageHeight;
			float rectRatio  = (float)width / height;
			if (imageRatio >= rectRatio) {
				if (imageWidth != width) {
					imageWidth = width;
					imageHeight = (int)(width / imageRatio);
				}
			} else {
				if (imageHeight != height) {
					imageHeight = height;
					imageWidth = (int)(imageRatio * imageHeight);
				}
			}
		} else if (scaleType == ScaleType.cropCenter) {
			float imageRatio = (float)imageWidth / imageHeight;
			float rectRatio  = (float)width / height;
			if (imageRatio <= rectRatio) {
				if (imageWidth != width) {
					imageWidth = width;
					imageHeight = (int)(width / imageRatio);
				}
			} else {
				if (imageHeight != height) {
					imageHeight = height;
					imageWidth = (int)(imageRatio * imageHeight);
				}
			}			
		}
		
		if (scaleType == ScaleType.fitCenter || scaleType == ScaleType.cropCenter) {
			y += (height - imageHeight) / 2;
			x += (width - imageWidth) / 2;
		}
		
		canvas.viewStart(bounds.x, bounds.y, bounds.width, bounds.height);
		canvas.drawImage(image, x, y, imageWidth, imageHeight);
		canvas.viewEnd();
	}
	
	public Image getImage() {
		return image;
	}
	
	@Override
	public void destroy() {
		destroyCurrentImage();
	}

	private void destroyCurrentImage() {
		if (this.image==null) return;
		this.image.destroy();
		this.image = null;
	}
	
}
