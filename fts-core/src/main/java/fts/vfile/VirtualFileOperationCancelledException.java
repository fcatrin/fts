package fts.vfile;

import java.io.IOException;

public class VirtualFileOperationCancelledException extends IOException {

	private static final long serialVersionUID = 1L;

	public VirtualFileOperationCancelledException() {
		super("Operation has been cancelled");
	}
}
