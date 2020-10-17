package fts.graphics;

public abstract class BackBuffer {
	int width;
	int height;
	
	public abstract void create(String id, int width, int height);
	public abstract void bind();
	public abstract void draw(Canvas canvas, int x, int y);
	public abstract void unbind();
	public abstract void destroy();
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	protected void setWidth(int width) {
		this.width = width;
	}
	protected void setHeight(int height) {
		this.height = height;
	}
	
}
