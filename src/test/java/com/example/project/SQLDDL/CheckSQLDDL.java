package com.example.project.SQLDDL;

import com.example.project.util.JdbcUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckSQLDDL {
	private static DataSource dataSource;
	private static Statement statement;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\solutionDDL.sql")));
	}

	@Test
	@DisplayName("Table student has been created and have a correct name")
	void testTableHasCorrectName() throws IOException, SQLException {

		ResultSet resultSet = statement.executeQuery("SHOW TABLES");
		resultSet.next();
		String tableName = resultSet.getString("table_name");

		assertEquals("student", tableName);
	}

	@Test
	void testTableHasPrimaryKey() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'student' AND constraint_type = 'PRIMARY_KEY';");

			boolean resultIsNotEmpty = resultSet.next();

			assertTrue(resultIsNotEmpty);
		}
	}

}


