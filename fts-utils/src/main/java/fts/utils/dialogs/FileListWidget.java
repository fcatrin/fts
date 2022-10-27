package fts.utils.dialogs;

import java.io.File;

import fts.core.NativeWindow;
import fts.vfile.VirtualFile;
import fts.widgets.ListWidget;

public class FileListWidget extends ListWidget<VirtualFile> {
	private final static String LOGTAG = FileListWidget.class.getSimpleName();
	
	public FileListWidget(NativeWindow window) {
		super(window);
	}

}
