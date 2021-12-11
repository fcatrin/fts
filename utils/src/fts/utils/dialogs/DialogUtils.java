package fts.utils.dialogs;

import java.io.File;
import java.util.List;

import fts.core.Callback;
import fts.core.Container;
import fts.core.ListOption;
import fts.core.NativeWindow;
import fts.core.SimpleCallback;
import fts.core.Utils;
import fts.core.Widget;
import fts.core.Widget.Visibility;
import fts.events.KeyEvent;
import fts.events.OnClickListener;
import fts.events.OnItemSelectedListener;
import fts.events.OnItemSelectionChangedListener;
import fts.widgets.ButtonWidget;
import fts.widgets.ListWidget;
import fts.widgets.TextWidget;

public class DialogUtils {

	private static Widget visiblePanel;
	private static ListOptionWidget listWidget;
	private static SimpleDialogCallback dismissCallback;
	
	private DialogUtils() {}
	
	public static void openListSelection(NativeWindow window, List<ListOption> options, String title, final DialogListCallback callback) {
		dismissCallback = callback;
		
		Widget panel = window.findWidget("modalListPanel");

		listWidget = (ListOptionWidget)panel.findWidget("list");
		listWidget.setAdapter(new DialogListAdapter(window, options));
		
		listWidget.setOnItemSelectedListener(new OnItemSelectedListener<ListOption>() {
			@Override
			public void onItemSelected(ListWidget<ListOption> widget, ListOption item, int index) {
				closeVisiblePanel();
				callback.onItemSelected(item.getCode());
			}
		});
		
		listWidget.setOnItemSelectionChangedListener(null);
		
		TextWidget titleWidget = (TextWidget)panel.findWidget("title");
		titleWidget.setText(title);
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
		
		listWidget.requestFocus();
	}

	public static void openDialog(NativeWindow window, String text) {
		openDialog(window, text, "OK", null, null);
	}

	public static void openDialog(NativeWindow window, String text, String optYes) {
		openDialog(window, text, optYes, null, null);
	}

	public static void openDialog(NativeWindow window, String text, String optYes, DialogCallback callback) {
		openDialog(window, text, optYes, null, callback);
	}
	
	public static void openDialog(NativeWindow window, String text, String optYes, String optNo, final DialogCallback callback) {
		dismissCallback = callback;
		
		Widget panel = window.findWidget("modalDialogPanel");
		TextWidget textWidget = (TextWidget)panel.findWidget("text");
		ButtonWidget btnYes = (ButtonWidget)panel.findWidget("btnYes");
		ButtonWidget btnNo  = (ButtonWidget)panel.findWidget("btnNo");

		Widget buttonToFocus = btnYes;
		
		textWidget.setText(text);
		btnYes.setText(optYes);
		
		
		if (Utils.isEmptyString(optNo)) {
			btnNo.setVisibility(Visibility.Gone);
		} else {
			btnNo.setText(optNo);
			btnNo.setVisibility(Visibility.Visible);
			buttonToFocus = btnNo;
		}
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel();
				if (callback!=null) callback.onYes();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel();
				if (callback!=null) callback.onNo();
			}
		});

		buttonToFocus.requestFocus();
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
	}

	public static void openDialogCustom(NativeWindow window, Widget widget, String optYes, String optNo, final DialogCallback callback) {
		dismissCallback = callback;
		
		Widget panel = window.findWidget("modalCustomDialogPanel");
		ButtonWidget btnYes = (ButtonWidget)panel.findWidget("btnYes");
		ButtonWidget btnNo  = (ButtonWidget)panel.findWidget("btnNo");

		Widget buttonToFocus = btnYes;
		
		btnYes.setText(optYes);
		
		if (Utils.isEmptyString(optNo)) {
			btnNo.setVisibility(Visibility.Gone);
		} else {
			btnNo.setText(optNo);
			btnNo.setVisibility(Visibility.Visible);
			buttonToFocus = btnNo;
		}
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel();
				if (callback!=null) callback.onYes();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel();
				if (callback!=null) callback.onNo();
			}
		});

		
		Container container = (Container)panel.findWidget("dialogCustomArea");
		container.removeAllChildren();
		container.add(widget);
		widget.requestLayout();
		
		buttonToFocus.requestFocus();
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
	}

	public static void openFileBrowser(NativeWindow window, File folder, Callback<File> onSelectedFileCallback, DialogCallback callback) {
		dismissCallback = callback;
		
		Widget panel = window.findWidget("modalFilesPanel");
		FileListWidget fileList = (FileListWidget)panel.findWidget("filesList");
		TextWidget fileListTitle = (TextWidget)panel.findWidget("filesTitle");
		FileListPanel fileListPanel = new FileListPanel(fileListTitle, fileList);
		
		fileListPanel.browse(folder);
		fileList.requestFocus();
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
	}
	
	public static void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
		listWidget.setOnItemSelectionChangedListener(listener);
	}
	
	public static boolean hasVisiblePanel() {
		return visiblePanel!=null;
	}
	
	private static void dismissVisiblePanel() {
		closeVisiblePanel();
		if (dismissCallback != null) {
			dismissCallback.onDismiss();
		}
	}
	
	private static void closeVisiblePanel() {
		if (!hasVisiblePanel()) return;
		
		visiblePanel.setVisibility(Visibility.Gone);
		visiblePanel = null;
	}
	
	public static boolean onKeyDown(KeyEvent event) {
		return false;
	}
	
	public static boolean onKeyUp(KeyEvent event) {
		if (event.keyCode == KeyEvent.KEY_ESC) {
			return dispatchCancelKey();
		}
		return false;
	}
	
	public static boolean dispatchCancelKey() {
		if (hasVisiblePanel()) {
			dismissVisiblePanel();
			return true;
		}
		return false;
	}
}
