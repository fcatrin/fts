package fts.android;

import java.util.List;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import fts.core.Callback;
import fts.core.ListOption;
import fts.core.NativeWindow;
import fts.core.SimpleCallback;
import fts.core.Utils;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.events.OnItemSelectionChangedListener;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogFactory;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.FileListPanel.FileChooserConfig;
import fts.utils.dialogs.SimpleDialogCallback;
import fts.vfile.VirtualFile;

public class AndroidDialogFactory implements DialogFactory {
	private static final int DIALOG_OPENING_THRESHOLD = 800;

	private long openTimeStart = 0;

	public AndroidDialogFactory() {}

	@Override
	public void confirm(NativeWindow window, String text, String optYes, String optNo, final DialogCallback callback) {
		final Activity activity = ((AndroidWindow)window).getActivity();
		setDismissCallback(activity, R.id.modal_dialog_actions, callback);

		TextView txtMessage = activity.findViewById(R.id.txtDialogAction);
		txtMessage.setText(text);
		
		final Button btnYes = activity.findViewById(R.id.btnDialogActionPositive);
		final Button btnNo = activity.findViewById(R.id.btnDialogActionNegative);
		
		if (Utils.isEmptyString(optYes)) optYes = "OK";
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
		
		final boolean hasNoButton = !Utils.isEmptyString(optNo);
		
		if (hasNoButton) {
			btnNo.setVisibility(View.VISIBLE);
			btnNo.setText(optNo);
			btnNo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (callback!=null) {
						callback.onNo();
						callback.onFinally();
					}
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
	public void select(NativeWindow window, List<ListOption> options, String title, final DialogListCallback callback) {
		final Activity activity = ((AndroidWindow)window).getActivity();
		setDismissCallback(activity, R.id.modal_dialog_list, callback);
		
		TextView txtTitle = activity.findViewById(R.id.txtDialogListTitle);
		if (Utils.isEmptyString(title)) {
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
	public void custom(NativeWindow window, Widget widget, String optYes, String optNo, DialogCallback callback) {
		confirm(window, "Custon Dialog not implemented on AndroidDialogs", null, null, null);
	}

	@Override
	public void browse(NativeWindow window, VirtualFile sysRoot, FileChooserConfig config,
			Callback<VirtualFile> onSelectedFileCallback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnItemSelectionChangedListener(OnItemSelectionChangedListener<ListOption> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasVisiblePanel(NativeWindow window) {
		Activity activity = ((AndroidWindow)window).getActivity();
		return getVisibleDialog(activity) != null;
	}

	@Override
	public boolean onKeyDown(NativeWindow window, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(NativeWindow window, KeyEvent event) {
		int keyCode = event.keyCode;
		
		if (keyCode == KeyEvent.KEY_BACKSPACE && hasVisiblePanel(window)) {
			
			if ((System.currentTimeMillis() - openTimeStart) < DIALOG_OPENING_THRESHOLD) {
				// ignore if this is a key up from a tap on the BACK/SELECT key
				return true;
			}
			
			return cancelDialog(window);
		}
		
		return false;
	}

	@Override
	public boolean dispatchCancelKey(NativeWindow window) {
		return cancelDialog(window);
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
		
		return
			isVisible(dialogActions)? dialogActions :
			isVisible(dialogList)   ? dialogList :
			isVisible(dialogChooser)   ? dialogChooser :
			isVisible(customDialog) ? customDialog : null;
	}
	
	private boolean isVisible(View v) {
		return v!=null && v.getVisibility() == View.VISIBLE;
	}


	public boolean cancelDialog(NativeWindow window) {
		Activity activity = ((AndroidWindow)window).getActivity();
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


}
