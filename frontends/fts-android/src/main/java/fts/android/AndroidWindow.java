package fts.android;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import fts.utils.dialogs.DialogContext;

public class AndroidWindow extends Activity implements DialogContext, WithPermissions {
    private Map<Integer, PermissionsHandler> permissionHandlers = new HashMap<Integer, PermissionsHandler>();

    @Override
    public AndroidWindow getAndroidWindow() {
        return this;
    }

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

}
