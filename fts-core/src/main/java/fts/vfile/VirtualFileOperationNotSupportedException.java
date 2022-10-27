package fts.vfile;

import java.io.IOException;

public class VirtualFileOperationNotSupportedException extends IOException {

	private static final long serialVersionUID = 1L;

	public VirtualFileOperationNotSupportedException() {
		super("Operation not supported");
	}
}
