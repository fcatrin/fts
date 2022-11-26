package fts.ui.widgets;

import fts.core.Utils;
import fts.ui.Widget;
import fts.ui.Window;
import fts.ui.events.OnProgressChangedListener;
import fts.ui.events.PaintEvent;
import fts.ui.events.TouchEvent;
import fts.ui.graphics.Drawable;
import fts.ui.graphics.Point;
import fts.ui.graphics.Rectangle;

public class ProgressWidget extends Widget {
	private static final int TOUCH_DELAY = 200;

	private int minHeight;
	private long progress;
	private long total;
	
	long touchTime = 0;
	boolean tracking = false;
	
	private OnProgressChangedListener onProgressChangedListener;
	
	private Drawable progressDrawable;
	private Drawable progressBackgroundDrawable;
	
	public ProgressWidget(Window window) {
		super(window);
		minHeight = this.resolvePropertyValueDimen("minHeight", "20pt");
	}
	
	public void setProgressDrawable(Drawable drawable) {
		this.progressDrawable = drawable;
		invalidate();
	}

	public void setProgressBackgroundDrawable(Drawable drawable) {
		this.progressBackgroundDrawable = drawable;
		invalidate();
	}

	public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
		this.onProgressChangedListener = onProgressChangedListener;
	}

	public void setProgress(long progress) {
		if (this.progress == progress || tracking) return;
		this.progress = progress;
		invalidate();
		
		if (onProgressChangedListener!=null) onProgressChangedListener.onProgressChanged(this, progress, total, false, false);
	}

	public void setTotal(long total) {
		if (this.total == total) return;
		this.total = total;
		invalidate();
	}

	@Override
	protected void onPaint(PaintEvent e) {
		super.onPaint(e);

		Rectangle paintBounds = getPaintBounds().clone();
		paintBounds.x += padding.left;
		paintBounds.y += padding.top;
		paintBounds.width  -= padding.left + padding.right;
		paintBounds.height -= padding.top + padding.bottom;
		
		if (progressBackgroundDrawable!=null) {
			progressBackgroundDrawable.setBounds(paintBounds);
			progressBackgroundDrawable.draw(e.canvas);
		}
		
		if (progressDrawable == null || total <= 0 || progress <= 0) return;
		
		float ratio = (float)progress / total;
		
		paintBounds.width *= ratio;
		progressDrawable.setBounds(paintBounds);
		progressDrawable.draw(e.canvas);
	}
	
	@Override
	protected void onTouchDown(TouchEvent e) {
		super.onTouchDown(e);
		touchTime = System.currentTimeMillis();
		tracking = true;
	}

	@Override
	protected void onTouchMove(TouchEvent e) {
		super.onTouchMove(e);
		if (System.currentTimeMillis() - touchTime > TOUCH_DELAY && tracking) {
			adjustProgress(e.x, false);
		}
	}

	@Override
	protected void onTouchUp(TouchEvent e) {
		super.onTouchUp(e);
		if (!tracking) return;
		tracking = false;
		adjustProgress(e.x, true);
	}

	@Override
	protected void onTouchExit(TouchEvent e) {
		super.onTouchExit(e);
		if (!tracking) return;
		tracking = false;
		adjustProgress(e.x, true);
	}

	private void adjustProgress(int x, boolean forceUpdate) {
		int traversalWidth = bounds.width - padding.left - padding.right;
		if (traversalWidth <= 0) return;
		
		x -= bounds.x + padding.left;

		if (x < 0) x = 0;
		if (x > traversalWidth) x = traversalWidth;
		
		int newProgress = (int)(((float)x / traversalWidth) * total);
		if (!forceUpdate && progress == newProgress) return;
		
		progress = newProgress;
		invalidate();
		if (onProgressChangedListener!=null) onProgressChangedListener.onProgressChanged(this, progress, total, true, tracking);
	}

	@Override
	public Point getContentSize(int width, int height) {
		return new Point(width, Math.min(minHeight, height));
	}
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("progressDrawable") || propertyName.equals("progressBackgroundDrawable") ) {
			return resolveBackground(value);
		} else if (propertyName.equals("progress") || propertyName.equals("total")) {
			return Utils.str2l(value);
		}
		return super.resolvePropertyValue(propertyName, value);
	}


}
