package fts.ui.graphics;

import fts.ui.AppContext;

public class Dimension {
	public static int pt2px(int pt) {
		return AppContext.getInstance().pt2px(pt);
	}
	
	public static int px2pt(int px) {
		return AppContext.getInstance().px2pt(px);
	}
	
	public static int parse(String spec) {
		if (spec == null || spec.length() == 0) return 0;
		
		if (spec.endsWith("pt")) {
			int pt = Integer.parseInt(spec.substring(0, spec.length()-2));
			return pt2px(pt);
		}
		if (spec.endsWith("px")) {
			return Integer.parseInt(spec.substring(0, spec.length()-2));
		}
		throw new RuntimeException("Invalid dimension " + spec);
	}
}
