package fts.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TintableImageView extends ImageView {

	private ColorStateList colorStateList;

	public TintableImageView(Context context) {
		super(context);
	}
	
	public TintableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	public void setColorStateList(ColorStateList colorStateList) {
		this.colorStateList = colorStateList;
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (colorStateList != null && colorStateList.isStateful())
			updateTintColor();
	}

	private void updateTintColor() {
		int color = colorStateList.getColorForState(getDrawableState(), 0);
		setColorFilter(color);
	}
}
