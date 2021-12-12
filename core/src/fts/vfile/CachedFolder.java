package fts.vfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedFolder {
	private static long VALID_TIME = 129000; // 129[s]
	
	Map<String, VirtualFile> vfilesMap = new HashMap<String, VirtualFile>();
	List<VirtualFile> vfiles;
	
	long lastAccessed;

	public CachedFolder(List<VirtualFile> vfiles) {
		this.vfiles = vfiles;
		for(VirtualFile vfile : vfiles) {
			this.vfilesMap.put(vfile.getName(), vfile);
		}
		lastAccessed = System.currentTimeMillis();
	}
	
	public List<VirtualFile> getList() {
		lastAccessed = System.currentTimeMillis();
		return vfiles;
	}
	
	public boolean isValid() {
		return System.currentTimeMillis() - lastAccessed < VALID_TIME;
	}
	
	public VirtualFile get(String name) {
		lastAccessed = System.currentTimeMillis();
		return vfilesMap.get(name);
	}

}
