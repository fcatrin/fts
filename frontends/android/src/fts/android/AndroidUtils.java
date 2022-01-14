package fts.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import fts.core.Log;
import fts.core.ProgressListener;
import fts.core.SimpleCallback;
import fts.core.Utils;
import fts.utils.dialogs.DialogCallback;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.SimpleDialogs;

public class AndroidUtils {
	private static int permissionsRequest = 0;
	
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
    	Log.d("DEVICENAME", buildDeviceNameLower());
    	return buildDeviceNameLower().contains("unknown android sdk");
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
		if (unpackDir.exists()) Utils.delTree(unpackDir);
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
				Utils.copyFile(is, new FileOutputStream(dstFile));
			} catch (FileNotFoundException e) {
				// this is a folder
				unpackAssets(ctx, fileName, dstDir, progressListener);
			}
		}
	}

	
	public static void checkPermissions(final WithPermissions target, String reason, final String permission, final PermissionsHandler handler) {
		final Activity activity = target.getActivity();
		if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
			
			String optYes = "Continue";
			String optNo  = "Cancel";
			DialogUtils.confirm(target.getNativeWindow(), reason, optYes, optNo, new DialogCallback(){

				@Override
				public void onYes() {
					permissionsRequest++;
					target.setPermissionHandler(permissionsRequest, handler);
					ActivityCompat.requestPermissions(activity,
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
	
}
