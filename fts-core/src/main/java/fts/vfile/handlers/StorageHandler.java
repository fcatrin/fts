package fts.vfile.handlers;

import java.io.IOException;
import java.util.List;

import fts.vfile.VirtualFile;
import fts.vfile.VirtualFileHandler;
import fts.vfile.VirtualFileOperationNotSupportedException;
import fts.vfile.VirtualFileOperationProgressListener;

public abstract class StorageHandler implements VirtualFileHandler {

	@Override
	final public void get(VirtualFile srcFile, VirtualFile dstFolder,
			VirtualFileOperationProgressListener listener) throws IOException {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	final public void put(VirtualFile srcFile, VirtualFile dstFolder,
			VirtualFileOperationProgressListener listener) throws IOException {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	final public void mkdir(VirtualFile virtualFile) {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	final public boolean delete(VirtualFile virtualFile) {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	public boolean supportsInnerCopy() {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	public void copyOrMove(VirtualFile srcFile, VirtualFile dstFolder, VirtualFileOperationProgressListener progressListener, boolean move) {
		throw new RuntimeException("Invalid call on storage");
	}
	
	@Override
	public boolean exists(VirtualFile virtualFile) {
		throw new RuntimeException("Invalid call on storage");
	}

	@Override
	public void rename(VirtualFile virtualFile, String newName) {
		throw new RuntimeException("Invalid call on storage");
	}
	
	@Override
	public void stat(VirtualFile virtualFile) {
		throw new RuntimeException("Invalid call on storage");
	}
	
	@Override
	public List<VirtualFile> listTree(VirtualFile virtualFile) throws IOException {
		throw new VirtualFileOperationNotSupportedException();
	}
	
	@Override
	public boolean hasElements() {
		return false;
	}

}
