package fts.android;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import fts.ui.events.KeyEvent;

public class AndroidKeyMap {
	private static final String LOGTAG = AndroidKeyMap.class.getSimpleName();
	
	static Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	static {
		map.put(android.view.KeyEvent.KEYCODE_PAGE_DOWN, KeyEvent.KEY_PAGE_DOWN);
		map.put(android.view.KeyEvent.KEYCODE_PAGE_UP,   KeyEvent.KEY_PAGE_UP);
		
		map.put(android.view.KeyEvent.KEYCODE_DPAD_LEFT,  KeyEvent.KEY_DPAD_LEFT);
		map.put(android.view.KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEY_DPAD_RIGHT);
		map.put(android.view.KeyEvent.KEYCODE_DPAD_UP,    KeyEvent.KEY_DPAD_UP);
		map.put(android.view.KeyEvent.KEYCODE_DPAD_DOWN,  KeyEvent.KEY_DPAD_DOWN);
		
		map.put(android.view.KeyEvent.KEYCODE_BACK, KeyEvent.KEY_BACKSPACE);
		map.put(android.view.KeyEvent.KEYCODE_ENTER, KeyEvent.KEY_ENTER);
		map.put(android.view.KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEY_ESC);
		map.put(android.view.KeyEvent.KEYCODE_SPACE, KeyEvent.KEY_SPACE);

		map.put(android.view.KeyEvent.KEYCODE_A, KeyEvent.KEY_A);
		map.put(android.view.KeyEvent.KEYCODE_B, KeyEvent.KEY_B);
		map.put(android.view.KeyEvent.KEYCODE_C, KeyEvent.KEY_C);
		map.put(android.view.KeyEvent.KEYCODE_D, KeyEvent.KEY_D);
		map.put(android.view.KeyEvent.KEYCODE_E, KeyEvent.KEY_E);
		map.put(android.view.KeyEvent.KEYCODE_F, KeyEvent.KEY_F);
		map.put(android.view.KeyEvent.KEYCODE_G, KeyEvent.KEY_G);
		map.put(android.view.KeyEvent.KEYCODE_H, KeyEvent.KEY_H);
		map.put(android.view.KeyEvent.KEYCODE_I, KeyEvent.KEY_I);
		map.put(android.view.KeyEvent.KEYCODE_J, KeyEvent.KEY_J);
		map.put(android.view.KeyEvent.KEYCODE_K, KeyEvent.KEY_K);
		map.put(android.view.KeyEvent.KEYCODE_L, KeyEvent.KEY_L);
		map.put(android.view.KeyEvent.KEYCODE_M, KeyEvent.KEY_M);
		map.put(android.view.KeyEvent.KEYCODE_N, KeyEvent.KEY_N);
		map.put(android.view.KeyEvent.KEYCODE_O, KeyEvent.KEY_O);
		map.put(android.view.KeyEvent.KEYCODE_P, KeyEvent.KEY_P);
		map.put(android.view.KeyEvent.KEYCODE_Q, KeyEvent.KEY_Q);
		map.put(android.view.KeyEvent.KEYCODE_R, KeyEvent.KEY_R);
		map.put(android.view.KeyEvent.KEYCODE_S, KeyEvent.KEY_S);
		map.put(android.view.KeyEvent.KEYCODE_T, KeyEvent.KEY_T);
		map.put(android.view.KeyEvent.KEYCODE_U, KeyEvent.KEY_U);
		map.put(android.view.KeyEvent.KEYCODE_V, KeyEvent.KEY_V);
		map.put(android.view.KeyEvent.KEYCODE_W, KeyEvent.KEY_W);
		map.put(android.view.KeyEvent.KEYCODE_X, KeyEvent.KEY_X);
		map.put(android.view.KeyEvent.KEYCODE_Y, KeyEvent.KEY_Y);
		map.put(android.view.KeyEvent.KEYCODE_Z, KeyEvent.KEY_Z);
		
		map.put(android.view.KeyEvent.KEYCODE_0, KeyEvent.KEY_0);
		map.put(android.view.KeyEvent.KEYCODE_1, KeyEvent.KEY_1);
		map.put(android.view.KeyEvent.KEYCODE_2, KeyEvent.KEY_2);
		map.put(android.view.KeyEvent.KEYCODE_3, KeyEvent.KEY_3);
		map.put(android.view.KeyEvent.KEYCODE_4, KeyEvent.KEY_4);
		map.put(android.view.KeyEvent.KEYCODE_5, KeyEvent.KEY_5);
		map.put(android.view.KeyEvent.KEYCODE_6, KeyEvent.KEY_6);
		map.put(android.view.KeyEvent.KEYCODE_7, KeyEvent.KEY_7);
		map.put(android.view.KeyEvent.KEYCODE_8, KeyEvent.KEY_8);
		map.put(android.view.KeyEvent.KEYCODE_9, KeyEvent.KEY_9);
	}

	public static KeyEvent translate(android.view.KeyEvent androidEvent) {
		int androidKeyCode = androidEvent.getKeyCode();
		if (!map.containsKey(androidKeyCode)) {
			Log.d(LOGTAG, "Key code not found " + androidKeyCode);
			return null;
		}
		
		KeyEvent event = new KeyEvent();
		event.keyCode = map.get(androidKeyCode);
		event.down = androidEvent.getAction() == android.view.KeyEvent.ACTION_DOWN;
		event.timestamp = System.currentTimeMillis();
		if ((androidEvent.getModifiers() & android.view.KeyEvent.META_SHIFT_ON) != 0) {
			event.modifiers = KeyEvent.KEY_MOD_SHIFT;
		}
		return event;
	}
	
	private static void buildMap(int srcStart, int srcEnd, int baseKeyCode) {
		for(int keyCode=srcStart; keyCode<=srcEnd; keyCode++) {
			map.put(keyCode, keyCode - srcStart + baseKeyCode);
		}
	}
	

}
