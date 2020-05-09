package fts.gl;

import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Image;

public class GLCanvas extends Canvas {

	public GLCanvas(int width, int height) {
		super(width, height);
	}

	@Override
	protected Image createImage(int width, int height) {
		return null;
	}

	@Override
	public void drawImage(Image srcImage, int x, int y) {
	}

	@Override
	public void setForeground(Color foregroundColor) {
	}

	@Override
	public void setBackground(Color backgroundColor) {
	}

	@Override
	public void setLineWidth(int lineWidth) {
	}

	@Override
	public void setAntialias(boolean antialias) {
	}

	@Override
	public void setFont(Font font) {
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int radius) {
	}

	@Override
	public void drawFilledRect(int x, int y, int width, int height, int radius) {
	}

	@Override
	public void drawText(int x, int y, int width, int height, String text) {
	}

}
