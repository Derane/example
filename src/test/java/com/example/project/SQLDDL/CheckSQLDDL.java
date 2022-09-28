package com.example.project.SQLDDL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckSQLDDL {

	@Test
	@DisplayName("Table student has been created")
	void checkSqlStatement() throws IOException {

		String rightSql = """
				CREATE TABLE IF NOT EXISTS Student
								(
								    id         INTEGER PRIMARY KEY,
								    number_group             INTEGER,
								    firstname TEXT,
								    lastname  TEXT,
								    birthdate DATE,
								    adress     TEXT,
								    average_grade               INTEGER
								);
				""";


		String sql = Files.readString(Paths.get("solutionDDL.sql"));
		assertTrue(sql.replaceAll("\\s+", "").trim()
				.equalsIgnoreCase(rightSql.replaceAll("\\s+", "").trim()));


	}

}
