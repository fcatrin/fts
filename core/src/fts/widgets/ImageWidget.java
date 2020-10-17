package fts.widgets;

import fts.core.NativeWindow;
import fts.core.Widget;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Image;
import fts.graphics.ImageDrawable;
import fts.graphics.ImageDrawable.ScaleType;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public class ImageWidget extends Widget {
	ImageDrawable imageDrawable;
	
	public ImageWidget(NativeWindow w) {
		super(w);
		imageDrawable = new ImageDrawable();
	}
	
	public void setSrc(String src) {
		imageDrawable.setSrc(src);
		invalidate();
	}
	
	public void setImage(Image image) {
		imageDrawable.setImage(image);
		invalidate();
	}
	
	public void setScaleType(ScaleType scaleType) {
		imageDrawable.setScaleType(scaleType);
		invalidate();
	}
	
	@Override
	protected void onPaint(PaintEvent e) {
		super.onPaint(e);
		Rectangle paintBounds = getInternalPaintBounds(bounds.width, bounds.height);

		Canvas canvas = e.canvas;
		imageDrawable.setBounds(paintBounds);
		imageDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Point paddingSize = getPaddingSize();
		Image image = imageDrawable.getImage();
		if (image == null) return paddingSize;
		
		Point imageSize = new Point(image.getWidth(), image.getHeight());

		Point contentSize = new Point(
			imageSize.x + paddingSize.x,
			imageSize.y + paddingSize.y);
		
		return contentSize;
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("scaleType")) {
			try {
				return ScaleType.valueOf(value);
			} catch (Exception e) {
				throw new RuntimeException("Invalid ScaleType value " + value);
			}
		} else if (propertyName.equals("src")) {
			return value;
		}
		return super.resolvePropertyValue(propertyName, value);
	}

}
