package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;

public interface UserRepository {

	List<User> findAll() throws Exception;
	
	User findById(int id) throws Exception;
	
	void save(User user) throws Exception;
	
	void update(User user) throws Exception;
	
	void deleteById(int id) throws Exception;
	
	List<Book> getRentedBooks(int id) throws Exception;
	
}
