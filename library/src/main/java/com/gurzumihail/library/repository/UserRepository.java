package com.gurzumihail.library.repository;

import java.util.List;

import com.gurzumihail.library.model.User;

public interface UserRepository {

	List<User> findAll() throws RepositoryException;
	
	User findById(int id) throws RepositoryException;
	
	void save(User user) throws RepositoryException;
	
	void update(User user) throws RepositoryException;
	
	void deleteById(int id) throws RepositoryException;
	
}
