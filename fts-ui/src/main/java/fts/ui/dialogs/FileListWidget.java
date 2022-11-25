package fts.ui.dialogs;

import fts.ui.Window;
import fts.ui.widgets.ListWidget;
import fts.vfile.VirtualFile;

public class FileListWidget extends ListWidget<VirtualFile> {
	private final static String LOGTAG = FileListWidget.class.getSimpleName();
	
	public FileListWidget(Window window) {
		super(window);
	}

}
