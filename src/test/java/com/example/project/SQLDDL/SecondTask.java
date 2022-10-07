package com.example.project.SQLDDL;

import com.example.project.exception.NoneTableExistException;
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
import java.util.Optional;

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
	private static String tableName;

	@BeforeAll
	static void init() throws SQLException, IOException {
		dataSource = JdbcUtils.createDefaultInMemoryH2DataSource();
		statement = dataSource.getConnection().createStatement();
		statement.execute(Files.readString(Paths.get("src\\prepareForSecondTask.sql")));
		statement.execute(Files.readString(Paths.get("src\\solutionSecondTask.sql")));
		ResultSet resultSet = statement.executeQuery("SHOW TABLES");
		tableName = Optional.ofNullable(fetchTableNames(resultSet)
						.stream()
						.filter(tables -> !tables.equals("employee") && !tables.equals("chat"))
						.toList()
						.get(0))
				.orElseThrow(NoneTableExistException::new);
	}

	@Test
	@Order(1)
	@DisplayName("The employee_chat table has correct name")
	void employeeChatTableHasCorrectName() {
		Assertions.assertEquals("employee_chat", tableName);
	}

	@Test
	@Order(2)
	@DisplayName("Column employee_id and chat_id exist and have type is Bigint")
	void employeeChatIdTypeIsBigint() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = '%s';", tableName));

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
			ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM INFORMATION_SCHEMA.COLUMNS" +
					" WHERE table_name = '%s';", tableName));

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
			ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = '%s' AND constraint_type = 'PRIMARY_KEY';", tableName));

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
			ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
							" WHERE table_name = '%s' AND constraint_type = 'REFERENTIAL' AND column_list = 'employee_id';"
					, tableName));

			boolean resultIsNotEmpty = resultSet.next();

			assertTrue(resultIsNotEmpty);
		}
	}

	@Test
	@Order(6)
	@DisplayName("The table have all relations for proper column in the other tables")
	void employeeChatTablesHasForeignKeyToChat() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS" +
					" WHERE table_name = '%s' AND constraint_type = 'REFERENTIAL' AND column_list = 'chat_id';", tableName));

			boolean resultIsNotEmpty = resultSet.next();
			assertTrue(resultIsNotEmpty);

			statement.execute(String.format("""
							INSERT INTO CHAT values (1, 'programmer_chat');
										INSERT INTO employee values (1, 'Vladislav', 'Tereshuk');
										INSERT INTO %s values (1, 1);
					""", tableName));
			resultSet = statement.executeQuery(String.format("""
					SELECT * FROM employee e
					JOIN %s ec on e.id = ec.employee_id
					JOIN chat c on ec.chat_id = c.id;
					""", tableName));
			resultIsNotEmpty = resultSet.next();
			assertTrue(resultIsNotEmpty);
			List<String> stringColumns = new ArrayList<>();
			stringColumns.add(resultSet.getString("firstname"));
			stringColumns.add(resultSet.getString("lastname"));
			stringColumns.add(resultSet.getString("name"));
			assertThat(stringColumns).contains("Vladislav", "Tereshuk", "programmer_chat");
		}
	}

}
