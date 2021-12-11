package fts.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
	T map(ResultSet rs, int index) throws SQLException;
}
