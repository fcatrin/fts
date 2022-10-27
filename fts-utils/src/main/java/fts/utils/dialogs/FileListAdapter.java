package fts.utils.dialogs;

import java.util.List;

import fts.core.Application;
import fts.core.ListAdapter;
import fts.core.NativeWindow;
import fts.core.Utils;
import fts.core.Widget;
import fts.core.Widget.State;
import fts.core.Widget.Visibility;
import fts.events.OnStateListener;
import fts.vfile.VirtualFile;
import fts.widgets.TextWidget;

public class FileListAdapter extends ListAdapter<VirtualFile> {

	private NativeWindow window;

	public FileListAdapter(NativeWindow window, List<VirtualFile> files) {
		setItems(files);
		this.window = window;
	}

	@Override
	public Widget getWidget(Widget widget, int index, Widget list) {
		if (widget == null) {
			widget = Application.inflate(window, "modal_files_item");
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
