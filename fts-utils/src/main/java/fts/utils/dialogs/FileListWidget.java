package fts.utils.dialogs;

import fts.ui.NativeWindow;
import fts.ui.widgets.ListWidget;
import fts.vfile.VirtualFile;

public class FileListWidget extends ListWidget<VirtualFile> {
	private final static String LOGTAG = FileListWidget.class.getSimpleName();
	
	public FileListWidget(NativeWindow window) {
		super(window);
	}

}
