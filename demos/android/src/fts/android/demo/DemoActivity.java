package fts.android.demo;

import fts.android.FtsActivity;

public class DemoActivity extends FtsActivity {

	@Override
	protected String getRootLayout() {
		return "main";
	}

	@Override
	public boolean sync() {
		return false;
	}
	
}
