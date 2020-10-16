package fts.widgets;

import fts.core.NativeWindow;
import fts.core.Widget;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Image;
import fts.graphics.ImageDrawable;
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
	
	@Override
	protected void onPaint(PaintEvent e) {
		super.onPaint(e);
		Rectangle paintBounds = getInternalPaintBounds(bounds.width, bounds.height);
		
		// TODO consider scaling on paintBounds vs image size
		
		Canvas canvas = e.canvas;
		imageDrawable.setBounds(paintBounds);
		imageDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Point paddingSize = getPaddingSize();
		Image image = imageDrawable.getImage();
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
}
