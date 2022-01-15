package fts.android.demo;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import fts.android.AndroidUtils;
import fts.android.FtsActivity;
import fts.android.PermissionsHandler;
import fts.core.Callback;
import fts.core.Context;
import fts.core.ListOption;
import fts.core.Log;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileListPanel.FileChooserConfig;
import fts.utils.dialogs.SimpleDialogs;
import fts.vfile.VirtualFile;
import fts.vfile.handlers.LocalFileHandler;

public class DemoActivity extends FtsActivity {
	private static final String LOGTAG = DemoActivity.class.getSimpleName();
	
	@Override
	public void onWindowCreate() {
		Context.pointsPerPixel = getBounds().y / 540.0f;
		DialogUtils.factory = new SimpleDialogs();
		
		Widget rootView = inflate("main");
		setContentView(rootView);

		VirtualFile.addHandler(SystemRootHandler.ROOT_FS, new LocalFileHandler("/"));
		VirtualFile.addHandler(SystemRootHandler.ROOT_HOME, new LocalFileHandler(getFilesDir().getAbsolutePath()));
		VirtualFile.addHandler(SystemRootHandler.ROOT_SDCARD, new LocalFileHandler("/sdcard"));
		VirtualFile.addHandler(SystemRootHandler.ROOT_SYSTEM, new SystemRootHandler());
	}

	@Override
	public boolean onKeyDown(KeyEvent event) {
		if (DialogUtils.onKeyDown(getNativeWindow(), event)) return true;
		return super.onKeyDown(event);
	}

	@Override
	public boolean onKeyUp(KeyEvent event) {
		if (DialogUtils.onKeyUp(getNativeWindow(), event)) return true;
		return super.onKeyUp(event);
	}
	
	@Override
	public void onBackPressed() {
		
		if (DialogUtils.dispatchCancelKey(getNativeWindow())) {
			return;
		}

		openMainMenu();
		
	}
	
	private void openMainMenu() {
		List<ListOption> options = new ArrayList<ListOption>();
		options.add(new ListOption("msg", "Test Message"));
		options.add(new ListOption("folder", "Browse folders"));
		options.add(new ListOption("dummy", "Dummy Option"));
		options.add(new ListOption("quit", "Quit"));
		DialogUtils.select(getNativeWindow(), options, "Demo Dialog", new DialogListCallback() {
			
			@Override
			public void onItemSelected(String code) {
				if (code.equals("folder")) browseFolders();
				if (code.equals("msg")) showTestMessage();
				if (code.equals("quit")) askForQuit();
			}
		});
	}
	
	protected void browseFolders() {
		FileChooserConfig config = new FileChooserConfig();
		config.isDirOnly = true;
		
		VirtualFile folder = SystemRootHandler.getSysRoot();
		
		DialogUtils.browse(getNativeWindow(), folder, config, new Callback<VirtualFile>() {

			@Override
			public void onResult(VirtualFile folder) {
				Log.d(LOGTAG, "Selected folder :" + folder);
			}
		});
		
	}

	private void showTestMessage() {
		DialogUtils.message(getNativeWindow(), "Simple Dialog test");
	}
	
	private void askForQuit() {
		DialogUtils.confirm(getNativeWindow(), "Quit app", "OK", "Cancel", new DialogCallback() {
			
			@Override
			public void onYes() {
				finish();
			}
		});
		
	}

	@Override
	public void onWindowStart() {
		super.onWindowStart();
		
		AndroidUtils.checkPermissions(this, "This app needs to read your storage", Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionsHandler(){

			@Override
			public void onGranted() {
				Log.d(LOGTAG, "granted");
			}

			@Override
			public void onDenied() {
				Log.d(LOGTAG, "denied");
			}});
	}

}
