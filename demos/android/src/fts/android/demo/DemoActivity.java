package fts.android.demo;

import java.util.ArrayList;
import java.util.List;

import fts.android.FtsActivity;
import fts.core.Context;
import fts.core.ListOption;
import fts.core.Widget;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;

public class DemoActivity extends FtsActivity {

	@Override
	public void onWindowCreate() {
		Context.pointsPerPixel = getBounds().y / 540.0f;
		
		Widget rootView = inflate("main");
		setContentView(rootView);

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
		options.add(new ListOption("dummy", "Dummy Option"));
		options.add(new ListOption("quit", "Quit"));
		DialogUtils.openListSelection(getNativeWindow(), options, "Demo Dialog", new DialogListCallback() {
			
			@Override
			public void onItemSelected(String code) {
				if (code.equals("msg")) showTestMessage();
				if (code.equals("quit")) askForQuit();
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
		DialogUtils.openDialog(getNativeWindow(), "Simple Dialog test", "OK", "Cancel", new DialogCallback() {
			
			@Override
			public void onYes() {
				finish();
			}
		});
		
	}

}
