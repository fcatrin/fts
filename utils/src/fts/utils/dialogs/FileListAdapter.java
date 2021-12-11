package fts.utils.dialogs;

import java.io.File;
import java.util.List;

import fts.core.Application;
import fts.core.ListAdapter;
import fts.core.NativeWindow;
import fts.core.Utils;
import fts.core.Widget;
import fts.core.Widget.State;
import fts.core.Widget.Visibility;
import fts.events.OnStateListener;
import fts.graphics.ColorListSelector;
import fts.widgets.TextWidget;

public class FileListAdapter extends ListAdapter<File> {

	private NativeWindow window;

	public FileListAdapter(NativeWindow window, List<File> files) {
		setItems(files);
		this.window = window;
	}

	@Override
	public Widget getWidget(Widget widget, int index, Widget list) {
		if (widget == null) {
			widget = Application.inflate(window, "modal_files_item");
		}
		
		File item = getItem(index);
		
		final TextWidget txtText = (TextWidget)widget.findWidget("text");
		txtText.setText(item.getName());
		
		final TextWidget txtValue = (TextWidget)widget.findWidget("value");
		if (item.isDirectory()) {
			txtValue.setText("[DIR]");
			txtValue.setVisibility(Visibility.Visible);
		} else {
			txtValue.setText(Utils.size2human(item.length()));
			txtValue.setVisibility(Visibility.Gone);
		}
		
		widget.setOnStateListener(new OnStateListener() {
			
			@Override
			public void onStateChanged(Widget widget, State state, boolean value) {
				txtText.setState(state, value);
				txtValue.setState(state, value);
			}
		});
		
		return widget;
	}

}
