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
import java.util.ArrayList;
import java.util.List;

import static com.example.project.util.JdbcUtils.fetchColumnValues;
import static com.example.project.util.JdbcUtils.fetchTableNames;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Database should store information about employee, chats and its relations.
 * We already have two tables "employee" and "chat" that have their own identifiers.
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
	void employeeChatTableHasCorrectName() throws SQLException {
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
	void employeeChatIdTypeIsBigint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = 'employee_chat';");

			List<String> columnTypes = fetchColumnValues(resultSet, "type_name");

			assertThat(columnTypes).containsExactlyInAnyOrder("BIGINT", "BIGINT");
		}
	}

	@Test
	@Order(3)
	@DisplayName("The table has not redundant columns")
	void employeeChatTableHasAllRequiredColumns() throws SQLException {
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

			assertThat(uniqueConstraintColumn).isIn("employee_id,chat_id", "chat_id,employee_id");
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

	@Test
	@Order(7)
	@DisplayName("The table have all relations for proper column in the other tables")
	void checkInsertIntoAllTableAndCheckOutputFromJoin() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			statement.execute("""
							INSERT INTO CHAT values (1, 'programmer_chat');
										INSERT INTO employee values (1, 'Vladislav', 'Tereshuk');
										INSERT INTO employee_chat values (1, 1);
					""");
			ResultSet resultSet = statement.executeQuery("""
					SELECT * FROM employee e
					JOIN employee_chat ec on e.id = ec.employee_id
					JOIN chat c on ec.chat_id = c.id;
					""");
			boolean resultIsNotEmpty = resultSet.next();
			assertTrue(resultIsNotEmpty);
			List<String> stringColumns = new ArrayList<>();
			stringColumns.add(resultSet.getString("firstname"));
			stringColumns.add(resultSet.getString("lastname"));
			stringColumns.add(resultSet.getString("name"));
			assertThat(stringColumns).contains("Vladislav", "Tereshuk", "programmer_chat");
		}
	}

}
