package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.Book;

public interface BookRepository {

	List<Book> findAll() throws RepositoryException;
	
	Book findById(int id) throws RepositoryException;
	
	void save(Book book) throws RepositoryException;
	
	void update(Book book) throws RepositoryException;
	
	void deleteById(int id) throws RepositoryException;
}
