package fts.utils.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fts.core.Log;
import fts.core.NativeWindow;
import fts.widgets.ListWidget;

public class FileListWidget extends ListWidget<File> {
	private final static String LOGTAG = FileListWidget.class.getSimpleName();
	
	File folder;

	public FileListWidget(NativeWindow window) {
		super(window);
	}
	
	public void browse(File folder) {
		Log.d(LOGTAG, "Browse " + folder.getAbsolutePath());
		this.folder = folder;
		
		File files[] = folder.listFiles();
		if (files == null) {
			setAdapter(null);
			return;
		}
		
		List<File> fileItems = Arrays.asList(files);
		
		setAdapter(new FileListAdapter(getWindow(), fileItems));
	}

}
