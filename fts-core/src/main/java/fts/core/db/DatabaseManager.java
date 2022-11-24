package fts.core.db;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import fts.core.SharedPreferences;

public abstract class DatabaseManager {
	private static final String KEY_VERSION = "version";
	
	private String url;
	private SharedPreferences prefs;
	private DatabaseWrapper con = null;
	
	public DatabaseManager(File dataDir, String databaseName, int databaseVersion) throws IOException, SQLException {
		prefs = new SharedPreferences(dataDir, "db_"+ databaseName);
		
		File dbFile = new File(dataDir, databaseName + ".db");
		url = "jdbc:sqlite:" + dbFile.getCanonicalPath();
		
		init(databaseVersion);
	}
	
	public DatabaseWrapper getConnection() throws SQLException {
		if (con!=null) return con;
		
		con = new DatabaseWrapper(DriverManager.getConnection(url));
		return con;
	}

	private void closeConnection() {
		if (con == null) return;
		con.close();
		con = null;
	}

	private void init(int version) throws SQLException {
		try {
			DatabaseWrapper con = getConnection();
			int lastVersion = prefs.getInt(KEY_VERSION);
			if (lastVersion < version) {
				onUpgrade(con, lastVersion, version);
			}
			onCreate(con);
			
			prefs.setInt(KEY_VERSION, version);
			prefs.commit();
		} finally {
			closeConnection();
		}
	}

	public abstract void onUpgrade(DatabaseWrapper con, int lastVersion, int version);
	public abstract void onCreate(DatabaseWrapper con);

	public <T> T run(DatabaseRunnable<T> runnable) throws SQLException {
		try {
			return runnable.run(getConnection());
		} finally {
			closeConnection();
		}
	}
	
	public void runSimple(SimpleDatabaseRunnable runnable) throws SQLException {
		try {
			runnable.run(getConnection());
		} finally {
			closeConnection();
		}
	}

}
