package fts.ui;

public interface BackgroundProcessor {
    <T> void exec(BackgroundTask<T> task);

    void start();
    void shutdown();
}
