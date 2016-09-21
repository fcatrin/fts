package fts.core;

public class Log {
	public static boolean TRACE = false;
	
	public static void d(String tag, String msg) {
		if (TRACE) System.out.println("[" + tag + "] " + msg);
	}
}
