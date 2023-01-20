package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.Book;

public interface BookRepository {

	List<Book> findAll();
	
	Book findById(int id);
	
	void save(Book book);
	
	void update(Book book);
	
	void deleteById(int id);
}
