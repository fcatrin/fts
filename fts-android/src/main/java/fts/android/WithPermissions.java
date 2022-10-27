package fts.android;

import android.app.Activity;
import fts.core.NativeWindow;

public interface WithPermissions {
	void setPermissionHandler(int request, PermissionsHandler handler);
	Activity getActivity();
	NativeWindow getNativeWindow();
}
