package com.example.project.SQLDDL;


import com.example.project.util.JdbcUtils;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.example.project.util.JdbcUtils.fetchColumnValues;
import static com.example.project.util.JdbcUtils.fetchTableNames;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Table should have a single-value identifier of type BIGINT, which is a primary key
 * also table should have 3 fields as 'name', 'author', 'isbn'
 * and you need match constraints(NOT NULL, UNIQUE and CREATE PK) for them
 * table name must be single (e.g. "library", not "libraries")
 * for text fields should use varchar(255)
 */
public class FirstTask {
	private static DataSource dataSource;
	private static Statement statement;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\solutionFirstTask.sql")));
	}

	@Test
	@Order(1)
	@DisplayName("The library table has correct name")
	void libraryTableHasCorrectName() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery("SHOW TABLES");
			var tableNames = fetchTableNames(resultSet);

			Assertions.assertTrue(tableNames.contains("library"));
		}
	}

	@Test
	@Order(2)
	@DisplayName("Library Id type is Bigint")
	void libraryIdTypeIsBigint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'library' AND column_name = 'id';");

			resultSet.next();
			String idTypeName = resultSet.getString("type_name");

			Assertions.assertEquals("BIGINT", idTypeName);
		}
	}

	@Test
	@Order(3)
	@DisplayName("The library table has all the required columns")
	void libraryTableHasAllRequiredColumns() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'library';");

			List<String> columns = fetchColumnValues(resultSet, "column_name");

			assertThat(columns.size()).isEqualTo(4);
			assertThat(columns).containsExactlyInAnyOrder("id", "name", "author", "isbn");
		}
	}

	@Test
	@Order(4)
	@DisplayName("The library table String columns have correct type and length")
	void testLibraryTableStringColumnsHaveCorrectTypeAndLength() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'library' AND type_name = 'VARCHAR' AND character_maximum_length = 255;");

			List<String> stringColumns = fetchColumnValues(resultSet, "column_name");

			assertThat(stringColumns.size()).isEqualTo(3);
			assertThat(stringColumns).contains("name", "isbn", "author");
		}
	}

	@Test
	@Order(5)
	@DisplayName("The library table required columns have Not Null constrains")
	void libraryTableRequiredColumnsHaveHaveNotNullConstraint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'library' AND nullable = false;");

			List<String> notNullColumns = fetchColumnValues(resultSet, "column_name");

			assertThat(notNullColumns).contains("name");
		}
	}

	@Test
	@Order(6)
	@DisplayName("The library table required columns have unique constrains")
	void libraryTableRequiredColumnsHaveHaveUniqueConstraint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'library' AND constraint_type = 'UNIQUE';");

			resultSet.next();
			String uniqueConstraintColumn = resultSet.getString("column_list");

			assertThat(uniqueConstraintColumn).isEqualTo("isbn");
		}
	}

	@Test
	@Order(7)
	@DisplayName("The library table has primary key")
	void libraryTablesHasPrimaryKey() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'library' AND constraint_type = 'PRIMARY_KEY';");

			boolean resultIsNotEmpty = resultSet.next();

			assertThat(resultIsNotEmpty).isEqualTo(true);
		}
	}

	@Test
	@Order(8)
	@DisplayName("The library table primary key based on field 'id ")
	void libraryTableHasPrimaryKeyBasedOnProperField() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'library' AND constraint_type = 'PRIMARY_KEY';");

			resultSet.next();
			String pkColumn = resultSet.getString("column_list");

			assertThat("id", equalTo(pkColumn));
		}
	}
}
