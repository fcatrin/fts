package fts.android.snippets;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import fts.android.AndroidWindow;
import fts.android.R;
import fts.android.Snippet;
import fts.core.Callback;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogUtils;

public class AndroidUserAccountSnippet<T extends AndroidWindow> extends Snippet<T> {
    private static final int PERMISSIONS_REQUEST = 0x92;
    private static final int ACCOUNT_PICK = 0x29;

    private Callback<String> permissionsGrantedCallback;

    public AndroidUserAccountSnippet(T activity) {
        super(activity);
    }

    public void checkAccountPermissions(String message, final Callback<String> callback) {

        // Android >= Oreo
        // Uses an activity to request the account then returns on onActivityResult
        //
        // Older androids
        // Asks for permissions, then gets the accounts through the callback

        permissionsGrantedCallback = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String optYes = getString(R.string.login_select_account_yes);
            String optNo  = getString(R.string.login_select_account_no);
            DialogUtils.confirm(activity, message, optYes, optNo, new DialogCallback() {
                @Override
                public void onYes() {
                    chooseAccount();
                }

                @Override
                public void onNo() {
                    callback.onError(null);
                }
            });
        } else {
            String permission =  Manifest.permission.GET_ACCOUNTS;
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                String optNo  = getString(R.string.login_grant_permissions_yes);
                String optYes = getString(R.string.login_grant_permissions_no);

                DialogUtils.confirm(activity, message, optYes, optNo, new DialogCallback() {
                    @Override
                    public void onYes() {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{permission}, PERMISSIONS_REQUEST);
                    }
                    @Override
                    public void onNo() {
                        callback.onError(null);
                    }
                });
            } else {
                callback.onResult(null);
            }
        }
    }

    private void chooseAccount() {
        Intent intent = AccountManager.newChooseAccountIntent(
                null,
                null,
                new String[] {"com.google"},
                true,
                null,
                null,
                null,
                null);
        activity.startActivityForResult(intent, ACCOUNT_PICK);
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACCOUNT_PICK) {
                Object oAccountName = data.getExtras().get(AccountManager.KEY_ACCOUNT_NAME);
                String accountName = oAccountName == null ? null : oAccountName.toString();
                permissionsGrantedCallback.onResult(accountName);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == ACCOUNT_PICK) {
                permissionsGrantedCallback.onError(null);
            }
        }
    }

    @Override
    public View getRootPanel() {
        return null;
    }
}
