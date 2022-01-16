package fts.core;

public class UserVisibleException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserVisibleException(String detailMessage) {
		super(detailMessage);
	}

	public UserVisibleException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
