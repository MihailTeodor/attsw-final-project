package com.gurzumihail.library.transaction_code;


import com.gurzumihail.library.repository.BookRepository;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.UserRepository;

@FunctionalInterface
public interface TransactionCode<T> {

	T apply(UserRepository userRepository, BookRepository bookRepository) throws RepositoryException;
}
