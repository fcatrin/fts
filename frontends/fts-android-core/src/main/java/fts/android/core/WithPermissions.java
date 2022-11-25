package fts.android.core;

import android.app.Activity;

public interface WithPermissions {
	void setPermissionHandler(int request, PermissionsHandler handler);
	AndroidWindow getAndroidWindow();
}
