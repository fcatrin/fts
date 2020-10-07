package fts.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;

public class DatabaseWrapper {

	private Connection con;

	public DatabaseWrapper(Connection con) {
		this.con = con;
	}
	
	public void close() {
		if (con == null) return;
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void exec(String sql) {
		exec(sql, null);
	}

	public void exec(String sql, Object params[]) {
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			setParameters(ps, params);
			
			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setParameters(PreparedStatement ps, Object params[]) throws SQLException {
		if (params == null) return;
		
		for(int i=0; i<params.length; i++) {
			Object param = params[i];
			int index = i+1;
			if (param == null) {
				ps.setNull(index, Types.VARCHAR);
			} else if (param instanceof Integer) {
				ps.setInt(index, (Integer)param);
			} else if (param instanceof String) {
				ps.setString(index, (String)param);
			}
		}
	}

}
