package fts.graphics;

public class Rectangle {
	public int x;
	public int y;
	public int width;
	public int height;

	public Rectangle() {}
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String toString() {
		return String.format("{class: Rect, x: %d, y: %d, width: %d, height: %d}",
				x, y, width, height);
	}
	
}
