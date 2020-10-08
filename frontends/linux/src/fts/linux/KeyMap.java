package fts.linux;

import java.util.HashMap;
import java.util.Map;

import fts.core.Log;
import fts.events.KeyEvent;

public class KeyMap {
	private static final String LOGTAG = KeyMap.class.getSimpleName();
	private static final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	static {
		map.put(0x4000004b, KeyEvent.KEY_PAGE_DOWN);
		map.put(0x4000004e, KeyEvent.KEY_PAGE_UP);
		
		map.put(0x40000050, KeyEvent.KEY_DPAD_LEFT);
		map.put(0x4000004f, KeyEvent.KEY_DPAD_RIGHT);
		map.put(0x40000052, KeyEvent.KEY_DPAD_UP);
		map.put(0x40000051, KeyEvent.KEY_DPAD_DOWN);
		
		map.put(0x08, KeyEvent.KEY_BACKSPACE);
		map.put(0x0d, KeyEvent.KEY_ENTER);
		map.put(0x1b, KeyEvent.KEY_ESC);
		map.put(0x20, KeyEvent.KEY_SPACE);
		
		int sdl_a = 97;
		int sdl_z = 122;
		buildMap(sdl_a, sdl_z, KeyEvent.KEY_A);
		
		int sdl_0 = 48;
		int sdl_9 = 57;
		buildMap(sdl_0, sdl_9, KeyEvent.KEY_0);
	}
	
	private static void buildMap(int srcStart, int srcEnd, int baseKeyCode) {
		for(int keyCode=srcStart; keyCode<=srcEnd; keyCode++) {
			map.put(keyCode, keyCode - srcStart + baseKeyCode);
		}
	}
	
	public static int translate(int keyCode) {
		if (map.containsKey(keyCode)) return map.get(keyCode);
		Log.d(LOGTAG, String.format("Unknown keyCode %d 0x%04x ", keyCode, keyCode));
		return 0;
	}
}
