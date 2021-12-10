package fts.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
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
	
	public Integer getOneInt(String sql, Object[] params) {
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			setParameters(ps, params);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}

			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs!=null) safeClose(rs);
		}
	}

	
	public int insert(String sql, Object params[]) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setParameters(ps, params);
			
			ps.execute();
			
			rs = ps.getGeneratedKeys();
			int key = 0;
			if (rs.next()){
			    key = rs.getInt(1);
			}
			return key;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs!=null) safeClose(rs);
			if (ps!=null) safeClose(ps);
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

	private void safeClose(PreparedStatement ps) {
		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void safeClose(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
