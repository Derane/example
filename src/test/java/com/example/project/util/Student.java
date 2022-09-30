package com.example.project.util;

import java.sql.Date;
import java.util.Objects;

public class Student {
	private Integer ID;
	private Integer number;
	private String first_name_Student;
	private String last_name_Student;
	private Date birth_date_student;
	private String adress_student;
	private Double pbal;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return Objects.equals(ID, student.ID);
	}


	public Student(Integer ID, Integer number, String first_name_Student, String last_name_Student, Date birth_date_student, String adress_student, Double pbal) {
		this.ID = ID;
		this.number = number;
		this.first_name_Student = first_name_Student;
		this.last_name_Student = last_name_Student;
		this.birth_date_student = birth_date_student;
		this.adress_student = adress_student;
		this.pbal = pbal;
	}


}
