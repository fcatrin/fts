package fts.vfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fts.vfile.handlers.LocalFileHandler;

public class VirtualFile {
	private static final String ROOT_LOCAL = "local";
	public static final String TYPE_SEPARATOR = "://";
	public static final String CONTAINER_SEPARATOR = ":";
	public static final String PATH_SEPARATOR = "/";
	private static final String LOGTAG = VirtualFile.class.getSimpleName();
	
	private static Map<String, VirtualFileHandler> handlers = new HashMap<String, VirtualFileHandler>();
	private static final VirtualFileHandler LOCAL_HANDLER = new LocalFileHandler("/");
	
	private String type;
	private String container;
	private String path;
	private boolean isDirectory;
	private long size;
	private long modified;
	private String friendlyName;
	VirtualFile parent;
	private boolean hidden;
	private boolean isParent = false;
	private boolean isLoading = false;
	private boolean canRead = true;
	private boolean canWrite = true;
	private boolean isStorage = false;
	
	static int iconResourceIdDefault = 0;
	int iconResourceId = 0;
	private String mountPoint;
	private String nativePath;
	
	public static void addHandler(String type, VirtualFileHandler handler) {
		handlers.put(type.toLowerCase(Locale.US), handler);
	}
	
	// url format is  type://path/to/file:optional/internal/file
	public VirtualFile(String url) {
		init(url);
	}
	
	public VirtualFile(String type, String container, String path) {
		init(type, container, path);
	}
	
	public VirtualFile(VirtualFile dstFolder, String name) {
		String innerPath = dstFolder.path + PATH_SEPARATOR + name;
		innerPath = innerPath.replace("//", "/");
		init(dstFolder.type, dstFolder.container, innerPath);
	}

	public VirtualFile(File file) {
		init(ROOT_LOCAL, null, file.getAbsolutePath());
	}

	public static VirtualFile buildRoot(String type, String friendlyName, int iconResourceId) {
		return buildRoot(type, friendlyName, iconResourceId, null);
	}
	
	public static VirtualFile buildRoot(String type, String friendlyName, int iconResourceId, String path) {
		String trailingPath = path!=null ? path : "";
		
		VirtualFile vf = new VirtualFile(type + TYPE_SEPARATOR + trailingPath);
		vf.friendlyName = friendlyName;
		vf.setIconResourceId(iconResourceId);
		vf.setIsDirectory(true);
		vf.setCanRead(false);
		vf.setCanWrite(false);
		vf.setIsStorage(true);
		return vf;
	}
	
	private void init(String url) {
		int pType = url.indexOf(TYPE_SEPARATOR);
		if (pType < 0) throw new RuntimeException("Invalid url format " + url);
		
		String type = url.substring(0, pType);
		String remaining = url.substring(pType+TYPE_SEPARATOR.length());
		
		int pColon = remaining.indexOf(CONTAINER_SEPARATOR);
		String container = pColon < 0 ? null : remaining.substring(0, pColon);
		String finalPath = pColon < 0 ? remaining : remaining.substring(0, pColon + CONTAINER_SEPARATOR.length());
		
		init(type, container, finalPath);
	}
	
	private void init(String type, String container, String path) {
		this.type = type.toLowerCase(Locale.US);
		this.container = normalizePath(container);
		this.path = normalizePath(path);
		
		if (!handlers.containsKey(type) && !ROOT_LOCAL.equals(type)) throw new RuntimeException("Unknown file handler " + type);
	}
	
	private static String normalizePath(String path) {
		if (path == null) return null;
		return path.startsWith(PATH_SEPARATOR) ? path.substring(PATH_SEPARATOR.length()) : path;
	}
	
	public String getLocalName() {
		return isLocal() ? getPath() : getUrl();
	}
	
	public String getPath() {
		return "/" + path;
	}
	
	public String getName() {
		int p = getLastSeparator();
		return p < 0 ? path : path.substring(p + PATH_SEPARATOR.length());
	}

	protected String getParentPath() {
		if (path.length() == 0) return null;
		int p = getLastSeparator();
		return p < 0 ? "" : path.substring(0, p);
	}
	
	private int getLastSeparator() {
		return path.lastIndexOf("/");
	}
	
	public VirtualFile getParent() {
		if (parent!=null) return parent;
		
		String parentPath = getParentPath();
		if (parentPath == null || parentPath.equals(mountPoint)) {
			return getStorageParent();
		}
		
		parent = new VirtualFile(type, container, parentPath);
		parent.setIsDirectory(true);
		return parent;
	}
	
	public VirtualFile getStorageParent() {
		for(VirtualFileHandler handler : handlers.values()) {
			VirtualFile parent = handler.getParentStorage(this);
			if (parent!=null) return parent;
		}
		return null;
	}

	public VirtualFile getStorage() {
		for(VirtualFileHandler handler : handlers.values()) {
			VirtualFile parent = handler.getStorage(this);
			if (parent!=null) return parent;
		}
		return null;
	}
	
	public VirtualFileHandler getHandler() {
		if (ROOT_LOCAL.equals(type)) return LOCAL_HANDLER; 
		return handlers.get(type);
	}

	public List<VirtualFile> list() throws IOException {
		VirtualFileHandler virtualFileHandler = getHandler();
		return virtualFileHandler.list(this);
	};

	public List<VirtualFile> listTree() throws IOException {
		VirtualFileHandler virtualFileHandler = getHandler();
		return virtualFileHandler.listTree(this);
	};
	
	public void mkdir() throws IOException {
		VirtualFileHandler virtualFileHandler = getHandler();
		virtualFileHandler.mkdir(this);
	};

	public void renameTo(String newName) throws IOException{
		VirtualFileHandler virtualFileHandler = getHandler();
		virtualFileHandler.rename(this, newName);
	};

	public void delete() throws IOException{
		VirtualFileHandler virtualFileHandler = getHandler();
		virtualFileHandler.delete(this);
	};
	
	public long getFreeSpace() {
		VirtualFileHandler virtualFileHandler = getHandler();
		return virtualFileHandler.getFreeSpace(this);
	}

	public long getTotalSpace() throws IOException {
		VirtualFileHandler virtualFileHandler = getHandler();
		return virtualFileHandler.getTotalSpace(this);
	}
	
	public boolean canSort(){
		VirtualFileHandler virtualFileHandler = getHandler();
		return virtualFileHandler.canSort(this);
	};
	
	public void stat() throws IOException {
		VirtualFileHandler virtualFileHandler = getHandler();
		virtualFileHandler.stat(this);
	}

	@Override
	public String toString() {
		return getUrl() + " {folder:" + isDirectory + ", size:" + size + ", modified:" + modified + ", nativePath:" + nativePath + "}";
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setIsDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	public boolean isStorage() {
		return isStorage;
	}

	public void setIsStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getType() {
		return type;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public int getIconResourceId() {
		return iconResourceId != 0 ? iconResourceId : iconResourceIdDefault;
	}
	
	public static void setIconResourceIdDefault(int iconResourceId) {
		iconResourceIdDefault = iconResourceId;
	}

	public void setIconResourceId(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}

	public String getUrl() {
		String strContainer = container == null ? "" : container + ":";
		return type + TYPE_SEPARATOR + strContainer + path;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isType(String type) {
		return type.equals(this.type);
	}
	
	public boolean isParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	
	public boolean isLoading() {
		return isLoading;
	}

	public boolean canRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean canWrite() {
		return canWrite;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public void copyOrMove(VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean move) throws IOException {
		VirtualFileHandler srcHandler = getHandler();
		VirtualFileHandler dstHandler = dstFolder.getHandler();
		
		boolean isSameHandler = srcHandler.equals(dstHandler);
		boolean supportsInnerCopy = isSameHandler && srcHandler.supportsInnerCopy();
		
		// one of both is local
		boolean srcIsLocal = srcHandler.isLocal();
		boolean dstIsLocal = dstHandler.isLocal();
		
		if (!srcIsLocal && !dstIsLocal && !supportsInnerCopy) {
			throw new RuntimeException("Source or destination must be local");
		}
		
		if (supportsInnerCopy) {
			srcHandler.copyOrMove(this, dstFolder, progressListener, move);
		} else {
			if (isDirectory()) {
				copyOrMoveTree(dstFolder, progressListener, srcIsLocal, move);
			} else {
				copyOrMoveFile(dstFolder, progressListener, srcIsLocal, move);
			}
		}
	}
	
	private void copyOrMoveTree(VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean srcIsLocal, boolean move) throws IOException {
		int total = getTreeCount(progressListener);
		copyTree(this, dstFolder, progressListener, srcIsLocal, move, 0, total);
	}
	
	private int copyTree(VirtualFile srcFolder, VirtualFile dstFolder, VirtualFileOperationProgressListener listener, boolean srcIsLocal, boolean move, int count, int max) throws IOException {
		List<VirtualFile> files = srcFolder.list();
		
		VirtualFile newFolder = srcFolder.buildWith(dstFolder);
		newFolder.mkdir();
		
		String action = move ? "Moving" : "Copying";
		for(VirtualFile file: files) {
			String info = action + " files and folders {count}";
			listener.updateProgress(info, count+1, max);

			if (file.isDirectory()) {
				count = copyTree(file, newFolder, listener, srcIsLocal, move, count, max);
			} else {
				String fileName = file.getPath().substring(getPath().length()+1);
				info = action + " " + fileName;
				listener.updateProgress(info, count+1, max);
				file.copyOrMoveFile(newFolder, listener, srcIsLocal, move);
				count++;
			}
		}
		if (move) {
			srcFolder.delete();
		}
		return count;
	}
	
	private void copyOrMoveFile(VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean srcIsLocal, boolean move) throws IOException {
		VirtualFileHandler srcHandler = getHandler();
		if (srcIsLocal) {
			VirtualFileHandler dstHandler = dstFolder.getHandler();
			dstHandler.put(this, dstFolder, progressListener);
		} else {
			srcHandler.get(this, dstFolder, progressListener);
		}
		if (move) {
			srcHandler.delete(this);
		}
	}

	public VirtualFileInfo getInfo(VirtualFileOperationProgressListener listener) throws IOException {
		VirtualFileInfo elementInfo = new VirtualFileInfo();
		getTreeInfo(elementInfo, listener, true);
		return elementInfo;
	}
	
	public int getTreeCount(VirtualFileOperationProgressListener listener) throws IOException {
		VirtualFileInfo elementInfo = new VirtualFileInfo();
		getTreeInfo(elementInfo, listener, false);
		return elementInfo.elements;
	}
	
	
	public File retrieveLocal() {
		if (!isLocal()) return null;
		return new File(getPath());
	}
	public File retrieve(VirtualFileOperationProgressListener listener) throws IOException {
		File localFile = retrieveLocal();
		if (localFile!=null) return localFile;
		
		File tmpFile = File.createTempFile("rbx", null);
		tmpFile.delete();
		tmpFile.mkdir();
		return retrieve(tmpFile, listener);
	}
	
	public File retrieve(File baseDir, VirtualFileOperationProgressListener listener) throws IOException {

		VirtualFile dstFolder = new VirtualFile(baseDir);
		
		VirtualFileHandler srcHandler = getHandler();
		srcHandler.get(this, dstFolder, listener);
		return new File(baseDir, getName());
	}
	
	private void getTreeInfo(VirtualFileInfo treeInfo, VirtualFileOperationProgressListener listener, boolean includeDirs) throws IOException {
		if (!isDirectory()) return;
		
		List<VirtualFile> files = list();
		
		for(VirtualFile file: files) {
			if (file.isDirectory()) {
				file.getTreeInfo(treeInfo, listener, includeDirs);
				if (includeDirs) treeInfo.elements++;
			} else {
				treeInfo.size += file.size;
				treeInfo.elements++;
			}
			
			String info = "Getting files and folder info {countonly}";
			listener.updateProgress(info, 1, treeInfo.elements);
		}
	}
	
	
	public String buildPathWith(VirtualFile newParent) {
		String dstPath = newParent.getPath() + VirtualFile.PATH_SEPARATOR + getName();
		return dstPath.replace("//", "/");
	}
	
	public VirtualFile buildWith(VirtualFile newParent) {
		return new VirtualFile(newParent, getName());
	}

	public boolean supportsInnerCopy() {
		VirtualFileHandler handler = getHandler();
		return handler.supportsInnerCopy();
	}
	
	public boolean exists() throws IOException {
		VirtualFileHandler handler = getHandler();
		return handler.exists(this);
	}
	
	public boolean existsAndReachable() {
		try {
			return exists();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
	
	public String getMountPoint() {
		return mountPoint;
	}
	
	public String getRelativePath() {
		String relativePath = getPath();
		if (mountPoint == null) return relativePath;
		return relativePath.substring(mountPoint.length());
	}

	public File getContainerFile() {
		return new File("/" + container);
	}
	
	public String getContainer() {
		return container;
	}

	public boolean hasExtension(String ext) {
		return path.toLowerCase(Locale.US).endsWith("." + ext.toLowerCase());
	}
	
	public String getExtension() {
		int p = path.lastIndexOf(".");
		if (p>0) {
			return path.substring(p+1);
		}
		return path;
	}

	public VirtualFile getContainerVirtualFile() {
		if (container == null) return null;
		return new VirtualFile(ROOT_LOCAL, null, container);
	}

	public boolean isLocal() {
		return getHandler().isLocal();
	}
	
	public static String extractName(String path) {
		int p = path.lastIndexOf("/");
		if (p>0) {
			return path.substring(p+1);
		}
		return path;
	}

	public static VirtualFile fromLocal(String localPath) {
		return new VirtualFile(ROOT_LOCAL, null, localPath);
	}

	public String getNativePath() {
		return nativePath;
	}

	public void setNativePath(String nativePath) {
		this.nativePath = nativePath;
	}
	
	public void setParent(VirtualFile parent) {
		this.parent = parent;
	}

	public VirtualFile clone() {
		VirtualFile vf = new VirtualFile(type, container, path);
		vf.setIsDirectory(isDirectory);
		vf.setSize(size);
		vf.setModified(modified);
		vf.setFriendlyName(friendlyName);
		vf.setParent(parent);
		vf.setHidden(hidden);
		vf.setIsParent(isParent);
		vf.setCanRead(canRead);
		vf.setCanWrite(canWrite);
		vf.setIsStorage(isStorage);
		vf.setIconResourceId(iconResourceId);
		vf.setMountPoint(mountPoint);
		vf.setNativePath(nativePath);
		return vf;
	}

}
