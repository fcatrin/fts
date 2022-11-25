package fts.utils.dialogs;

import java.util.List;

import fts.core.Callback;
import fts.vfile.VirtualFile;

public interface DialogFactory {
	public void select(DialogContext context, List<ListOption> options, String title, DialogListCallback callback);

	public void confirm(DialogContext context, String text, String optYes, String optNo, DialogCallback callback);
	public void browse(final DialogContext context, final VirtualFile sysRoot,
					   final FileChooserConfig config,
					   final Callback<VirtualFile> onSelectedFileCallback);
	
	public void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener);
	
	public boolean hasVisiblePanel(DialogContext context);
	
	public boolean dispatchCancelKey(DialogContext context);
	public boolean cancelDialog(DialogContext context);
}
