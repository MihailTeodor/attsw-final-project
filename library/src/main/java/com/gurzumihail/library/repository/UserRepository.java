package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.User;

public interface UserRepository {

	List<User> findAll();
	
	User findById(int id);
	
	void save(User user);
	
	void update(User user);
	
	void deleteById(int id);
	
}
