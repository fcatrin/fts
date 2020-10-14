package fts.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import fts.core.Application;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.graphics.Point;

public abstract class FtsActivity extends Activity implements GLWindowListener {
	private GLWindow nativeWindow;
	private Point bounds = new Point();

	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		bounds.x = size.x;
		bounds.y = size.y;
		
		AndroidUtils.configureAsFullscreen(this);
		setContentView(R.layout.main);
		
		Application.init(new ComponentFactory(), new AndroidResourceLocator(this), new AndroidLogger(), new fts.core.Context());

		nativeWindow = (GLWindow)Application.createNativeWindow("", 0, 0, 0);
		nativeWindow.setWindowListener(this);
				
		Widget rootView = Application.inflate(nativeWindow, getRootLayout());
		setContentView(rootView);
		
		onWindowCreate();
		
		GLRenderer renderer = new GLRenderer(nativeWindow);
		GLSurface surface = (GLSurface)findViewById(R.id.fts_view);
		surface.setRenderer(renderer);
		surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	protected abstract String getRootLayout();
	
	public GLWindow getNativeWindow() {
		return nativeWindow;
	}
	
	public void setContentView(Widget view) {
		nativeWindow.setContentView(view);
	}
	
	public Widget inflate(String layoutName) {
		return Application.inflate(nativeWindow, layoutName);
	}
	
	public Widget findWidget(String id) {
		return nativeWindow.findWidget(id);
	}

	@Override
	public void setTitle(String title) {
		// this is weird, the compiler complains if not defined in some way
		super.setTitle(title);
	}
	
	@Override
	final protected void onDestroy() {
		super.onDestroy();
		onWindowDestroy();
	}

	@Override
	final protected void onStart() {
		super.onStart();
		onWindowStart();
	}

	@Override
	final protected void onStop() {
		super.onStop();
		onWindowStop();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		return nativeWindow.dispatchKeyEvent(keyEvent);
	}

	@Override
	public boolean onKeyDown(KeyEvent keyEvent) {
		return nativeWindow.onKeyDown(keyEvent);
	}

	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		return nativeWindow.onKeyUp(keyEvent);
	}

	@Override
	public Point getBounds() {
		return bounds;
	}
	
	@Override
	public void onWindowCreate() {}

	@Override
	public void onWindowStart() {}

	@Override
	public void onWindowStop() {}

	@Override
	public void onWindowDestroy() {}

	@Override
	public void onFrame() {}

	@Override
	public void open() {}

	@Override
	public boolean sync() {
		return false;
	}
}
