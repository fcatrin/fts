package fts.android.snippets;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import fts.android.AndroidUtils;
import fts.android.AndroidWindow;
import fts.android.R;
import fts.android.Snippet;
import fts.core.AppVersionInfo;
import fts.core.Application;
import fts.core.BackgroundTask;
import fts.core.CoreUtils;
import fts.core.ProgressListener;
import fts.core.SimpleCallback;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogUtils;

public abstract class AndroidUpdateSnippet<T extends AndroidWindow> extends Snippet<T> {
    private View btnCancel;
    private ProgressBar barProgress;
    private TextView txtInfo;
    private TextView txtProgress;
    private boolean updateIsCancelled;

    public AndroidUpdateSnippet(T activity) {
        super(activity);
    }

    protected void init(View btnCancel, ProgressBar barProgress, TextView txtInfo, TextView txtProgress) {
        this.btnCancel = btnCancel;
        this.barProgress = barProgress;
        this.txtInfo = txtInfo;
        this.txtProgress = txtProgress;
    }

    protected abstract AppVersionInfo getAppVersionInfo() throws Exception;
    protected abstract File downloadUpdate(ProgressListener progressListener) throws Exception;

    public void checkForUpdates(SimpleCallback noUpdatesCallback) {
        BackgroundTask<AppVersionInfo> task = new BackgroundTask<AppVersionInfo>() {
            @Override
            public AppVersionInfo onBackground() throws Exception {
                return getAppVersionInfo();
            }

            @Override
            public void onSuccess(AppVersionInfo systemInfo) {
                checkSystemInfo(systemInfo, noUpdatesCallback);
            }
        };
        task.execute();
    }

    private void checkSystemInfo(AppVersionInfo appVersionInfo, SimpleCallback callback) {
        final int installedVersionCode = AndroidUtils.getThisAPKInstalledVersionCode(activity);
        if (installedVersionCode >= appVersionInfo.getVersionCode()) {
            callback.onResult();
            return;
        }

        requestForUpdate(appVersionInfo);
    }

    private void requestForUpdate(AppVersionInfo appVersionInfo) {
        String msg = String.format(getString(R.string.update_available_info), appVersionInfo.getVersionName());
        DialogUtils.message(activity, msg, getString(R.string.update_installer_ok), new DialogCallback() {
            @Override
            public void onYes() {
                update(appVersionInfo);
            }

            @Override
            public void onNo() {
                activity.finish();
            }
        });
    }

    private void update(AppVersionInfo appVersionInfo) {

        ProgressListener progressListener = new ProgressListener() {
            @Override
            public boolean onProgress(int progress, int max) {
                setProgressUpdateInfo(progress, max);
                return updateIsCancelled;
            }

        };

        String msg = String.format(getString(R.string.update_download_info), appVersionInfo.getVersionName());
        setProgressInfo(msg);
        setProgressUpdateInfo(0, 0);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIsCancelled = true;
            }
        });
        btnCancel.requestFocus();
        updateIsCancelled = false;

        getRootPanel().setVisibility(View.VISIBLE);

        BackgroundTask<File> task = new BackgroundTask<File>() {
            @Override
            public File onBackground() throws Exception {
                return downloadUpdate(progressListener);
            }

            @Override
            public void onSuccess(File file) {
                if (!updateIsCancelled) {
                    AndroidUtils.installApk(activity, activity.getPackageName() + ".fileprovider", file);
                }
                activity.finish();
            }
        } ;
        task.execute();
    }

    private void setProgressInfo(String msg) {
        txtInfo.setText(msg);
    }

    private long lastTimeUpdated;

    private void setProgressUpdateInfo(int progress, int max) {
        // avoid enqueuing update requests
        // this kind of logic can be moved to xtvapps.core
        long t = System.currentTimeMillis();
        if (t - lastTimeUpdated < 100) return;
        lastTimeUpdated = t;

        Application.post(new Runnable() {
            @Override
            public void run() {
                if (max == 0) {
                    barProgress.setProgress(0);
                    barProgress.setMax(0);
                    txtProgress.setText("");
                } else {
                    barProgress.setProgress(progress);
                    barProgress.setMax(max);

                    String progressInfoText = String.format("%s / %s", CoreUtils.size2human(progress), CoreUtils.size2human(max));
                    txtProgress.setText(progressInfoText);
                }
            }
        });
    }
}
