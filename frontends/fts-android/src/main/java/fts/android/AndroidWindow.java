package fts.android;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import fts.utils.dialogs.DialogContext;

public abstract class AndroidWindow extends Activity implements DialogContext, WithPermissions {
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

    private SharedPreferences getPreferences() {
        return getSharedPreferences("main", Activity.MODE_PRIVATE);
    }

    public void savePreferences() {
        SharedPreferences preferences = getPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        savePreferences(editor);
        editor.commit();
    }

    public void loadPreferences() {
        SharedPreferences preferences = getPreferences();
        loadPreferences(preferences);
    }

    protected abstract void loadPreferences(SharedPreferences preferences);
    protected abstract void savePreferences(SharedPreferences.Editor editor);

    protected void setViewVisible(int resourceId, boolean visible) {
        AndroidUtils.setViewVisible(findViewById(resourceId), visible);
    }

}
