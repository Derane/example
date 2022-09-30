package com.example.project.SQLDML;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckSQLDML {
	private Connection connection;
	private Statement statement;

	@BeforeEach
	void setUp() throws SQLException {
		// 1) connect to in-memory H2 db
		connection = DriverManager.getConnection("jdbc:h2:mem:test");
		statement = connection.createStatement();

	}

	@AfterEach
	void tearDown() throws SQLException {
		connection.close();
	}

	@Test
	@DisplayName("Insert student into student's table")
	void checkSqlStatement() throws SQLException, IOException {
		String sql = """
				CREATE TABLE IF NOT EXISTS Student
								(
								    id         INTEGER PRIMARY KEY,
								    number_group             INTEGER,
								    first_name TEXT,
								    last_name  TEXT,
								    birth_date DATE,
								    address     TEXT,
								    average_grade               INTEGER
								);
				""";
		statement.execute(sql);


		sql = Files.readString(Paths.get("src\\solution.sql"));

		int rows = statement.executeUpdate(sql);

		assertEquals(1, rows);
	}
}
