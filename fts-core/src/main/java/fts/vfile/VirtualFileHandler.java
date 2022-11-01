package fts.vfile;

import java.io.IOException;
import java.util.List;

public interface VirtualFileHandler {
	public List<VirtualFile> list(VirtualFile virtualFile) throws IOException;
	public void get(VirtualFile srcFile, VirtualFile dstFolder, VirtualFileOperationProgressListener listener) throws IOException;
	public void put(VirtualFile srcFile, VirtualFile dstFolder, VirtualFileOperationProgressListener listener) throws IOException;
	public void mkdir(VirtualFile virtualFile) throws IOException;
	public boolean delete(VirtualFile virtualFile) throws IOException;
	public boolean canSort(VirtualFile virtualFile);
	public VirtualFile getParentStorage(VirtualFile virtualFile);
	public long getFreeSpace(VirtualFile virtualFile);
	public long getTotalSpace(VirtualFile virtualFile) throws IOException;
	public VirtualFile getStorage(VirtualFile virtualFile);
	public boolean supportsInnerCopy();
	public boolean hasElements();
	public void copyOrMove(VirtualFile srcFile, VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean move) throws IOException;
	public boolean exists(VirtualFile virtualFile) throws IOException;
	public void rename(VirtualFile virtualFile, String newName) throws IOException;
	public List<VirtualFile> listTree(VirtualFile virtualFile) throws IOException;
	public void stat(VirtualFile virtualFile) throws IOException;
	public boolean isLocal();
}
