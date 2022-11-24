package fts.vfile;

import java.io.IOException;
import java.io.InputStream;

public class ProgressableInputStream extends InputStream {

	public InputStream pipedStream;
	private VirtualFileOperationProgressListener progressListener;
	private int size;
	private int totalRead;
	
	public ProgressableInputStream(InputStream is, VirtualFileOperationProgressListener progressListener, int size) {
		this.pipedStream = is;
		this.progressListener = progressListener;
		this.size = size;
	}
	
	@Override
	public int read() throws IOException {
		int value = pipedStream.read();
		if (value>=0) updateReadBytes(1);
		return value;
	}

	@Override
	public int available() throws IOException {
		return pipedStream.available();
	}

	@Override
	public void close() throws IOException {
		pipedStream.close();
	}

	@Override
	public void mark(int readlimit) {
		pipedStream.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return pipedStream.markSupported();
	}

	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		int readBytes = pipedStream.read(buffer, byteOffset, byteCount);
		updateReadBytes(readBytes);
		return readBytes;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		int readBytes = pipedStream.read(buffer);
		updateReadBytes(readBytes);
		return readBytes;
	}
	
	private void updateReadBytes(int readBytes) throws IOException {
		if (readBytes<0) return;
		
		totalRead += readBytes;
		progressListener.updateProgress(null, totalRead, size);
	}

	@Override
	public synchronized void reset() throws IOException {
		pipedStream.reset();
	}

	@Override
	public long skip(long byteCount) throws IOException {
		return pipedStream.skip(byteCount);
	}
}
