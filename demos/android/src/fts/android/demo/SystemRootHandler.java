package fts.android.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Build;
import fts.vfile.VirtualFile;
import fts.vfile.handlers.StorageHandler;

public class SystemRootHandler extends StorageHandler {
	public static final String ROOT_SYSTEM = "sys";
	public static final String ROOT_HOME = "home";
	public static final String ROOT_SDCARD  = "sdcard";
	public static final String ROOT_FS   = "root";
	
	private static final String roots[] = {ROOT_HOME, ROOT_SDCARD, ROOT_FS};
	
	@Override
	public List<VirtualFile> list(VirtualFile virtualFile) {
		List<VirtualFile> files = new ArrayList<VirtualFile>();
		files.add(getHomeRoot());
		files.add(getSdCardRoot());
		files.add(getLocalRoot());
		return files;
	}

	@Override
	public boolean canSort(VirtualFile virtualFile) {
		return false;
	}

	/* For a given virtual file, give the ".." parent */
	@Override
	public VirtualFile getParentStorage(VirtualFile virtualFile) {
		for(String root : roots){
			if (virtualFile.isType(root)) return getSysRoot();
		}
		return null;
	}
	
	/* For a given virtual file, give the storage associated */
	@Override
	public VirtualFile getStorage(VirtualFile virtualFile) {
		if (virtualFile.isType(ROOT_HOME)) return getHomeRoot();
		if (virtualFile.isType(ROOT_SDCARD)) return getSdCardRoot();
		if (virtualFile.isType(ROOT_FS)) return getLocalRoot();
		return null;
	}
	
	private static String buildDeviceName() {
		String man = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.toLowerCase(Locale.US).contains(man.toLowerCase(Locale.US))) man = "";
		
		String fullName = (man + " " + model).trim();
		return fullName.substring(0, 1).toUpperCase(Locale.US) + fullName.substring(1);
	}
	
	public static VirtualFile getSysRoot() {
		String deviceName = buildDeviceName();
		return VirtualFile.buildRoot(ROOT_SYSTEM, deviceName, 0);
	}

	public static VirtualFile getLocalRoot() {
		String localfs = "File System";
		VirtualFile vf =  VirtualFile.buildRoot(ROOT_FS, localfs, 0);
		vf.setCanRead(true);
		vf.setCanWrite(true);
		return vf;
	}
	
	public static VirtualFile getSdCardRoot() {
		return VirtualFile.buildRoot(ROOT_SDCARD, "SDCARD", 0);
	}
	
	public static VirtualFile getHomeRoot() {
		return VirtualFile.buildRoot(ROOT_HOME, "Home", 0);
	}

	@Override
	public long getFreeSpace(VirtualFile virtualFile) {
		return 0;
	}

	@Override
	public long getTotalSpace(VirtualFile virtualFile) throws IOException {
		return 0;
	}

	@Override
	public boolean isLocal() {
		return true;
	}


}
