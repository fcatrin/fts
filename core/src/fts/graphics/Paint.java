package fts.graphics;

public class Paint {
	int alpha = 255;
	Color foregroundColor = null;
	Color backgroundColor = null;
	int lineWidth = 1;
	boolean antialias = true;
	Font font = null;
	
	public Paint(){};
	
	public Paint(Color color) {
		setForegroundColor(color);
		setBackgroundColor(color);
	}

	public Paint(Color foregroundColor, Color backgroundColor) {
		setForegroundColor(foregroundColor);
		setBackgroundColor(backgroundColor);
	}

	public void apply(Canvas canvas) {
		if (foregroundColor!=null) canvas.setForeground(foregroundColor);
		if (backgroundColor!=null) canvas.setBackground(backgroundColor);
		canvas.setLineWidth(lineWidth);
		canvas.setAntialias(antialias);
		if (font!=null) canvas.setFont(font);
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	private void setForegroundColor(Color color) {
		foregroundColor = color;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public boolean isAntialias() {
		return antialias;
	}

	public void setAntialias(boolean antialias) {
		this.antialias = antialias;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	
	public void dispose() {
		if (font!=null) font.dispose();
	}

}
