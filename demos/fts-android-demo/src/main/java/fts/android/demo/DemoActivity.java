package fts.android.demo;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;

import fts.android.gl.AndroidApplication;
import fts.android.gl.AndroidDialogFactory;
import fts.android.gl.AndroidUtils;
import fts.android.gl.GLActivity;
import fts.android.gl.PermissionsHandler;
import fts.core.Callback;
import fts.ui.dialogs.SimpleDialogs;
import fts.utils.dialogs.ListOption;
import fts.core.Application;
import fts.core.Log;
import fts.ui.Widget;
import fts.ui.events.KeyEvent;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileChooserConfig;
import fts.vfile.VirtualFile;
import fts.vfile.handlers.LocalFileHandler;

public class DemoActivity extends GLActivity {
	private static final String LOGTAG = DemoActivity.class.getSimpleName();
	
	@Override
	public void onWindowCreate() {
		Application.pointsPerPixel = getBounds().y / 540.0f;
		DialogUtils.factory = new AndroidDialogFactory();
		
		Widget rootView = inflate("main");
		setContentView(rootView);

		VirtualFile.addHandler(SystemRootHandler.ROOT_FS, new LocalFileHandler("/"));
		VirtualFile.addHandler(SystemRootHandler.ROOT_HOME, new LocalFileHandler(getFilesDir().getAbsolutePath()));
		VirtualFile.addHandler(SystemRootHandler.ROOT_SDCARD, new LocalFileHandler("/sdcard"));
		VirtualFile.addHandler(SystemRootHandler.ROOT_SYSTEM, new SystemRootHandler());
	}

	@Override
	public boolean onKeyDown(KeyEvent event) {
		SimpleDialogs dialogs = (SimpleDialogs) DialogUtils.factory;
		if (dialogs.onKeyDown(this, event)) return true;
		return super.onKeyDown(event);
	}

	@Override
	public boolean onKeyUp(KeyEvent event) {
		SimpleDialogs dialogs = (SimpleDialogs) DialogUtils.factory;
		if (dialogs.onKeyUp(this, event)) return true;
		return super.onKeyUp(event);
	}
	
	@Override
	public void onBackPressed() {
		
		if (DialogUtils.dispatchCancelKey(this)) {
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
		DialogUtils.select(this, options, "Demo Dialog", new DialogListCallback() {
			
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
		
		DialogUtils.browse(this, folder, config, new Callback<VirtualFile>() {

			@Override
			public void onResult(VirtualFile folder) {
				Log.d(LOGTAG, "Selected folder :" + folder);
			}
		});
		
	}

	private void showTestMessage() {
		DialogUtils.message(this, "Simple Dialog test");
	}
	
	private void askForQuit() {
		DialogUtils.confirm(this, "Quit app", "OK", "Cancel", new DialogCallback() {
			
			@Override
			public void onYes() {
				finish();
			}
		});
		
	}

	@Override
	public void onWindowStart() {
		super.onWindowStart();

		AndroidApplication.post(new Runnable() {
			@Override
			public void run() {
				checkPermissions();
			}
		});
	}

	private void checkPermissions() {
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
