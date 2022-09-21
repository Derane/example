CREATE TABLE IF NOT EXISTS Student
				(
				    ID         INTEGER AUTO_INCREMENT PRIMARY KEY,
				    ID_grupa           INTEGER references Grupa,
				    Number             INTEGER,
				    First_name_Student TEXT,
				    Last_name_Student  TEXT,
				    Birth_date_student DATE,
				    Adress_student     TEXT,
				    Pbal               INTEGER
				);