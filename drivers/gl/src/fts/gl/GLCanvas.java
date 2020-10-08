package fts.gl;

import java.io.File;

import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Image;
import fts.graphics.TextMetrics;
import fts.graphics.TextWrapper;

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
	public void setColor(Color color) {
		if (color==null) return;
		GLNativeInterface.setColor(color.r, color.g, color.b, color.a);
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
	public void drawRoundedRect(int x, int y, int width, int height, int radius, int strokeWidth) {
		GLNativeInterface.drawRect(x, y, width, height, radius, strokeWidth);
	}

	@Override
	public void drawFilledRect(int x, int y, int width, int height, int radius) {
		GLNativeInterface.drawFilledRect(x, y, width, height, radius);
	}
	
	@Override
	public void drawGradientRect(int x, int y, int width, int height, int radius, int angle, Color start, Color end) {
		GLNativeInterface.drawGraidentRect(x, y, width, height, radius, 
				angle, 
				start.r, start.g, start.b, start.a, 
				end.r, end.g, end.b, end.a);
	}

	@Override
	public void drawText(int x, int y, int width, int height, String text) {
		GLNativeInterface.drawText(x, y, text);
	}

	@Override
	public TextMetrics getTextSize(String text) {
		int[] textSizeInfo = GLNativeInterface.getTextSize(text);
		TextMetrics textMetrics = new TextMetrics();
		textMetrics.width = textSizeInfo[0];
		textMetrics.height = textSizeInfo[1];
		textMetrics.ascent = textSizeInfo[2];
		textMetrics.descent = textSizeInfo[3];
		return textMetrics;
	}
	
	public boolean createFont(String alias, File fontFile) {
		return GLNativeInterface.createFont(alias, fontFile.getAbsolutePath());
	}

	@Override
	public TextWrapper getTextWrap(String text, int width, int maxLines) {
		TextWrapper wrapper = new TextWrapper(this, text);
		wrapper.wrap(width, maxLines);
		return wrapper;
	}

	@Override
	public void viewStart(int x, int y, int width, int height) {
		GLNativeInterface.viewStart(x, y, width, height);
	}

	@Override
	public void viewEnd() {
		GLNativeInterface.viewEnd();
	}

	@Override
	public void drawLine(int x, int y, int dx, int dy) {
		GLNativeInterface.drawLine(x, y, dx, dy);
	}

}
