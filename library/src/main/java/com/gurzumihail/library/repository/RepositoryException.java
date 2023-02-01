package com.gurzumihail.library.repository;

public class RepositoryException extends Exception {

	private static final long serialVersionUID = 8423079136762309124L;

	public RepositoryException(String message, Throwable err) {
		super(message, err);
	}
	
	public RepositoryException(String message) {
		super(message);
	}

}

