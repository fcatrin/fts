package fts.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import fts.core.CoreUtils;
import fts.core.Log;
import fts.core.ProgressListener;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogUtils;

public class AndroidUtils {
	private static int permissionsRequest = 0;

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void createNoMediaFile(File folder) {
		File nomediaFile = new File(folder, ".nomedia");
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    public static void configureAsFullscreen(Activity activity) {
    	configureAsFullscreen(activity, true);
    }
    
	public static void configureAsFullscreen(Activity activity, boolean hideNavigation) {
    	activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		hideSystemBar(activity, hideNavigation);
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void hideSystemBar(Activity activity, boolean hideNavigation) {
    	int requiredFlags = View.SYSTEM_UI_FLAG_FULLSCREEN |
    			View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    	
    	if (hideNavigation) {
    		requiredFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    	} else {
    		
    	}
    	
    	// casos especiales para Moto X y Moto G
    	boolean isMotoXorG = isMotoXorG();
    	if (isMotoXorG) {
    		requiredFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
    	}
    	
    	final int flags = requiredFlags;
    	
    	final View rootView = activity.getWindow().getDecorView().getRootView();
    	rootView.setSystemUiVisibility(flags);
    	rootView.postDelayed(new Runnable(){

			@Override
			public void run() {
				rootView.setSystemUiVisibility(flags);
				
			}
		}, 500);
    	
    }
    
    private static boolean isMotoXorG() {
    	String deviceName = buildDeviceNameLower();
		return deviceName.contains("xt1032") || deviceName.contains("xt1058");
	}
    
    public static boolean isEmulator() {
		String deviceName = buildDeviceNameLower();
    	Log.d("DEVICENAME", deviceName);
    	return deviceName.contains("unknown android sdk") ||  deviceName.equals("google aosp tv on x86");
    }

	public static String buildDeviceNameLower() {
		return buildDeviceName().toLowerCase(Locale.US);
	}

	public static String buildDeviceName() {
		String man = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.toLowerCase(Locale.US).contains(man.toLowerCase(Locale.US))) man = "";
		
		return (man + " " + model).trim();
	}
    
	public static void unpackAssets(Context ctx, String dir, File dstDir) throws IOException {
		unpackAssets(ctx, dir, dstDir, null);
	}
	
	public static void unpackAssets(Context ctx, String dir, File dstDir, ProgressListener progressListener) throws IOException {
		File unpackDir = new File (dstDir, dir);
		if (unpackDir.exists()) CoreUtils.delTree(unpackDir);
		unpackDir.mkdirs();

		AssetManager assets = ctx.getAssets();
		String[] files = assets.list(dir);
		
		int filesMax = files.length;
		int filesProgress = 0;
		
		for(String file : files) {
			String fileName = dir + "/" + file;
			if (progressListener!=null) progressListener.onProgress(filesProgress++, filesMax);
			try {
				InputStream is = assets.open(fileName);
				File dstFile = new File(dstDir, fileName);
				CoreUtils.copyFile(is, new FileOutputStream(dstFile));
			} catch (FileNotFoundException e) {
				// this is a folder
				unpackAssets(ctx, fileName, dstDir, progressListener);
			}
		}
	}

	public static void checkPermissions(WithPermissions target, String reason, final String permission, final PermissionsHandler handler) {
		if (ContextCompat.checkSelfPermission(target.getAndroidWindow(), permission) != PackageManager.PERMISSION_GRANTED) {
			
			String optYes = "Continue";
			String optNo  = "Cancel";
			DialogUtils.confirm(target.getAndroidWindow(), reason, optYes, optNo, new DialogCallback(){

				@Override
				public void onYes() {
					permissionsRequest++;
					target.setPermissionHandler(permissionsRequest, handler);
					ActivityCompat.requestPermissions(target.getAndroidWindow(),
							new String[]{permission},
							permissionsRequest);
				}
				
				@Override
				public void onNo() {
					handler.onDenied();
				}
				
				@Override
				public void onDismiss() {
					onNo();
				}
				
			}); 
		} else {
			handler.onGranted();
		}
		
	}
	
    public static void handlePermissionsResult(String permissions[], int[] grantResults, PermissionsHandler handler) {
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			handler.onGranted();
		} else {
			handler.onDenied();
		}
    }

	public static int getThisAPKInstalledVersionCode(Context ctx) {
		return getAPKInstalledVersionCode(ctx, ctx.getPackageName());
	}

	public static boolean isAPKInstalled(Context ctx, String apkId) {
		return getAPKInstalledVersionCode(ctx, apkId)>0;
	}

	public static int getAPKInstalledVersionCode(Context ctx, String apkId) {
		PackageManager packageManager = ctx.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(apkId, 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return 0;
		}
	}

	public static void showSoftKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(view, 0);
		}
	}

	public static void hideSoftKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (imm != null && view!=null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void installApk(Context context, String fileProviderDomain, File apk) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri apkUri = FileProvider.getUriForFile(context, fileProviderDomain, apk);
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(apkUri);
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}

	public static String[] getSignatures(Context context) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
				context.getPackageName(), PackageManager.GET_SIGNATURES);

		String[] signatures = new String[packageInfo.signatures.length];

		for(int i=0; i<packageInfo.signatures.length; i++) {
			Signature signature = packageInfo.signatures[i];
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(signature.toByteArray());

			signatures[i] = CoreUtils.md5(md.digest());
		}
		return signatures;
	}

	public static boolean checkSignature(Context context, String targetSignature) {
		try {
			String[] signatures = getSignatures(context);
			for (String signature : signatures) {
				Log.d("Signature", "Include this string as a value for SIGNATURE:" + signature);

				if (targetSignature.equals(signature)) return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getUserEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(context,  accountManager);
		if (account == null) return null;
		return account.name;
	}

	public static List<String> getGoogleUserEmails(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = getGoogleAccounts(accountManager);
		if (accounts == null || accounts.length == 0) return null;

		List<String> emails = new ArrayList<>();
		for(Account account : accounts) {
			emails.add(account.name);
		}
		return emails;
	}

	public static Account[] getGoogleAccounts(AccountManager accountManager) {
		return accountManager.getAccountsByType("com.google");
	}

	private static Account getAccount(Context context, AccountManager accountManager) {
		Account[] accounts = getGoogleAccounts(accountManager);
		if (accounts.length > 0) return accounts[0];

		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) return account;
		}
		return null;
	}

	public static void setViewVisible(View view, boolean visible) {
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
}
