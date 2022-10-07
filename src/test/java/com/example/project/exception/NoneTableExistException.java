package com.example.project.exception;

public class NoneTableExistException extends RuntimeException {
	public NoneTableExistException() {
		super("Please create at least 1 table!");
	}
}
