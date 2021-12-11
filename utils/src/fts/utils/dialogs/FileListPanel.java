package fts.utils.dialogs;

import java.io.File;

import fts.widgets.TextWidget;

public class FileListPanel {

	private FileListWidget list;
	private TextWidget title;

	public FileListPanel(TextWidget title, FileListWidget list) {
		this.title = title;
		this.list = list;
	}
	
	public void browse(File folder) {
		title.setText(folder.getAbsolutePath());
		list.browse(folder);
	}

}
