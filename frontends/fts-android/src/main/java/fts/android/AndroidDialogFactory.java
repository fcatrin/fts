package fts.android;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import fts.android.fileselector.FilesPanel;
import fts.core.Callback;
import fts.core.CoreUtils;
import fts.core.SimpleBackgroundTask;
import fts.core.SimpleCallback;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogContext;
import fts.utils.dialogs.DialogFactory;
import fts.utils.dialogs.DialogInputCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileChooserConfig;
import fts.utils.dialogs.ListOption;
import fts.utils.dialogs.OnItemSelectionChangedListener;
import fts.utils.dialogs.SimpleDialogCallback;
import fts.vfile.VirtualFile;

public class AndroidDialogFactory implements DialogFactory {
	private static final int DIALOG_OPENING_THRESHOLD = 800;

	private long openTimeStart = 0;

	public AndroidDialogFactory() {}

	@Override
	public void confirm(DialogContext context, String text, String optYes, String optNo, final DialogCallback callback) {
		final Activity activity = (AndroidWindow)context;
		setDismissCallback(activity, R.id.modal_dialog_actions, callback);

		TextView txtMessage = activity.findViewById(R.id.txtDialogAction);
		txtMessage.setText(text);
		
		final Button btnYes = activity.findViewById(R.id.btnDialogActionPositive);
		final Button btnNo = activity.findViewById(R.id.btnDialogActionNegative);
		
		if (CoreUtils.isEmptyString(optYes)) optYes = "OK";
		btnYes.setText(optYes);
		btnYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeDialog(activity, R.id.modal_dialog_actions, new SimpleCallback() {
					
					@Override
					public void onResult() {
						if (callback!=null) {
							callback.onYes();
							callback.onFinally();
						}
					}
				});
			}
		});
		
		final boolean hasNoButton = !CoreUtils.isEmptyString(optNo);
		
		if (hasNoButton) {
			btnNo.setVisibility(View.VISIBLE);
			btnNo.setText(optNo);
			btnNo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					closeDialog(activity, R.id.modal_dialog_actions, new SimpleCallback() {

						@Override
						public void onResult() {
							if (callback != null) {
								callback.onNo();
								callback.onFinally();
							}
						}
					});
				}
			});
		} else {
			btnNo.setVisibility(View.GONE);
		}
		
		openDialog(activity, R.id.modal_dialog_actions, new SimpleCallback(){
			@Override
			public void onResult() {
				Button activeButton = hasNoButton?btnNo:btnYes; 
				activeButton.setFocusable(true);
				activeButton.setFocusableInTouchMode(true);
				activeButton.requestFocus();
			}
		});
	}

	@Override
	public void select(DialogContext context, List<ListOption> options, String title, final DialogListCallback callback) {
		final Activity activity = (AndroidWindow)context;
		setDismissCallback(activity, R.id.modal_dialog_list, callback);
		
		TextView txtTitle = activity.findViewById(R.id.txtDialogListTitle);
		if (CoreUtils.isEmptyString(title)) {
			txtTitle.setVisibility(View.GONE);
		} else {
			txtTitle.setText(title);
			txtTitle.setVisibility(View.VISIBLE);
		}
		
		TextView txtInfo = activity.findViewById(R.id.txtDialogListInfo);
		txtInfo.setVisibility(View.GONE);
		
		final ListView lv = activity.findViewById(R.id.lstDialogList);
		lv.setAdapter(new ListOptionAdapter(options));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ListOption result = (ListOption)parent.getAdapter().getItem(position);
				closeDialog(activity, R.id.modal_dialog_list, new SimpleCallback() {
					@Override
					public void onResult() {
						if (callback!=null) {
							callback.onItemSelected(result.getCode());
							callback.onFinally();
						}
					}
				});
			}
		});
		
		openDialog(activity, R.id.modal_dialog_list, new SimpleCallback() {
			@Override
			public void onResult() {
		        // lv.setSelection(preSelectedItem);
		        
				lv.setFocusable(true);
				lv.setFocusableInTouchMode(true);
				lv.requestFocus();
			}
		});
	}

	@Override
	public void browse(final DialogContext context, final VirtualFile sysRoot, final FileChooserConfig config, Callback<VirtualFile> onSelectedFileCallback) {
		final Activity activity = (AndroidWindow)context;
		final Callback<VirtualFile> listCallback = new Callback<VirtualFile>() {
			@Override
			public void onResult(final VirtualFile result) {
				closeDialog(activity, R.id.modal_dialog_chooser, new SimpleCallback() {
					@Override
					public void onResult() {
						config.callback.onResult(result);
						config.callback.onFinally();
					}
				});
			}
			
			@Override
			public void onFailure(final Exception e) {
				closeDialog(activity, R.id.modal_dialog_chooser, new SimpleCallback(){
					@Override
					public void onResult() {
						config.callback.onFailure(e);
						config.callback.onFinally();
					}
				});
			}
		};
		
		activity.findViewById(R.id.modal_dialog_chooser).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listCallback.onCancel();
			}
		});

		TextView txtTitle = activity.findViewById(R.id.txtDialogChooserTitle);
		if (CoreUtils.isEmptyString(config.title)) {
			txtTitle.setVisibility(View.GONE);
		} else {
			txtTitle.setText(config.title);
			txtTitle.setVisibility(View.VISIBLE);
		}

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
				final ListView lv = activity.findViewById(R.id.lstDialogChooser);

				TextView txtStatus1 = activity.findViewById(R.id.txtPanelStatus1);
				TextView txtStatus2 = activity.findViewById(R.id.txtPanelStatus2);

				TextView  txtStorage = activity.findViewById(R.id.txtStorage);
				ImageView imgStorage = activity.findViewById(R.id.imgStorage);

				FilesPanel filesPanel = new FilesPanel(context, sysRoot, lv, txtStorage, imgStorage,
						txtStatus1, txtStatus2, listCallback, config);
				filesPanel.refresh();

				openDialog(activity, R.id.modal_dialog_chooser, new SimpleCallback() {
					@Override
					public void onResult() {
						if (lv.getChildCount()>0) {
							lv.setSelection(0);
						}

						lv.setFocusable(true);
						lv.setFocusableInTouchMode(true);
						lv.requestFocus();
					}
				});
			}
		};
		task.execute();
	}

	@Override
	public void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
	}

	@Override
	public boolean hasVisiblePanel(DialogContext context) {
		final Activity activity = (AndroidWindow)context;
		return getVisibleDialog(activity) != null;
	}

	public boolean onKeyDown(DialogContext context, KeyEvent event) {
		return false;
	}

	public boolean onKeyUp(DialogContext context, KeyEvent event) {
		int keyCode = event.getKeyCode();

		if (keyCode == KeyEvent.KEYCODE_BACK && hasVisiblePanel(context)) {
			if ((System.currentTimeMillis() - openTimeStart) < DIALOG_OPENING_THRESHOLD) {
				// ignore if this is a key up from a tap on the BACK/SELECT key
				return true;
			}
			return cancelDialog(context);
		}
		return false;
	}

	@Override
	public boolean dispatchCancelKey(DialogContext context) {
		return cancelDialog(context);
	}
	
	private void openDialog(Activity activity, int dialogResourceId, final SimpleCallback callback) {
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setDuration(400);
		
		final View view = activity.findViewById(dialogResourceId);
		fadeIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				view.clearAnimation();
				if (callback!=null) callback.onResult();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
		});
		
		openTimeStart = System.currentTimeMillis();
		view.setVisibility(View.VISIBLE);
		view.clearAnimation();
		view.startAnimation(fadeIn);
	}

	
	private void closeDialog(Activity activity, int dialogResourceId, final SimpleCallback callback) {
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new DecelerateInterpolator());
		fadeOut.setDuration(300);
		
		final View view = activity.findViewById(dialogResourceId);
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
				if (callback!=null) {
					callback.onResult();
					callback.onFinally();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
		});
		
		view.clearAnimation();
		view.startAnimation(fadeOut);
	}
	
	private View getVisibleDialog(Activity activity) {
		View dialogActions = activity.findViewById(R.id.modal_dialog_actions);
		View dialogList = activity.findViewById(R.id.modal_dialog_list);
		View dialogChooser = activity.findViewById(R.id.modal_dialog_chooser);
		View customDialog = activity.findViewById(R.id.modal_dialog_custom);
		View inputDialog = activity.findViewById(R.id.modal_dialog_input);
		
		return
			isVisible(dialogActions) ? dialogActions :
			isVisible(dialogList)    ? dialogList :
			isVisible(dialogChooser) ? dialogChooser :
			isVisible(inputDialog)   ? inputDialog :
			isVisible(customDialog)  ? customDialog : null;
	}
	
	private boolean isVisible(View v) {
		return v!=null && v.getVisibility() == View.VISIBLE;
	}


	public boolean cancelDialog(DialogContext context) {
		final Activity activity = (AndroidWindow)context;
		View dialog = getVisibleDialog(activity);
		
		if (dialog == null) return false;
		
		dialog.performClick();
		return true;
	}
	
	private void setDismissCallback(final Activity activity, final int resourceId, final SimpleDialogCallback callback) {
		View view = activity.findViewById(resourceId);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeDialog(activity, resourceId, new SimpleCallback() {
					
					@Override
					public void onResult() {
						if (callback!=null) {
							callback.onDismiss();
							callback.onFinally();
						}
					}
				});
			}
		});
	}

	public void input(DialogContext context, String text, String lastInput, DialogInputCallback callback) {
		final Activity activity = (AndroidWindow)context;
		setDismissCallback(activity, R.id.modal_dialog_input, callback);

		final Button btnYes = activity.findViewById(R.id.btnDialogInputPositive);
		final Button btnNo = activity.findViewById(R.id.btnDialogInputNegative);

		TextView txtMessage = activity.findViewById(R.id.txtDialogInput);
		txtMessage.setText(text);

		EditText editor = activity.findViewById(R.id.editorDialogInput);
		editor.setText(lastInput);
		editor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, android.view.KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					AndroidUtils.hideSoftKeyboard(activity, editor);
					btnYes.performClick();
					return true;
				}
				return false;
			}
		});

		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AndroidUtils.hideSoftKeyboard(activity, editor);
				closeDialog(activity, R.id.modal_dialog_input, new SimpleCallback() {

					@Override
					public void onResult() {
						if (callback!=null) {
							callback.onInput(editor.getText().toString());
							callback.onFinally();
						}
					}
				});
			}
		});

		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AndroidUtils.hideSoftKeyboard(activity, editor);
				closeDialog(activity, R.id.modal_dialog_input, new SimpleCallback() {

					@Override
					public void onResult() {
						if (callback != null) {
							callback.onNo();
							callback.onFinally();
						}
					}
				});
			}
		});

		openDialog(activity, R.id.modal_dialog_input, new SimpleCallback(){
			@Override
			public void onResult() {
				editor.requestFocus();
			}
		});

	}

	public static void custom(Activity activity, View view,
						String optYes, String optNo,
						DialogCallback callback) {
		((AndroidDialogFactory) DialogUtils.factory).dialogCustom(activity, view,
				optYes, optNo, callback);
	}

	private void dialogCustom(final Activity activity, View view,
							  String optYes, String optNo,
							  final DialogCallback callback) {

		setDismissCallback(activity, R.id.modal_dialog_custom, callback);

		ViewGroup container = activity.findViewById(R.id.modal_dialog_custom_container);
		container.removeAllViews();
		container.addView(view);

		final Button btnYes = activity.findViewById(R.id.btnDialogCustomPositive);
		final Button btnNo = activity.findViewById(R.id.btnDialogCustomNegative);

		final boolean hasNoButton = !CoreUtils.isEmptyString(optNo);
		final boolean hasButtons = optYes != null || optNo != null;

		View actions = activity.findViewById(R.id.modal_dialog_custom_buttons);
		if (!hasButtons) {
			actions.setVisibility(View.GONE);
		} else {
			actions.setVisibility(View.VISIBLE);

			if (CoreUtils.isEmptyString(optYes)) optYes = "OK";

			btnYes.setText(optYes);

			btnYes.setOnClickListener(v -> closeDialog(activity, R.id.modal_dialog_custom, new SimpleCallback() {
				@Override
				public void onResult() {
					if (callback != null) {
						callback.onYes();
						callback.onFinally();
					}
				}
			}));

			if (hasNoButton) {
				btnNo.setVisibility(View.VISIBLE);
				btnNo.setText(optNo);
				btnNo.setOnClickListener(v -> closeDialog(activity, R.id.modal_dialog_custom, new SimpleCallback() {
					@Override
					public void onResult() {
						if (callback != null) callback.onNo();
						if (callback != null) callback.onFinally();
					}
				}));
			} else {
				btnNo.setVisibility(View.GONE);
			}
		}

		openDialog(activity, R.id.modal_dialog_custom, new SimpleCallback() {
			@Override
			public void onResult() {
				if (hasButtons) {
					Button activeButton = hasNoButton ? btnNo : btnYes;
					activeButton.setFocusable(true);
					activeButton.setFocusableInTouchMode(true);
					activeButton.requestFocus();
				}
			}
		});
	}

	public static void custom(Activity activity, int viewResourceId,
							  Callback<View> customViewCallback,
							  Callback<View> customViewFocusCallback,
							  String optYes, String optNo,
							  DialogCallback callback) {
		((AndroidDialogFactory) DialogUtils.factory).dialogCustom(activity, viewResourceId,
				customViewCallback,
				customViewFocusCallback,
				optYes, optNo, callback);
	}

	private void dialogCustom(final Activity activity, int viewResourceId,
							  Callback<View> customViewCallback,
							  final Callback<View> customViewFocusCallback,
							  String optYes, String optNo,
							  final DialogCallback callback) {

		setDismissCallback(activity, R.id.modal_dialog_custom, callback);

		ViewGroup container = activity.findViewById(R.id.modal_dialog_custom_container);
		container.removeAllViews();

		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View customView = layoutInflater.inflate(viewResourceId, container);
		if (customViewCallback!=null) customViewCallback.onResult(customView);


		final Button btnYes = activity.findViewById(R.id.btnDialogCustomPositive);
		final Button btnNo = activity.findViewById(R.id.btnDialogCustomNegative);

		final boolean hasNoButton = !CoreUtils.isEmptyString(optNo);
		final boolean hasButtons = optYes != null || optNo != null;

		View actions = activity.findViewById(R.id.modal_dialog_custom_buttons);
		if (!hasButtons) {
			actions.setVisibility(View.GONE);
		} else {
			actions.setVisibility(View.VISIBLE);

			if (CoreUtils.isEmptyString(optYes)) optYes = "OK";

			btnYes.setText(optYes);

			btnYes.setOnClickListener(v -> closeDialog(activity, R.id.modal_dialog_custom, new SimpleCallback() {
				@Override
				public void onResult() {
					if (callback!=null) {
						callback.onYes();
						callback.onFinally();
					}
				}
			}));

			if (hasNoButton) {
				btnNo.setVisibility(View.VISIBLE);
				btnNo.setText(optNo);
				btnNo.setOnClickListener(v -> closeDialog(activity, R.id.modal_dialog_custom, new SimpleCallback() {
					@Override
					public void onResult() {
						if (callback!=null) {
							callback.onNo();
							callback.onFinally();
						}
					}
				}));
			} else {
				btnNo.setVisibility(View.GONE);
			}
		}

		openDialog(activity, R.id.modal_dialog_custom, new SimpleCallback(){
			@Override
			public void onResult() {
				if (customViewFocusCallback!=null) {
					customViewFocusCallback.onResult(activity.findViewById(R.id.modal_dialog_custom));
				} else {
					if (hasButtons) {
						Button activeButton = hasNoButton?btnNo:btnYes;
						activeButton.setFocusable(true);
						activeButton.setFocusableInTouchMode(true);
						activeButton.requestFocus();
					}
				}
			}
		});
	}
}
