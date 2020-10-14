package fts.android.demo;

import fts.android.FtsActivity;
import fts.core.Context;
import fts.core.Widget;

public class DemoActivity extends FtsActivity {

	@Override
	public void onWindowCreate() {
		Context.pointsPerPixel = 2;
		
		Widget rootView = inflate("main");
		setContentView(rootView);

	}

}
