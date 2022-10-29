package fts.android;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import fts.core.Application;
import fts.core.AppContext;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.graphics.Point;

public class FtsActivity extends Activity implements GLWindowListener, WithPermissions {
	private static final String LOGTAG = FtsActivity.class.getSimpleName();
	
	private GLWindow nativeWindow;
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
		
		GLSurface glSurface = (GLSurface)findViewById(R.id.fts_view);
		glSurface.setFtsActivity(this);
		
		Application.init(new AndroidComponentFactory(this), new AndroidResourceLocator(this), new AndroidLogger(), new AppContext());

		nativeWindow = (GLWindow)Application.createNativeWindow("", 0, 0, 0);
		nativeWindow.setWindowListener(this);
		started = false;
	}
	
	protected void configureScreen() {
		AndroidUtils.configureAsFullscreen(this);
	}
	
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
		AppContext.backgroundProcessor.shutdown();
		onWindowDestroy();
	}

	@Override
	final protected void onStart() {
		super.onStart();
		
		if (started) return;
		GLRenderer renderer = new GLRenderer(this, nativeWindow);
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
		return nativeWindow.dispatchKeyEvent(keyEvent);
	}

	@Override
	public boolean onKeyDown(KeyEvent keyEvent) {
		return nativeWindow.onKeyDown(keyEvent);
	}

	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		Log.d(LOGTAG, "onKeyUp keyCode " + keyEvent.keyCode + ", mod:" + keyEvent.modifiers);
		if (keyEvent.keyCode == KeyEvent.KEY_BACKSPACE) {
			onBackPressed();
			return true;
		}
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
	
	@Override
	public File getDataDir() {
		return this.getFilesDir();
	}

	private Map<Integer, PermissionsHandler> permissionHandlers = new HashMap<Integer, PermissionsHandler>(); 
	
	@Override
	public void setPermissionHandler(int request, PermissionsHandler handler) {
		permissionHandlers.put(request,  handler);
	}
	
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    	PermissionsHandler handler = permissionHandlers.get(requestCode);
    	if (handler == null) return;
    	
    	AndroidUtils.handlePermissionsResult(permissions, grantResults, handler);
    }

	@Override
	public Activity getActivity() {
		return this;
	}
	
}
