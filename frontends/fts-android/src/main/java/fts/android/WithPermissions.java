package fts.android;

import android.app.Activity;

import fts.ui.NativeWindow;

public interface WithPermissions {
	void setPermissionHandler(int request, PermissionsHandler handler);
	Activity getActivity();
	NativeWindow getNativeWindow();
}
