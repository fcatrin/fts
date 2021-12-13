package fts.android.demo;

import java.util.ArrayList;
import java.util.List;

import fts.android.FtsActivity;
import fts.core.Callback;
import fts.core.Context;
import fts.core.ListOption;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileListPanel.FileChooserConfig;
import fts.vfile.VirtualFile;
import fts.vfile.handlers.LocalFileHandler;

public class DemoActivity extends FtsActivity {

	@Override
	public void onWindowCreate() {
		Context.pointsPerPixel = getBounds().y / 540.0f;
		
		Widget rootView = inflate("main");
		setContentView(rootView);

		VirtualFile.addHandler("local", new LocalFileHandler());
	}

	@Override
	public boolean onKeyDown(KeyEvent event) {
		if (DialogUtils.onKeyDown(event)) return true;
		return super.onKeyDown(event);
	}

	@Override
	public boolean onKeyUp(KeyEvent event) {
		if (DialogUtils.onKeyUp(event)) return true;
		return super.onKeyUp(event);
	}
	
	@Override
	public void onBackPressed() {
		
		if (DialogUtils.dispatchCancelKey()) {
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
		DialogUtils.openListSelection(getNativeWindow(), options, "Demo Dialog", new DialogListCallback() {
			
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
		
		VirtualFile folder = new VirtualFile(getFilesDir().getParentFile());
		
		DialogUtils.openFileBrowser(getNativeWindow(), folder, config, new Callback<VirtualFile>() {

			@Override
			public void onResult(VirtualFile result) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void showTestMessage() {
		DialogUtils.openDialog(getNativeWindow(), "Simple Dialog test", "OK", new DialogCallback() {
			
			@Override
			public void onYes() {}
		});
	}
	
	private void askForQuit() {
		DialogUtils.openDialog(getNativeWindow(), "Quit app", "OK", "Cancel", new DialogCallback() {
			
			@Override
			public void onYes() {
				finish();
			}
		});
		
	}

}
