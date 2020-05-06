package fts.graphics;

public abstract class Canvas {
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

	public abstract void drawImage(Image srcImage, int x, int y);
	public abstract void setForeground(Color foregroundColor);
	public abstract void setBackground(Color backgroundColor);
	public abstract void setLineWidth(int lineWidth);
	public abstract void setAntialias(boolean antialias);
	public abstract void setFont(Font font);
}
