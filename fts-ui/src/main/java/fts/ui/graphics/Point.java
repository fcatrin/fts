package fts.ui.graphics;

public class Point {
	public int x;
	public int y;

	public Point() {}

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("{class: %s, x:%d, y:%d}", Point.class.getName(), x, y);
	}
}
