package fts.android.core;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class AndroidFonts {
	private static final String LOGTAG = AndroidFonts.class.getSimpleName();
	
	private static Map<String, String>   namedFonts = new HashMap<String, String>();
	private static Map<String, Typeface> knownFonts = new HashMap<String, Typeface>();
	
	private AndroidFonts() {
	}
	
	public static void clearNamedFonts() {
		namedFonts.clear();
	}
	
	public static void clearKnownFonts() {
		knownFonts.clear();
	}
	
	public static void addNamedFont(String name, String spec) {
		namedFonts.put(name, spec);
	}
	
	public static String getNamedFont(String name) {
		return namedFonts.get(name);
	}
	
	public static Typeface getFont(Context context, String name) {
		String namedFont = namedFonts.get(name);
		
		if (namedFont!=null) name = namedFont;
		
		Typeface font = knownFonts.get(name);
		if (font == null) {
			if (name.startsWith("/")) {
				font = Typeface.createFromFile(name);
			} else {
				font = Typeface.createFromAsset(context.getAssets(),"fonts/" + name);
			}
			knownFonts.put(name, font);
		}
		//Log.d(LOGTAG, "typeface for " + name + " is " + (font == null?"null":"not null"));
		return font;
	}
	
	public static void setViewFont(View v, File file) {
		setViewFont(v, file.getAbsolutePath());
	}
	
	public static void setViewFont(View v, String name) {
		if (!(v instanceof TextView)) {
			Log.d(LOGTAG, "view " + v.getId() + " is not a TextView");
			return;
		}
		TextView tv = (TextView)v;
		Typeface tf = getFont(v.getContext(), name);
		tv.setTypeface(tf);
	}

	public static void setViewFontRecursive(View v, String name) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)v;
			for(int i=0; i<vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setViewFontRecursive(child, name);
			}
		} else {
			setViewFont(v, name);
		}
	}
}
