package fts.core;

import fts.graphics.Point;

public abstract class NativeView {

	public abstract Point getTextSize(String s);

	public abstract void setBounds(int x, int y, int width, int height);
}
