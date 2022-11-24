package fts.utils.dialogs;

import java.util.List;

import fts.core.ListOption;
import fts.ui.Application;
import fts.ui.NativeWindow;
import fts.ui.Widget;
import fts.ui.Widget.State;
import fts.ui.Widget.Visibility;
import fts.ui.adapters.ListOptionAdapter;
import fts.ui.events.OnStateListener;
import fts.ui.graphics.ColorListSelector;
import fts.ui.widgets.TextWidget;

public class DialogListAdapter extends ListOptionAdapter {

	private NativeWindow window;

	public DialogListAdapter(NativeWindow window, List<ListOption> items) {
		setItems(items);
		this.window = window;
	}

	@Override
	public Widget getWidget(Widget widget, int index, Widget list) {
		if (widget == null) {
			widget = Application.inflate(window, "modal_list_item");
		}
		
		ListOption item = getItem(index);
		
		final TextWidget txtText = (TextWidget)widget.findWidget("text");
		txtText.setText(item.getText());
		
		final TextWidget txtValue = (TextWidget)widget.findWidget("value");
		if (item.getValue()!=null) {
			txtValue.setText(item.getValue());
			txtValue.setVisibility(Visibility.Visible);
		} else {
			txtValue.setVisibility(Visibility.Gone);
		}
		
		widget.setOnStateListener(new OnStateListener() {
			
			@Override
			public void onStateChanged(Widget widget, State state, boolean value) {
				if (state != State.Selected) return;
				ColorListSelector colorBg = widget.resolveColor("@color/modal_background");
				ColorListSelector colorFg = widget.resolveColor("@color/modal_foreground");
				txtText.setColor(value ? colorBg : colorFg);
				txtValue.setColor(value ? colorBg : colorFg);
			}
		});
		
		return widget;
	}

}
