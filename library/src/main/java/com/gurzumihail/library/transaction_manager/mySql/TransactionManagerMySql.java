package com.gurzumihail.library.transaction_manager.mySql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.MySql.BookRepositoryMySql;
import com.gurzumihail.library.repository.MySql.UserRepositoryMySql;
import com.gurzumihail.library.transaction_code.TransactionCode;
import com.gurzumihail.library.transaction_manager.TransactionManager;

public class TransactionManagerMySql implements TransactionManager {
	
	private static final Logger LOGGER = LogManager.getLogger(TransactionManagerMySql.class);

	private UserRepositoryMySql userRepo;
	private BookRepositoryMySql bookRepo;
	private Connection connection;

	public TransactionManagerMySql(UserRepositoryMySql userRepo, BookRepositoryMySql bookRepo, Connection connection) {
		this.userRepo = userRepo;
		this.bookRepo = bookRepo;
		this.connection = connection;
	}
	@Override
	public <T> T doInTransaction(TransactionCode<T> code) throws RepositoryException {
		Savepoint savepoint = null;
		try {
			connection.setAutoCommit(false);
			savepoint = connection.setSavepoint();
			T result = code.apply(userRepo, bookRepo);
			
			connection.commit();
			connection.setAutoCommit(true);
			LOGGER.info("transaction executed successfully");
			return result;
		} catch (Exception ex) {
			try {
				connection.rollback(savepoint);
				connection.setAutoCommit(true);
				LOGGER.info("exception was thrown", ex);
			} catch (SQLException sqlEx) {
				LOGGER.info("exception was thrown during rollback", sqlEx);
				throw new RepositoryException(sqlEx.getMessage(), sqlEx);
			}
			throw new RepositoryException(ex.getMessage(), ex);
		}
	}
}
