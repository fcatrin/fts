package fts.android;

import android.app.Activity;

import fts.utils.dialogs.DialogContext;

public abstract class AndroidWindow extends Activity implements DialogContext, WithPermissions {
    @Override
    public AndroidWindow getAndroidWindow() {
        return this;
    }
}
