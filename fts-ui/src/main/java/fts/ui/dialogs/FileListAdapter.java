package fts.ui.dialogs;

import java.util.List;

import fts.core.Utils;
import fts.ui.ListAdapter;
import fts.ui.Resources;
import fts.ui.Widget;
import fts.ui.Widget.State;
import fts.ui.Widget.Visibility;
import fts.ui.Window;
import fts.ui.events.OnStateListener;
import fts.ui.widgets.TextWidget;
import fts.vfile.VirtualFile;

public class FileListAdapter extends ListAdapter<VirtualFile> {

	private Window window;

	public FileListAdapter(Window window, List<VirtualFile> files) {
		setItems(files);
		this.window = window;
	}

	@Override
	public Widget getWidget(Widget widget, int index, Widget list) {
		if (widget == null) {
			widget = Resources.inflate(window, "modal_files_item");
		}
		
		VirtualFile item = getItem(index);
		
		boolean isParent = item.isParent();
		
		String name = item.getFriendlyName();
		if (isParent) {
			if (name == null || name.isEmpty())	name = "..";
		} else if (name == null) {
			name = item.getName();
		}
		
		final TextWidget txtText = (TextWidget)widget.findWidget("text");
		txtText.setText(name);
		
		final TextWidget txtValue = (TextWidget)widget.findWidget("value");
		if (item.isDirectory()) {
			txtValue.setVisibility(Visibility.Gone);
		} else {
			txtValue.setText(Utils.size2human(item.getSize()));
			txtValue.setVisibility(Visibility.Visible);
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
