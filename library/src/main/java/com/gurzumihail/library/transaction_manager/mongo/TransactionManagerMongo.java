package com.gurzumihail.library.transaction_manager.mongo;

import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_code.TransactionCode;
import com.gurzumihail.library.transaction_manager.TransactionManager;
import com.mongodb.client.ClientSession;

public class TransactionManagerMongo implements TransactionManager {

	private UserRepositoryMongo userRepo;
	private BookRepositoryMongo bookRepo;
	private ClientSession session;

	public TransactionManagerMongo(UserRepositoryMongo userRepo, BookRepositoryMongo bookRepo, ClientSession session) {
		this.userRepo = userRepo;
		this.bookRepo = bookRepo;
		this.session = session;
	}
	@Override
	public <T> T doInTransaction(TransactionCode<T> code) throws RepositoryException {

		try {
			session.startTransaction();
			T result = code.apply(userRepo, bookRepo);
			session.commitTransaction();
			
			return result;
		} catch (Exception e) {
			session.abortTransaction();	
			throw new RepositoryException(e.getMessage(), e);
		}
	}
}
	