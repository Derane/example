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
import java.util.List;

import static com.example.project.util.JdbcUtils.fetchColumnsNames;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckSQLDDL {
	private static DataSource dataSource;
	private static Statement statement;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\solution0.sql")));
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
	void testPrimaryKeyBasedOnIdField() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'student' AND constraint_type = 'PRIMARY_KEY';");

			resultSet.next();
			String pkColumn = resultSet.getString("column_list");

			assertEquals("id", pkColumn);
		}
	}

	@Test
	void testTableHasCorrectAlternativeKey() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'student' AND constraint_type = 'UNIQUE';");

			resultSet.next();
			String uniqueConstraintName = resultSet.getString("constraint_name");
			String uniqueConstraintColumn = resultSet.getString("column_list");

			assertEquals("student_email_uq", uniqueConstraintName);
			assertEquals("email", uniqueConstraintColumn);
		}
	}

	@Test
	void testTableHasAllRequiredColumns() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'student';");

			List<String> columns = fetchColumnsNames(resultSet);

			assertEquals(5, columns.size());
			assertTrue(columns.containsAll(List.of("id", "email", "first_name", "last_name", "group_id")));
		}
	}

	@Test
	void testProfilesHasForeignKeyToUsers() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'student' AND constraint_type = 'REFERENTIAL' AND column_list = 'group_id';");
			boolean resultIsNotEmpty = resultSet.next();

			assertThat(resultIsNotEmpty, is(true));
		}
	}
}


