package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.Book;

public interface BookRepository {

	List<Book> findAll();
	
	Book findById(int id);
	
	void save(Book user);
	
	void update(Book user);
	
	void deleteById(int id);
}
