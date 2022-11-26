package fts.ui.dialogs;

import java.io.IOException;
import java.util.List;

import fts.core.Callback;
import fts.core.SimpleBackgroundTask;
import fts.core.Utils;
import fts.ui.Container;
import fts.ui.Widget;
import fts.ui.Widget.Visibility;
import fts.ui.Window;
import fts.ui.events.KeyEvent;
import fts.ui.events.OnClickListener;
import fts.ui.widgets.ButtonWidget;
import fts.ui.widgets.TextWidget;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogContext;
import fts.utils.dialogs.DialogFactory;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.FileChooserConfig;
import fts.utils.dialogs.ListOption;
import fts.utils.dialogs.OnItemSelectedListener;
import fts.utils.dialogs.OnItemSelectionChangedListener;
import fts.utils.dialogs.SimpleDialogCallback;
import fts.vfile.VirtualFile;

public class SimpleDialogs implements DialogFactory {

	private Widget visiblePanel;
	private ListOptionWidget listWidget;
	private SimpleDialogCallback dismissCallback;
	
	public SimpleDialogs() {}
	
	public void select(DialogContext context, List<ListOption> options, String title, final DialogListCallback callback) {
		dismissCallback = callback;

		final Window window = (Window)context;
		Widget panel = window.findWidget("modalListPanel");

		listWidget = (ListOptionWidget)panel.findWidget("list");
		listWidget.setAdapter(new DialogListAdapter(window, options));
		
		listWidget.setOnItemSelectedListener(new OnItemSelectedListener<ListOption>() {
			@Override
			public void onItemSelected(ListOption item, int index) {
				closeVisiblePanel(window);
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

	
	public void confirm(DialogContext context, String text, String optYes, String optNo, final DialogCallback callback) {
		dismissCallback = callback;

		final Window window = (Window)context;
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
				closeVisiblePanel(window);
				if (callback!=null) callback.onYes();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel(window);
				if (callback!=null) callback.onNo();
			}
		});

		buttonToFocus.requestFocus();
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
	}

	public void custom(final DialogContext context, Widget widget, String optYes, String optNo, final DialogCallback callback) {
		dismissCallback = callback;
		final Window window = (Window)context;
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
				closeVisiblePanel(window);
				if (callback!=null) callback.onYes();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Widget w) {
				closeVisiblePanel(window);
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

	public void browse(DialogContext context, final VirtualFile sysRoot,
					   final FileChooserConfig config,
					   final Callback<VirtualFile> onSelectedFileCallback) {

		final Window window = (Window)context;
		dismissCallback = new SimpleDialogCallback() {

			@Override
			public void onDismiss() {
				onSelectedFileCallback.onError(null);
			}
			
		};
		
		final Callback<VirtualFile> onDialogSelectedFileCallback = new Callback<VirtualFile>() {
			
			@Override
			public void onResult(VirtualFile file) {
				closeVisiblePanel(window);
				onSelectedFileCallback.onResult(file);
			}

			@Override
			public void onError(Exception e) {
				closeVisiblePanel(window);
				onSelectedFileCallback.onError(e);
			}
			
		};
		
		Widget panel = window.findWidget("modalFilesPanel");
		final FileListWidget fileList = (FileListWidget)panel.findWidget("filesList");
		final TextWidget fileListTitle = (TextWidget)panel.findWidget("filesTitle");
		
		SimpleBackgroundTask task = new SimpleBackgroundTask() {
			
			@Override
			public void onBackgroundTask() throws Exception {
				if (config.initialDir == null) return;
				try {
					if (config.initialDir.exists()) return;
				} catch (IOException e) {
					e.printStackTrace();
				}
				config.initialDir = null;
			}

			@Override
			public void onSuccess() {
				FileListPanel fileListPanel = new FileListPanel(window, fileListTitle, fileList, sysRoot, 
						config, onDialogSelectedFileCallback);
				
				fileListPanel.refresh();
			}
			
		};
		
		task.execute();
		
		panel.setVisibility(Visibility.Visible);
		visiblePanel = panel;
	}
	
	public void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
		listWidget.setOnItemSelectionChangedListener(listener);
	}

	@Override
	public boolean hasVisiblePanel(DialogContext context) {
		return visiblePanel!=null;
	}
	
	private boolean dismissVisiblePanel(Window window) {
		boolean closed = closeVisiblePanel(window);
		if (dismissCallback != null && closed) {
			dismissCallback.onDismiss();
		}
		return closed;
	}
	
	private boolean closeVisiblePanel(Window window) {
		if (!hasVisiblePanel(window)) return false;
		
		visiblePanel.setVisibility(Visibility.Gone);
		visiblePanel = null;
		return true;
	}

	@Override
	public boolean cancelDialog(DialogContext context) {
		return dismissVisiblePanel((Window)context);
	}
	
	public boolean onKeyDown(DialogContext context, KeyEvent event) {
		return false;
	}
	
	public boolean onKeyUp(DialogContext context, KeyEvent event) {
		final Window window = (Window)context;
		if (event.keyCode == KeyEvent.KEY_ESC) {
			return dispatchCancelKey(window);
		}
		return false;
	}
	
	public boolean dispatchCancelKey(DialogContext context) {
		final Window window = (Window)context;
		if (hasVisiblePanel(window)) {
			dismissVisiblePanel(window);
			return true;
		}
		return false;
	}
}
