package com.example.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckSQLDDL {

	@Test
	@DisplayName("Table student has been created")
	void checkSqlStatement() throws SQLException, IOException {

		String rightSql = "CREATE TABLE IF NOT EXISTS Student\n" +
				"\t\t\t\t(\n" +
				"\t\t\t\t    ID_student         INTEGER AUTO_INCREMENT PRIMARY KEY,\n" +
				"\t\t\t\t    ID_grupa           INTEGER references Grupa,\n" +
				"\t\t\t\t    Number             INTEGER,\n" +
				"\t\t\t\t    First_name_Student TEXT,\n" +
				"\t\t\t\t    Last_name_Student  TEXT,\n" +
				"\t\t\t\t    Birth_date_student DATE,\n" +
				"\t\t\t\t    Adress_student     TEXT,\n" +
				"\t\t\t\t    Pbal               INTEGER\n" +
				"\t\t\t\t);";


		String sql = Files.readString(Paths.get("solutionDDL.sql"));
		assertTrue(sql.replaceAll("\\s+", "")
				.equalsIgnoreCase(rightSql.replaceAll("\\s+", "")));


	}

}
