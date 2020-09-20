package fts.core;

import fts.graphics.Canvas;

public abstract class BackBuffer {
	int width;
	int height;
	
	public abstract void create(int width, int height);
	public abstract void bind();
	public abstract void draw(Canvas canvas);
	public abstract void unbind();
	public abstract void destroy();
	
}
