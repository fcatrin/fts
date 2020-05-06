package fts.swt.graphics;

import fts.graphics.BackBuffer;
import fts.graphics.Canvas;

public class SWTBackBuffer extends BackBuffer {

	@Override
	protected Canvas createCanvas(int width, int height) {
		return new SWTCanvas(width, height);
	}

}
