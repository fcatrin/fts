package fts.utils.dialogs;

import java.util.List;

import fts.core.Callback;
import fts.vfile.VirtualFile;

public class DialogUtils {

	public static DialogFactory factory;
	
	private DialogUtils() {}
	
	public static void message(DialogContext context, String text) {
		confirm(context, text, "OK", null, null);
	}

	public static void message(DialogContext context, String text, DialogCallback callback) {
		confirm(context, text, "OK", null, callback);
	}

	public static void message(DialogContext context, String text, String optYes) {
		confirm(context, text, optYes, null, null);
	}

	public static void message(DialogContext context, String text, String optYes, DialogCallback callback) {
		confirm(context, text, optYes, null, callback);
	}

	public static void confirm(DialogContext context, String text, String optYes, String optNo, DialogCallback callback) {
		factory.confirm(context, text, optYes, optNo, callback);
	}
	
	public static void select(DialogContext context, List<ListOption> options, String title, DialogListCallback callback) {
		factory.select(context, options, title, callback);
	}

	public static void browse(final DialogContext context, final VirtualFile sysRoot,
							  final FileChooserConfig config,
							  final Callback<VirtualFile> onSelectedFileCallback) {
		factory.browse(context, sysRoot, config, onSelectedFileCallback);
	}
	
	public static void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
		factory.setOnItemSelectionChangedListener(listener);
	}
	
	public static boolean hasVisiblePanel(DialogContext context) {
		return factory.hasVisiblePanel(context);
	}
	
	public static boolean dispatchCancelKey(DialogContext context) {
		return factory.dispatchCancelKey(context);
	}

	public static boolean cancelDialog(DialogContext context) {
		return factory.cancelDialog(context);
	}

}
