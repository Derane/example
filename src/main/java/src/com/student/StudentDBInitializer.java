package src.com.student;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentDBInitializer {
	private DataSource dataSource;

	public StudentDBInitializer(DataSource dataSource) {
		this.dataSource = dataSource;


	}

	public void init() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement statement = connection.createStatement();
			statement.execute("""
					CREATE TABLE student(
					id BIGINT,
					email VARCHAR(255) NOT NULL,
					first_name VARCHAR(255) NOT NULL,
					last_name VARCHAR(255) NOT NULL,
					CONSTRAINT student_pk PRIMARY KEY (id),
					CONSTRAINT student_email_uq UNIQUE (email)
					);
					""");


		}
	}
}
