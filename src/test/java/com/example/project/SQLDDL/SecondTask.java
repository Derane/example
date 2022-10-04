package com.example.project.SQLDDL;

import com.example.project.util.JdbcUtils;
import org.assertj.core.api.AssertionsForInterfaceTypes;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Database should store information about employee, chats and its relations.
 * We already have two tables "employee" and "chat that have their own identifiers.
 * Create link table "employee_chats" that should have a composite key that consists of two foreign key columns.
 */
public class SecondTask {
	private static DataSource dataSource;
	private static Statement statement;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\prepareForSecondTask.sql")));
		statement.execute(Files.readString(Paths.get("src\\solutionSecondTask.sql")));
	}

	@Test
	@Order(1)
	@DisplayName("The employee_chat table has correct name")
	void libraryTableHasCorrectName() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery("SHOW TABLES");
			var tableNames = fetchTableNames(resultSet);

			Assertions.assertTrue(tableNames.contains("employee_chat"));
		}
	}

	@Test
	@Order(2)
	@DisplayName("Column employee_id and chat_id exist and have type is Bigint")
	void libraryIdTypeIsBigint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'employee_chat';");

			List<String> columnTypes = fetchColumnValues(resultSet, "type_name");

			AssertionsForInterfaceTypes.assertThat(columnTypes).containsExactlyInAnyOrder("BIGINT", "BIGINT");
		}
	}

	@Test
	@Order(3)
	@DisplayName("The table has not redundant columns")
	void libraryTableHasAllRequiredColumns() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'employee_chat';");

			List<String> columns = fetchColumnValues(resultSet, "column_name");

			assertThat(columns.size()).isEqualTo(2);
		}
	}

	@Test
	@Order(4)
	@DisplayName("The relations table has a composite primary key")
	void employeeChatTableHasCompositePrimaryKey() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'employee_chat' AND constraint_type = 'PRIMARY_KEY';");

			resultSet.next();
			String uniqueConstraintColumn = resultSet.getString("column_list");

			assertThat(uniqueConstraintColumn).isEqualTo("employee_id,chat_id");
		}
	}

	@Test
	@Order(5)
	@DisplayName("The relations table has a foreign key to the employee table")
	void employeeChatTablesHasForeignKeyToEmployee() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'employee_chat' AND constraint_type = 'REFERENTIAL' AND column_list = 'employee_id';");

			boolean resultIsNotEmpty = resultSet.next();

			assertTrue(resultIsNotEmpty);
		}
	}

	@Test
	@Order(6)
	@DisplayName("The relations table has a foreign key to the chat table")
	void employeeChatTablesHasForeignKeyToChat() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = 'employee_chat' AND constraint_type = 'REFERENTIAL' AND column_list = 'chat_id';");

			boolean resultIsNotEmpty = resultSet.next();

			assertTrue(resultIsNotEmpty);
		}
	}

}
