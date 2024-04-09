package fts.android.snippets;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import fts.android.AndroidUtils;
import fts.android.AndroidWindow;
import fts.android.R;
import fts.android.Snippet;
import fts.core.Callback;
import fts.core.SimpleBackgroundTask;
import fts.core.SimpleCallback;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogListCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.ListOption;

/*
    required on main activity:
    1. Call to requestUserAccount to initiate account selection
    2. Call this onActivityResult on activity.onActivityResult
*/

public abstract class AndroidUserAccountSnippet<T extends AndroidWindow> extends Snippet<T> {
    private static final int PERMISSIONS_REQUEST = 0x9202;
    private static final int ACCOUNT_PICK = 0x2909;

    private Callback<String> permissionsGrantedCallback;
    private SimpleCallback onAccountSelectedCallback;
    private boolean allowSelection;

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
                    callback.onCancel();
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
                        callback.onCancel();
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
                permissionsGrantedCallback.onCancel();
            }
        }
    }

    @Override
    public View getRootPanel() {
        return null;
    }

    public void requestUserAccount(String message, SimpleCallback returnCallback, boolean allowSelection) {
        onAccountSelectedCallback = returnCallback;
        this.allowSelection = allowSelection;

        checkAccountPermissions(message, new Callback<String>() {
            @Override
            public void onResult(String accountName) {
                proceedWithAccountPermissionsGranted(accountName);
            }
        });
    }

    private void proceedWithAccountPermissionsGranted(String accountName) {
        if (accountName == null) {
            List<String> userEmails = AndroidUtils.getGoogleUserEmails(activity);
            if (userEmails.isEmpty()) {
                showNoRegisteredAccountsMessage();
                return;
            } else if (userEmails.size() > 1 && allowSelection) {
                openAccountSelection(userEmails);
                return;
            } else {
                accountName = userEmails.get(0);
            }
        }
        proceedWithAccount(accountName);
    }

    private void openAccountSelection(List<String> accounts) {
        List<ListOption> options = new ArrayList<>();
        for(String account : accounts) {
            options.add(new ListOption(account, account));
        }
        DialogUtils.select(activity, options, "Select user account", new DialogListCallback() {
            @Override
            public void onItemSelected(String account) {
                proceedWithAccount(account);
            }
        });
    }

    private void proceedWithAccount(String accountName) {
        SimpleBackgroundTask task = new SimpleBackgroundTask() {
            @Override
            public void onBackgroundTask() throws Exception {
                registerSelectedAccount(accountName);
            }
            @Override
            public void onSuccess() {
                onAccountSelectedCallback.onResult();
            }
        };
        task.execute();
    }

    protected abstract void registerSelectedAccount(String accountName) throws Exception;

    private void showNoRegisteredAccountsMessage() {
        String msg = getString(R.string.no_user_accounts_on_this_device);
        DialogUtils.message(activity, msg);
    }

}
