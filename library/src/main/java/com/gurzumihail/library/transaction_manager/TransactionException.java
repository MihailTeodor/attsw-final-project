package com.gurzumihail.library.transaction_manager;

public class TransactionException extends Exception {

	private static final long serialVersionUID = 8423079136762309124L;

	public TransactionException(String message, Throwable err) {
		super(message, err);
	}
	
	public TransactionException(String message) {
		super(message);
	}

}
