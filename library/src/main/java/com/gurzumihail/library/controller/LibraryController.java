package com.gurzumihail.library.controller;

import java.util.List;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.BookRepository;
import com.gurzumihail.library.repository.UserRepository;
import com.gurzumihail.library.transaction_manager.TransactionManager;
import com.gurzumihail.library.view.LibraryView;

public class LibraryController {

	private LibraryView libView;
		
	private TransactionManager transactionManager;

	public LibraryController(LibraryView libView, 
								TransactionManager transactionManager) {
		this.libView = libView;
		this.transactionManager = transactionManager;
	}

	public void allUsers() {
		List<User> users = transactionManager
				.doInTransaction((userRepository, bookRepository) -> userRepository.findAll());
		libView.showUsers(users);
	}
	
	public void allBooks() {
		List<Book> books = transactionManager
				.doInTransaction((userRepository, bookRepository) -> bookRepository.findAll());
		libView.showBooks(books);
	}
	
	public void addUser(User userToAdd) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			User existingUser = userRepository.findById(userToAdd.getId());
			if (existingUser != null) {
				libView.showError("Already existing user with id " + userToAdd.getId());
				return null;
			}
			userRepository.save(userToAdd);
			libView.userAdded(userToAdd);
			return null;
		});
	}

	public void addBook(Book bookToAdd) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			Book existingBook = bookRepository.findById(bookToAdd.getId());
			if (existingBook != null) {
				libView.showError("Already existing book with id " + bookToAdd.getId());
				return null;
			}
			bookRepository.save(bookToAdd);
			libView.bookAdded(bookToAdd);
			return null;
		});
	} 
	
	public void deleteUser(User userToDelete) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			User existingUser = userRepository.findById(userToDelete.getId());
			if(existingUser == null) {
				libView.showError("No existing user with id " + userToDelete.getId());
				return null;
			}
			if(!existingUser.getRentedBooks().isEmpty()) {
				libView.showError("Before deleting user return all borrowed books!");
				return null;
			}
			userRepository.deleteById(userToDelete.getId());
			libView.userDeleted(userToDelete);
			return null;
		});
	}
	 
	
	public void deleteBook(Book bookToDelete) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			Book existingBook = bookRepository.findById(bookToDelete.getId());
			if(existingBook == null) {
				libView.showError("No existing book with id " + bookToDelete.getId());
				return null;
			}
			if(!existingBook.isAvailable()) {
				libView.showError("Cannot cancel this book! Book borrowed by user with id: " + existingBook.getUserID());
				return null;
			}
			bookRepository.deleteById(bookToDelete.getId());
			libView.bookDeleted(bookToDelete);
			return null;
		});
	}
	
	
	public void borrowBook(User user, Book book) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			if(!bookRepository.findById(book.getId()).isAvailable()) {
				libView.showError("Book not available! Borrowed by user with id " + book.getUserID());
				return null;
			}
			book.setAvailable(false);
			book.setUserID(user.getId());
			bookRepository.update(book);
			user.getRentedBooks().add(book);
			userRepository.update(user);
			libView.bookBorrowed(book);
			return null;
		});
	}
	
	public void returnBook(User user, Book book) {
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			user.getRentedBooks().remove(book);
			userRepository.update(user);
			book.setAvailable(true);
			book.setUserID(-1);
			bookRepository.update(book);
			libView.bookReturned(book);
			return null;
		});
	} 
	
}