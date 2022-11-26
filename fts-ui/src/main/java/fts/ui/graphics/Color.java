package fts.ui.graphics;

import fts.core.CoreUtils;

public class Color {
	public int r, g, b, a;

	public Color(int r, int g, int b, int a) {
		init(r, g, b, a);
	}

	public static Color load(String spec) {
		if (spec == null) return null;
		return new Color(spec);
	}
	
	public Color(String spec) {
		boolean hasAlpha = spec.length()>7;
		int base = hasAlpha ? 3 : 1;
		int a = hasAlpha ? CoreUtils.strHex2i(spec.substring(1, 3)) : 255;
		int r = CoreUtils.strHex2i(spec.substring(base+0, base+2));
		int g = CoreUtils.strHex2i(spec.substring(base+2, base+4));
		int b = CoreUtils.strHex2i(spec.substring(base+4, base+6));
		init(r, g, b, a);
	}
	
	private void init(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
