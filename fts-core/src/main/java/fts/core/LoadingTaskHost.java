package fts.core;

public interface LoadingTaskHost {
	void showLoadingStart();
	void showLoadingProgress(String info, int progress, int total);
	void showLoadingEnd();
	void showLoadingEnd(Exception e);
	void showLoadingAlert(String title, String message, SimpleCallback callback);

	void showLoadingSuccess(String message);
}
