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

public interface DialogFactory {
	public void select(NativeWindow window, List<ListOption> options, String title, DialogListCallback callback);

	public void confirm(NativeWindow window, String text, String optYes, String optNo, DialogCallback callback);
	public void custom(NativeWindow window, Widget widget, String optYes, String optNo, DialogCallback callback);
	public void browse(final NativeWindow window, final VirtualFile sysRoot,
			final FileChooserConfig config,
			final Callback<VirtualFile> onSelectedFileCallback);
	
	public void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener);
	
	public boolean hasVisiblePanel(NativeWindow window);
	
	public boolean onKeyDown(NativeWindow window,KeyEvent event);
	public boolean onKeyUp(NativeWindow window,KeyEvent event);
	
	public boolean dispatchCancelKey(NativeWindow window);
	public boolean cancelDialog(NativeWindow window);
}
