package fts.core.db;

public interface DatabaseRunnable<T> {
	T run(DatabaseWrapper con);
}
