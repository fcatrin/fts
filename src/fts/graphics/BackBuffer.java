package fts.graphics;

public abstract class BackBuffer {
	int lastWidth = 0;
	int lastHeight = 0;
	Canvas canvas = null;
	
	public Canvas get(int width, int height) {
		if (canvas == null || width != lastWidth || height != lastHeight) {
			dispose();

			canvas = createCanvas(width, height);
			lastWidth = width;
			lastHeight = height;
		}
		
		return canvas;
	}
	
	public void dispose() {
		canvas.dispose();
		canvas = null;
	}
	
	protected abstract Canvas createCanvas(int width, int height);
	
	public void draw(Canvas canvas) {
		canvas.drawImage(canvas.getImage(), 0, 0);
	}
}
