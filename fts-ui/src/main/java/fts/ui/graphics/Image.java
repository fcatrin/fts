package fts.ui.graphics;

public class Image {
	int width;
	int height;
	private String name;
	
	public Image(String name) {
		this.name = name;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}
	
	public void destroy() {};

}
