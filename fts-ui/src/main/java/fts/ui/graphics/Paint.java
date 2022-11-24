package fts.graphics;

public class Paint {
	int alpha = 255;
	Color color = null;
	int lineWidth = 1;
	boolean antialias = true;
	Font font = null;
	
	public Paint(){};
	
	public Paint(Color color) {
		setColor(color);
	}

	public void apply(Canvas canvas) {
		if (color!=null) canvas.setColor(color);
		canvas.setLineWidth(lineWidth);
		canvas.setAntialias(antialias);
		if (font!=null) canvas.setFont(font);
	}

	public Color getColor() {
		return color;
	}

	private void setColor(Color color) {
		this.color = color;
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
	}

}
