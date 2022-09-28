	CREATE TABLE student(
					id BIGINT,
					email VARCHAR(255) NOT NULL,
					first_name VARCHAR(255) NOT NULL,
					last_name VARCHAR(255) NOT NULL,
					CONSTRAINT student_pk PRIMARY KEY (id),
					CONSTRAINT student_email_uq UNIQUE (email)
					);