package fts.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import fts.core.Application;
import fts.core.Context;
import fts.core.Widget;
import fts.core.NativeWindow;

public abstract class FtsActivity extends Activity {
	private NativeWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidUtils.configureAsFullscreen(this);
		
		setContentView(R.layout.main);
		
		Application app = new Application(new ComponentFactory(), new AndroidResourceLocator(this), new AndroidLogger(), new Context());
		window = Application.createWindow("", 0, 0);

		Widget rootView = app.inflate(window, getRootLayout());
		window.setContentView(rootView);
		
		GLRenderer renderer = new GLRenderer((AndroidWindow)window);
		GLSurface surface = (GLSurface)findViewById(R.id.fts_view);
		surface.setRenderer(renderer);
		surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	protected abstract String getRootLayout();
	
	public NativeWindow getFtsWindow() {
		return window;
	}
}
