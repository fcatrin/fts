package fts.graphics;

import fts.core.Utils;

public class Color {
	public int r, g, b, a;

	public Color(int r, int g, int b, int a) {
		init(r, g, b, a);
	}

	public Color(String color) {
		boolean hasAlpha = color.length()>7;
		int base = hasAlpha ? 3 : 1;
		int a = hasAlpha ? Utils.strHex2i(color.substring(1, 3)) : 255;
		int r = Utils.strHex2i(color.substring(base+0, base+2));
		int g = Utils.strHex2i(color.substring(base+2, base+4));
		int b = Utils.strHex2i(color.substring(base+4, base+6));
		init(r, g, b, a);
	}
	
	private void init(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void dispose() {}
}
