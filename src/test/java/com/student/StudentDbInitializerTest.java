package com.student;

import com.example.project.util.JdbcUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentDbInitializerTest {
	private static DataSource dataSource;

	@BeforeAll
	static void init() throws SQLException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		src.com.student.StudentDBInitializer studentDBInitializer = new src.com.student.StudentDBInitializer(dataSource);
		studentDBInitializer.init();
	}

	@Test
	void testTableHasCorrectName() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery("SHOW TABLES");
			resultSet.next();
			String tableName = resultSet.getString("table_name");

			assertEquals("student", tableName);
		}
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

	@Test
	void testPrimaryKeyHasCorrectName() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'student' AND constraint_type = 'PRIMARY_KEY';");

			resultSet.next();
			String pkConstraintName = resultSet.getString("constraint_name");

			assertEquals("student_pk", pkConstraintName);
		}
	}
}
