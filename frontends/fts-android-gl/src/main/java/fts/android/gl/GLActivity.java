package fts.android.gl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import java.io.File;

import fts.android.AndroidUtils;
import fts.android.AndroidWindow;
import fts.android.WithPermissions;
import fts.core.Application;
import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.ui.Resources;
import fts.ui.Widget;
import fts.ui.events.KeyEvent;
import fts.ui.graphics.Point;

public abstract class GLActivity extends AndroidWindow implements GLWindowListener, WithPermissions {
	private static final String LOGTAG = GLActivity.class.getSimpleName();
	
	private GLWindow window;
	private Point bounds = new Point();
	private boolean started = false;
	
	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		bounds.x = size.x;
		bounds.y = size.y;
		
		configureScreen();
		setContentView(R.layout.main);

		AndroidGLApplication.init(this);
		
		GLSurface glSurface = (GLSurface)findViewById(R.id.fts_view);
		glSurface.setFtsActivity(this);
		
		window = (GLWindow) Resources.createNativeWindow("", 0, 0, 0);
		window.setWindowListener(this);
		started = false;
	}
	
	protected void configureScreen() {
		AndroidUtils.configureAsFullscreen(this);
	}
	
	public GLWindow getGLWindow() {
		return window;
	}
	
	public void setContentView(Widget view) {
		window.setContentView(view);
	}
	
	public Widget inflate(String layoutName) {
		return Resources.inflate(window, layoutName);
	}
	
	public <W extends Widget> W findWidget(String id) {
		return (W)window.findWidget(id);
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@Override
	final protected void onDestroy() {
		super.onDestroy();
		Application.getBackgroundProcessor().shutdown();
		onWindowDestroy();
	}

	@Override
	final protected void onStart() {
		super.onStart();
		
		if (started) return;
		GLRenderer renderer = new GLRenderer(this, window);
		GLSurface surface = (GLSurface)findViewById(R.id.fts_view);
		surface.setRenderer(renderer);
		surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		started = true;
	}

	@Override
	final protected void onStop() {
		super.onStop();
		onWindowStop();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		return window.dispatchKeyEvent(keyEvent);
	}

	@Override
	public boolean onKeyDown(KeyEvent keyEvent) {
		return window.onKeyDown(keyEvent);
	}

	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		Log.d(LOGTAG, "onKeyUp keyCode " + keyEvent.keyCode + ", mod:" + keyEvent.modifiers);
		if (keyEvent.keyCode == KeyEvent.KEY_BACKSPACE) {
			onBackPressed();
			return true;
		}
		return window.onKeyUp(keyEvent);
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
	
	@Override
	public File getDataDir() {
		return this.getFilesDir();
	}

}
