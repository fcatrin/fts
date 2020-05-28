package fts.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLSurface extends GLSurfaceView {

	public GLSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
	}

}
