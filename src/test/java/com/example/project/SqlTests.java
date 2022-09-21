/*
 * Copyright 2015-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package com.example.project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlTests {
	private Connection connection;
	private Statement statement;

	@BeforeEach
	void setUp() throws SQLException {
		// 1) connect to in-memory H2 db
		connection = DriverManager.getConnection("jdbc:h2:mem:test");
		statement = connection.createStatement();
	}

	@AfterEach
	void tearDown() throws SQLException {
		connection.close();
	}

	@Test
	void checkSqlStatement() throws SQLException, IOException {

		// 2) if needed, fill this db with example data
		String sql = "CREATE TABLE STUDENTS (ID int primary key, name varchar(50))";
		statement.execute(sql);
		System.out.println("Created table students.");

		// 3) feed the student's sql statement to the db -> statement doesn't compile -> fail
		//    -> read the file with the statement
		sql = Files.readString(Paths.get("solution.sql"));
		//sql = "INSERT INTO STUDENTS (ID, name) VALUES (1, 'First'), (2, 'Second')";
		int rows = statement.executeUpdate(sql);
		// 4) compare the result of the execution of that statement -> pass or fail
		assertEquals(3, rows);

		// Example assertions with imaginary objects "result" and "row":
		// assertEquals(3, result.getNumberOfRows());
		// let's assume row is an array with: 1, "TS-1", 3
		// assertEquals("TS-1", row[1]):

	}

	@Test
	void checkAnotherSqlStatement() throws SQLException {

		// 2) if needed, fill this db with example data
		String sql = "CREATE TABLE STUDENTS (ID int primary key, name varchar(50))";
		statement.execute(sql);
		System.out.println("Created table students.");

		// 3) feed the student's sql statement to the db -> statement doesn't compile -> fail
		//    -> read the file with the statement
		sql = "INSERT INTO STUDENTS (ID, name) VALUES (1, 'First')";
		int rows = statement.executeUpdate(sql);

		// 4) compare the result of the execution of that statement -> pass or fail
		assertEquals(1, rows);
	}

}
