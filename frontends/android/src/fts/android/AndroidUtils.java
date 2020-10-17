package fts.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import fts.core.ProgressListener;
import fts.core.Utils;

public class AndroidUtils {
	
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
		return Build.MODEL.contains("XT1032") || Build.MODEL.contains("XT1058");
	}
    
    public static boolean isEmulator() {
    	return Build.MODEL.equals("sdk_google_atv_x86");
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

}
