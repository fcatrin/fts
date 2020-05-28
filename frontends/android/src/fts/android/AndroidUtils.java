package fts.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

}
