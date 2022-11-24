package fts.utils.dialogs;

import java.io.IOException;
import java.util.List;

import fts.core.Callback;
import fts.core.ListOption;
import fts.core.Utils;
import fts.ui.Container;
import fts.ui.NativeWindow;
import fts.ui.SimpleBackgroundTask;
import fts.ui.Widget;
import fts.ui.Widget.Visibility;
import fts.ui.events.KeyEvent;
import fts.ui.events.OnClickListener;
import fts.ui.events.OnItemSelectedListener;
import fts.ui.events.OnItemSelectionChangedListener;
import fts.ui.widgets.ButtonWidget;
import fts.ui.widgets.ListWidget;
import fts.ui.widgets.TextWidget;
import fts.utils.dialogs.FileListPanel.FileChooserConfig;
import fts.vfile.VirtualFile;

public class SimpleDialogs implements DialogFactory {

	private Widget visiblePanel;
	private ListOptionWidget listWidget;
	private SimpleDialogCallback dismissCallback;
	
	public SimpleDialogs() {}
	
	public void select(final NativeWindow window, List<ListOption> options, String title, final DialogListCallback callback) {
		dismissCallback = callback;
		
		Widget panel = window.findWidget("modalListPanel");

		listWidget = (ListOptionWidget)panel.findWidget("list");
		listWidget.setAdapter(new DialogListAdapter(window, options));
		
		listWidget.setOnItemSelectedListener(new OnItemSelectedListener<ListOption>() {
			@Override
			public void onItemSelected(ListWidget<ListOption> widget, ListOption item, int index) {
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

	
	public void confirm(final NativeWindow window, String text, String optYes, String optNo, final DialogCallback callback) {
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

	public void custom(final NativeWindow window, Widget widget, String optYes, String optNo, final DialogCallback callback) {
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

	public void browse(final NativeWindow window, final VirtualFile sysRoot,
			final FileChooserConfig config,
			final Callback<VirtualFile> onSelectedFileCallback) {
		
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
	
	public boolean hasVisiblePanel(NativeWindow window) {
		return visiblePanel!=null;
	}
	
	private boolean dismissVisiblePanel(NativeWindow window) {
		boolean closed = closeVisiblePanel(window);
		if (dismissCallback != null && closed) {
			dismissCallback.onDismiss();
		}
		return closed;
	}
	
	private boolean closeVisiblePanel(NativeWindow window) {
		if (!hasVisiblePanel(window)) return false;
		
		visiblePanel.setVisibility(Visibility.Gone);
		visiblePanel = null;
		return true;
	}

	@Override
	public boolean cancelDialog(NativeWindow window) {
		return dismissVisiblePanel(window);
	}
	
	public boolean onKeyDown(NativeWindow window, KeyEvent event) {
		return false;
	}
	
	public boolean onKeyUp(NativeWindow window, KeyEvent event) {
		if (event.keyCode == KeyEvent.KEY_ESC) {
			return dispatchCancelKey(window);
		}
		return false;
	}
	
	public boolean dispatchCancelKey(NativeWindow window) {
		if (hasVisiblePanel(window)) {
			dismissVisiblePanel(window);
			return true;
		}
		return false;
	}
}
