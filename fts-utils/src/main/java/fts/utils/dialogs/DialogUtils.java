package fts.utils.dialogs;

import java.util.List;

import fts.core.Callback;
import fts.core.ListOption;
import fts.ui.NativeWindow;
import fts.ui.Widget;
import fts.ui.events.KeyEvent;
import fts.ui.events.OnItemSelectionChangedListener;
import fts.utils.dialogs.FileListPanel.FileChooserConfig;
import fts.vfile.VirtualFile;

public class DialogUtils {

	public static DialogFactory factory;
	
	private DialogUtils() {}
	
	public static void message(NativeWindow window, String text) {
		confirm(window, text, "OK", null, null);
	}

	public static void message(NativeWindow window, String text, DialogCallback callback) {
		confirm(window, text, "OK", null, callback);
	}

	public static void message(NativeWindow window, String text, String optYes) {
		confirm(window, text, optYes, null, null);
	}

	public static void message(NativeWindow window, String text, String optYes, DialogCallback callback) {
		confirm(window, text, optYes, null, callback);
	}

	public static void confirm(NativeWindow window, String text, String optYes, String optNo, DialogCallback callback) {
		factory.confirm(window, text, optYes, optNo, callback);
	}
	
	public static void select(NativeWindow window, List<ListOption> options, String title, DialogListCallback callback) {
		factory.select(window, options, title, callback);
	}

	public static void custom(NativeWindow window, Widget widget, String optYes, String optNo, DialogCallback callback) {
		factory.custom(window, widget, optYes, optNo, callback);
	}
	public static void browse(final NativeWindow window, final VirtualFile sysRoot,
			final FileChooserConfig config,
			final Callback<VirtualFile> onSelectedFileCallback) {
		factory.browse(window, sysRoot, config, onSelectedFileCallback);
	}
	
	public static void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
		factory.setOnItemSelectionChangedListener(listener);
	}
	
	public static boolean hasVisiblePanel(NativeWindow window) {
		return factory.hasVisiblePanel(window);
	}
	
	public static boolean onKeyDown(NativeWindow window, KeyEvent event) {
		return factory.onKeyDown(window, event);
	}
	public static boolean onKeyUp(NativeWindow window, KeyEvent event) {
		return factory.onKeyUp(window, event);
	}
	
	public static boolean dispatchCancelKey(NativeWindow window) {
		return factory.dispatchCancelKey(window);
	}

	public static boolean cancelDialog(NativeWindow window) {
		return factory.cancelDialog(window);
	}

}
