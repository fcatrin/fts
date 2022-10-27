package fts.graphics;

import fts.core.Context;

public class Dimension {
	public static int pt2px(int pt) {
		return Context.getInstance().pt2px(pt);
	}
	
	public static int px2pt(int px) {
		return Context.getInstance().px2pt(px);
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
