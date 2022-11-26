package fts.android;

public interface WithPermissions {
	void setPermissionHandler(int request, PermissionsHandler handler);
	AndroidWindow getAndroidWindow();
}
