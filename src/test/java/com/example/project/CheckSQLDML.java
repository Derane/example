package com.example.project;

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

		// 2) if needed, fill this db with example data
		String sql = """
				CREATE TABLE IF NOT EXISTS Student
								(
								    ID_student         INTEGER AUTO_INCREMENT PRIMARY KEY,
								    Number             INTEGER,
								    First_name_Student TEXT,
								    Last_name_Student  TEXT,
								    Birth_date_student DATE,
								    Adress_student     TEXT,
								    Pbal               INTEGER
								);
				""";
		statement.execute(sql);
		System.out.println("Created table students.");

		// 3) feed the student's sql statement to the db -> statement doesn't compile -> fail
		//    -> read the file with the statement
		sql = Files.readString(Paths.get("solution.sql"));
		//sql = "INSERT INTO STUDENTS (ID, name) VALUES (1, 'First'), (2, 'Second')";
		int rows = statement.executeUpdate(sql);
		// 4) compare the result of the execution of that statement -> pass or fail
		assertEquals(1, rows);

		// Example assertions with imaginary objects "result" and "row":
		// assertEquals(3, result.getNumberOfRows());
		// let's assume row is an array with: 1, "TS-1", 3
		// assertEquals("TS-1", row[1]):

	}
}
