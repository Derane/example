package com.example.project.SQLDDL;


import com.example.project.exception.NoneTableExistException;
import com.example.project.util.JdbcUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import java.util.Optional;

import static com.example.project.util.JdbcUtils.fetchColumnValues;
import static com.example.project.util.JdbcUtils.fetchTableNames;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Table should have 3 fields as 'name', 'author', 'isbn'
 * and you need match constraints(NOT NULL and CREATE PK) for them
 * table name must be plural (e.g. "books", not "book")
 * for text fields should use varchar(255)
 */
public class FirstTask {
	private static DataSource dataSource;
	private static Statement statement;
	private static String tableName;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\solutionFirstTask.sql")));
		ResultSet resultSet = statement.executeQuery("SHOW TABLES");
		tableName = Optional.ofNullable(fetchTableNames(resultSet).get(0))
				.orElseThrow(NoneTableExistException::new);
	}

	@Test
	@Order(1)
	@DisplayName("The books table has correct name")
	void booksTableHasCorrectName() {
		assertEquals("books", tableName);
	}

	@Test
	@Order(2)
	@DisplayName("The books table has all the required columns")
	void booksTableHasAllRequiredColumns() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = '" + tableName + "';");

			List<String> columns = fetchColumnValues(resultSet, "column_name");

			assertThat(columns.size()).isEqualTo(3);
			assertThat(columns).containsExactlyInAnyOrder("name", "author", "isbn");
		}
	}

	@Test
	@Order(3)
	@DisplayName("The books table String columns have correct type and length")
	void testBooksTableStringColumnsHaveCorrectTypeAndLength() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = '" + tableName + "' AND type_name = 'VARCHAR' AND character_maximum_length = 255;");

			List<String> stringColumns = fetchColumnValues(resultSet, "column_name");

			assertThat(stringColumns.size()).isEqualTo(3);
			assertThat(stringColumns).contains("name", "isbn", "author");
		}
	}

	@Test
	@Order(4)
	@DisplayName("The books table required columns have Not Null constrains")
	void booksTableRequiredColumnsHaveHaveNotNullConstraint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = '" + tableName + "' AND nullable = false;");

			List<String> notNullColumns = fetchColumnValues(resultSet, "column_name");

			assertThat(notNullColumns).contains("name");
		}
	}

	@Test
	@Order(5)
	@DisplayName("The books table has primary key and primary key based on field 'id")
	void booksTablesHasBasedOnFieldPrimaryKey() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = '" + tableName + "' AND constraint_type = 'PRIMARY_KEY';");

			boolean resultIsNotEmpty = resultSet.next();

			assertThat(resultIsNotEmpty).isEqualTo(true);
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = '" + tableName + "' AND constraint_type = 'PRIMARY_KEY';");

			resultSet.next();
			String pkColumn = resultSet.getString("column_list");

			assertThat("isbn", equalTo(pkColumn));
		}
	}
}
