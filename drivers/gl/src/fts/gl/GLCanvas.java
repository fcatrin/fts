package fts.gl;

import java.io.File;

import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Image;
import fts.graphics.Point;

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
	public void setForeground(Color color) {
		GLNativeInterface.setColor(color.r, color.g, color.b, color.a);
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
		GLNativeInterface.setFont(font);
	}

	@Override
	public void drawRect(int x, int y, int width, int height, int radius) {
		GLNativeInterface.drawRect(x, y, width, height, radius);
	}

	@Override
	public void drawFilledRect(int x, int y, int width, int height, int radius) {
		GLNativeInterface.drawFilledRect(x, y, width, height, radius);
	}

	@Override
	public void drawText(int x, int y, int width, int height, String text) {
		GLNativeInterface.drawText(x, y, text);
	}

	@Override
	public Point getTextSize(String text) {
		int[] textSize = GLNativeInterface.getTextSize(text);
		return new Point(textSize[0], textSize[1]);
	}

	public boolean createFont(String alias, File fontFile) {
		return GLNativeInterface.createFont(alias, fontFile.getAbsolutePath());
	}

}
