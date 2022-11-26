package fts.ui.widgets;

import fts.ui.Widget;
import fts.ui.Window;
import fts.ui.events.PaintEvent;
import fts.ui.graphics.Canvas;
import fts.ui.graphics.Image;
import fts.ui.graphics.ImageDrawable;
import fts.ui.graphics.ImageDrawable.ScaleType;
import fts.ui.graphics.Point;
import fts.ui.graphics.Rectangle;

public class ImageWidget extends Widget {
	ImageDrawable imageDrawable;
	
	public ImageWidget(Window w) {
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
