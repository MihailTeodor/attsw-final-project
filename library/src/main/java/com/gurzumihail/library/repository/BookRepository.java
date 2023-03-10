package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.Book;

public interface BookRepository {

	List<Book> findAll() throws Exception;
	
	Book findById(int id) throws Exception;
	
	void save(Book book) throws Exception;
	
	void update(Book book) throws Exception;
	
	void deleteById(int id) throws Exception;
}
