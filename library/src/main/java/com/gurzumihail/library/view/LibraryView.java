package com.gurzumihail.library.view;

import java.util.List;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;

public interface LibraryView {
	
	void showUsers(List<User> users);
	
	void showBooks(List<Book> books);
	
	void userAdded(User user);
	
	void userUpdated(User user);

	void userDeleted(User user);
	
	void bookAdded(Book book);
	
	void bookUpdated(Book book);
	
	void bookDeleted(Book book);
	
	void bookBorrowed(Book book);
	
	void bookReturned(Book book);
	
	void showError(String message);
}
