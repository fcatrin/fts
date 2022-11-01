package fts.vfile.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fts.core.FileUtils;
import fts.core.Log;
import fts.vfile.VirtualFile;
import fts.vfile.VirtualFileHandler;
import fts.vfile.VirtualFileOperationProgressListener;

public class LocalFileHandler implements VirtualFileHandler {

	private static final String LOGTAG = LocalFileHandler.class.getSimpleName();
	private final File root;

	public LocalFileHandler(String path) {
		this.root = new File(path);
	}
	
	@Override
	public List<VirtualFile> list(VirtualFile virtualFile) {
		return listTreeRecursive(virtualFile, null);
	}

	@Override
	public List<VirtualFile> listTree(VirtualFile virtualFile) {
		List<VirtualFile> virtualFiles = new ArrayList<VirtualFile>();
		listTreeRecursive(virtualFile, virtualFiles);
		return virtualFiles;
	}
	
	private List<VirtualFile> listTreeRecursive(VirtualFile virtualFile, List<VirtualFile> list) {
		List<VirtualFile> virtualFiles = new ArrayList<VirtualFile>();
		
		File dir = getRelativeFile(virtualFile.getPath());
		File[] files = dir.listFiles();
		if (files!=null) {
			for(File file : files) {
				VirtualFile vf = new VirtualFile(virtualFile.getType(), null, getRelativePath(file));
				stat(vf, file);
				if (file.isDirectory()) {
					// vf.setIconResourceId(R.drawable.ic_folder_white_36dp);
				}
				virtualFiles.add(vf);
				if (list!=null) {
					list.add(vf);
					if (vf.isDirectory()) {
						listTreeRecursive(vf, list);
					}
				}
			}
		}
		return virtualFiles;
	}
	
	@Override
	public void stat(VirtualFile virtualFile) throws IOException {
		File file = getRelativeFile(virtualFile.getPath());
		stat(virtualFile, file);
	}

	private void stat(VirtualFile virtualFile, File file) {
		virtualFile.setIsDirectory(file.isDirectory());
		if (!file.isDirectory()) {
			virtualFile.setSize(file.length());
		}
		virtualFile.setModified(file.lastModified());
	}

	@Override
	public void mkdir(VirtualFile virtualFile) throws IOException {
		File dir = getRelativeFile(virtualFile.getPath());
		boolean success = (dir.exists() && dir.isDirectory()) || dir.mkdir(); 
		if (!success) {
			String msg = String.format("Cannot create directory %s", dir.getParent());
			throw new IOException(msg);
		}
	}

	@Override
	public boolean delete(VirtualFile virtualFile) {
		File dir = getRelativeFile(virtualFile.getPath());
		if (dir.isDirectory()) {
			FileUtils.delTree(dir);
			return true;
		} else {
			return dir.delete();
		}
	}

	@Override
	public boolean canSort(VirtualFile virtualFile) {
		return true;
	}

	@Override
	public VirtualFile getParentStorage(VirtualFile virtualFile) {
		return null;
	}

	
	private File getAllocationFile(VirtualFile virtualFile) {
		File dir = getRelativeFile(virtualFile.getPath());
		String path = dir.getAbsolutePath();
		if (path.equals("/")) path = "/data";
		return new File(path);
	}
	
	@Override
	public long getFreeSpace(VirtualFile virtualFile) {
		return getAllocationFile(virtualFile).getFreeSpace();
	}

	@Override
	public long getTotalSpace(VirtualFile virtualFile) {
		return getAllocationFile(virtualFile).getTotalSpace();
	}

	@Override
	public VirtualFile getStorage(VirtualFile virtualFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void get(VirtualFile srcFile, VirtualFile dstFolder,
			VirtualFileOperationProgressListener listener) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(VirtualFile srcFile, VirtualFile dstFolder,
			VirtualFileOperationProgressListener listener) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean supportsInnerCopy() {
		return true;
	}

	@Override
	public void copyOrMove(VirtualFile srcFile, VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean move) throws IOException {
		stat(srcFile);
		
		String dstPath = srcFile.buildPathWith(dstFolder);
		File srcPhysicalFile = getRelativeFile(srcFile.getPath());
		File dstPhysicalFile = getRelativeFile(dstPath);
		if (move & canMove(srcFile, dstFolder)) {
			srcPhysicalFile.renameTo(dstPhysicalFile);
			return;
		}
		
		if (!canWrite(dstFolder)) {
			String msg = String.format("Cannot write on %s", dstFolder.getPath());
			throw new IOException(msg);
		}
		
		if (srcPhysicalFile.isDirectory()) {
			File dstRoot = getRelativeFile(dstFolder.getPath());
			int nFiles = srcFile.getTreeCount(progressListener);
			FileUtils.copyTree(srcPhysicalFile, srcPhysicalFile, dstRoot, progressListener, 0, nFiles);
			if (move) {
				FileUtils.delTree(srcPhysicalFile, srcPhysicalFile, progressListener, 0, nFiles);
			}
		} else {
			long max = srcFile.getSize();
			FileInputStream is = new FileInputStream(srcPhysicalFile);
			FileOutputStream os = new FileOutputStream(dstPhysicalFile);
			FileUtils.copyFile(null, is, os, progressListener, max);
			if (move) {
				srcPhysicalFile.delete();
			}
		}
	}
	
	private boolean canWrite(VirtualFile dstFolder) {
		String testFileName = "ftswr-" + System.currentTimeMillis();
		File dstTestFile = getRelativeFile(new File(dstFolder.getPath(), testFileName));
		try {
			dstTestFile.delete();
			dstTestFile.createNewFile();
			dstTestFile.delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private boolean canMove(VirtualFile srcFile, VirtualFile dstFolder) throws IOException {
		String testFileName = "ftsfm-" + System.currentTimeMillis();
		File srcTestFile = getRelativeFile(new File(srcFile.getParent().getPath(), testFileName));
		File dstTestFile = getRelativeFile(new File(dstFolder.getPath(), testFileName));

		// create a test file and see if it can be simply moved
		srcTestFile.createNewFile();
		boolean canMove = srcTestFile.renameTo(dstTestFile);
		dstTestFile.delete();
		srcTestFile.delete();
		Log.d(LOGTAG, "canMove " + srcTestFile.getAbsolutePath() + " -> " + dstTestFile.getAbsolutePath() + " returns " + canMove);
		return canMove;
	}

	@Override
	public boolean exists(VirtualFile virtualFile) {
		File file = getRelativeFile(virtualFile.getPath());
		return file.exists();
	}

	@Override
	public void rename(VirtualFile virtualFile, String newName) {
		File src = getRelativeFile(virtualFile.getPath());
		File dst = getRelativeFile(new File(src.getParent(), newName));
		src.renameTo(dst);
	}

	@Override
	public boolean hasElements() {
		return true;
	}
	
	@Override
	public final boolean isLocal() {
		return true;
	}
	
	private File getRelativeFile(File file) {
		return getRelativeFile(file.getAbsolutePath());
	}

	private File getRelativeFile(String path) {
		return new File(root, path);
	}

	private String getRelativePath(File file) {
		String fullPath = file.getAbsolutePath();
		String relativePath = fullPath.substring(root.getAbsolutePath().length());
		if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
		
		return relativePath;
	}
}
