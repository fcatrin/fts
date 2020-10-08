package fts.graphics;

public abstract class Canvas {
	
	Align align = new Align();
	Image image = null;
	
	public Canvas(int width, int height) {
		image = createImage(width, height);
	}
	
	protected abstract Image createImage(int width, int height);
	 
	public Image getImage() {
		return image;
	}
	
	public void dispose() {
		image.dispose();
	}

	public abstract void viewStart(int x, int y, int width, int height);
	public abstract void viewEnd();
	
	public abstract void drawImage(Image srcImage, int x, int y);
	public abstract void setColor(Color color);
	public abstract void setLineWidth(int lineWidth);
	public abstract void setAntialias(boolean antialias);
	public abstract void setFont(Font font);
	
	public abstract TextMetrics getTextSize(String text);
	public abstract TextWrapper getTextWrap(String text, int width, int maxLines);
	
	final public void drawRect(int x, int y, int width, int height, int strokeWidth) {
		drawRoundedRect(x, y, width, height, 0, strokeWidth);
	}
	
	final public void drawFilledRect(int x, int y, int width, int height) {
		drawFilledRect(x, y, width, height, 0);
	}

	public abstract void drawRoundedRect(int x, int y, int width, int height, int radius, int strokeWidth);
	public abstract void drawFilledRect(int x, int y, int width, int height, int radius);
	public abstract void drawText(int x, int y, int width, int height, String text);
	public abstract void drawGradientRect(int x, int y, int width, int height, int radius,
			int angle, Color start, Color end);
	
	public abstract void drawLine(int x, int y, int dx, int dy);
}
