package fts.widgets;

import fts.core.NativeWindow;
import fts.core.Utils;
import fts.core.Widget;
import fts.events.PaintEvent;
import fts.graphics.Drawable;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public class ProgressWidget extends Widget {

	private int minHeight;
	private long progress;
	private long total;
	
	private Drawable drawable;
	
	public ProgressWidget(NativeWindow window) {
		super(window);
		minHeight = this.resolvePropertyValueDimen("minHeight", "10pt");
	}
	
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
		invalidate();
	}
	
	public void setProgress(long progress) {
		if (this.progress == progress) return;
		this.progress = progress;
		invalidate();
	}

	public void setTotal(long total) {
		if (this.total == total) return;
		this.total = total;
		invalidate();
	}

	@Override
	protected void onPaint(PaintEvent e) {
		super.onPaint(e);
		if (drawable == null || total <= 0 || progress <= 0) return;
		
		float ratio = (float)progress / total;
		
		Rectangle paintBounds = getPaintBounds().clone();
		paintBounds.width *= ratio;
		
		drawable.setBounds(paintBounds);
		drawable.draw(e.canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		return new Point(width, Math.min(minHeight, height));
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("drawable")) {
			return resolveBackground(value);
		} else if (propertyName.equals("progress") || propertyName.equals("total")) {
			return Utils.str2l(value);
		}
		return super.resolvePropertyValue(propertyName, value);
	}


}
