package fts.android.gl;

import android.app.Activity;

import fts.ui.Window;

public interface WithPermissions {
	void setPermissionHandler(int request, PermissionsHandler handler);
	Activity getActivity();
	Window getNativeWindow();
}
