package com.example.project.util;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtils {
	static String DEFAULT_DATABASE_NAME = "example_db";
	static String DEFAULT_USERNAME = "exampleuser";
	static String DEFAULT_PASSWORD = "examplepass";

	public static DataSource createDefaultInMemoryH2DataSource() {
		String url = formatH2ImMemoryDbUrl(DEFAULT_DATABASE_NAME);
		return createInMemoryH2DataSource(url, DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}

	public static DataSource createInMemoryH2DataSource(String url, String username, String pass) {
		JdbcDataSource h2DataSource = new JdbcDataSource();
		h2DataSource.setUser(username);
		h2DataSource.setPassword(pass);
		h2DataSource.setUrl(url);

		return h2DataSource;
	}
	private static String formatH2ImMemoryDbUrl(String databaseName) {
		return String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;DATABASE_TO_UPPER=false;", databaseName);
	}

	public static List<String> fetchColumnsNames(ResultSet resultSet) throws SQLException {
		List<String> columns = new ArrayList<>();
		while (resultSet.next()) {
			String columnName = resultSet.getString("column_name");
			columns.add(columnName);
		}
		return columns;
	}

}
