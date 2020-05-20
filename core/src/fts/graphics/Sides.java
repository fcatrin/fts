package fts.graphics;

public class Sides {
	public int left, right, top, bottom;

	public Sides() {}

	public Sides(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public Sides clone() {
		return new Sides(left, right, top, bottom);
	}
	
	@Override
	public String toString() {
		return String.format("{class: Sides, l: %d, r: %d, t: %d, b: %d}",
				left, right, top, bottom);
	}

}