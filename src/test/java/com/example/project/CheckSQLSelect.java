package com.example.project;

import com.example.project.util.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckSQLSelect {
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
	@DisplayName("Select student into student's table")
	void checkSqlStatement() throws SQLException, IOException {

		// 2) if needed, fill this db with example data
		String sql = """
				CREATE TABLE IF NOT EXISTS Student
								(
								    ID         INTEGER AUTO_INCREMENT PRIMARY KEY,
								    Number             INTEGER,
								    First_name_Student TEXT,
								    Last_name_Student  TEXT,
								    Birth_date_student DATE,
								    Adress_student     TEXT,
								    Pbal               INTEGER
								);
				""";
		statement.execute(sql);
		String anotherSql = """
				INSERT INTO Student
				values (1, 3, 'Vasya', 'Petrov', '2001-05-11', 'City: Kyiv, adress: Novikova 6', 3.3)
				""";
		statement.execute(anotherSql);
		Student rightStudent = new Student(1, 3, "Vasya", "Petrov", Date.valueOf("2001-05-11"),
				"City: Kyiv, adress: Novikova 6", 3.3);

		sql = Files.readString(Paths.get("solutionDML.sql"));

		ResultSet resultSet = statement.executeQuery(sql);
		resultSet.next();
		Student student = new Student(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3),
				resultSet.getString(4), resultSet.getDate(5), resultSet.getString(6), resultSet.getDouble(7));

		assertEquals(rightStudent, student);


	}
}
