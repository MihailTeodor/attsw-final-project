package com.gurzumihail.library.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.transaction_manager.TransactionManager;
import com.gurzumihail.library.view.LibraryView;

public class LibraryController {

	private static final Logger LOGGER = LogManager.getLogger(LibraryController.class);

	private LibraryView libView;
		
	private TransactionManager transactionManager;

	public LibraryController(LibraryView libView, 
								TransactionManager transactionManager) {
		this.libView = libView;
		this.transactionManager = transactionManager;
	}

	public void allUsers() {
		try {
			List<User> users = transactionManager
					.doInTransaction((userRepository, bookRepository) -> userRepository.findAll());
			libView.showUsers(users);
			LOGGER.info("All users shown");
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());
			LOGGER.info("an exception was thrown", e);
		}
	}
	
	public void allBooks() {
		try {
			List<Book> books = transactionManager
					.doInTransaction((userRepository, bookRepository) -> bookRepository.findAll());
			libView.showBooks(books);
			LOGGER.info("All books shown");
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());
			LOGGER.info("an exception was thrown", e);
		}
	}
	
	public void allBorrowedBooks(User user) {
		try {
			List<Book> books = transactionManager
					.doInTransaction((userRepository, bookRepository) -> userRepository.getRentedBooks(user.getId()));
			libView.showBorrowedBooks(books);
			LOGGER.info("All user's borrowed books shown");
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());
			LOGGER.info("an axception was thrown", e);
		}
	}
	
	public void addUser(User userToAdd) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				User existingUser = userRepository.findById(userToAdd.getId());
				if (existingUser != null) {
					libView.showError("Already existing user with id " + userToAdd.getId());
					LOGGER.info("tryed to insert user with already existing id");
					return null;
				}
				userRepository.save(userToAdd);
				libView.userAdded(userToAdd);
				LOGGER.info("user added");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());	
			LOGGER.info("an exception was thrown", e);
		}
	}

	public void addBook(Book bookToAdd) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				Book existingBook = bookRepository.findById(bookToAdd.getId());
				if (existingBook != null) {
					libView.showError("Already existing book with id " + bookToAdd.getId());
					LOGGER.info("tryed to add book with existing id");
					return null;
				}
				bookRepository.save(bookToAdd);
				libView.bookAdded(bookToAdd);
				LOGGER.info("book added");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());
			LOGGER.info("an exception was thrown", e);
		}
	} 
	
	public void deleteUser(User userToDelete) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				User existingUser = userRepository.findById(userToDelete.getId());
				if(existingUser == null) {
					libView.showError("No existing user with id " + userToDelete.getId());
					LOGGER.info("tryed to delete unexisting user");
					return null;
				}
				if(!existingUser.getRentedBooks().isEmpty()) {
					libView.showError("Before deleting user return all borrowed books!");
					LOGGER.info("tryed to delete user that still has borrowed books");
					return null;
				}
				userRepository.deleteById(userToDelete.getId());
				libView.userDeleted(userToDelete);
				LOGGER.info("user deleted");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());
			LOGGER.info("an exception was thrown", e);
		}
	}
	 
	
	public void deleteBook(Book bookToDelete) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				Book existingBook = bookRepository.findById(bookToDelete.getId());
				if(existingBook == null) {
					libView.showError("No existing book with id " + bookToDelete.getId());
					LOGGER.info("tryed to delete unexisting book");
					return null;
				}
				if(!existingBook.isAvailable()) {
					libView.showError("Cannot cancel this book! Book borrowed by user with id: " + existingBook.getUserID());
					LOGGER.info("tryed to delete borrowed book");
					return null;
				}
				bookRepository.deleteById(bookToDelete.getId());
				libView.bookDeleted(bookToDelete);
				LOGGER.info("book deleted");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());	
			LOGGER.info("an exception was thrown", e);
		}
	}
	
	
	public void borrowBook(User user, Book book) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				if(!bookRepository.findById(book.getId()).isAvailable()) {
					libView.showError("Book not available! Borrowed by user with id " + book.getUserID());
					LOGGER.info("tryed to borrow unavailable book");
					return null;
				}
				book.setAvailable(false);
				book.setUserID(user.getId());
				bookRepository.update(book);
				user.getRentedBooks().add(book);
				userRepository.update(user);
				libView.bookBorrowed(book);
				LOGGER.info("book borrowed");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());	
			LOGGER.info("an exception was thrown", e);
		}
	}
	
	public void returnBook(User user, Book book) {
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				user.getRentedBooks().remove(book);
				userRepository.update(user);
				book.setAvailable(true);
				book.setUserID(-1);
				bookRepository.update(book);
				libView.bookReturned(book);
				LOGGER.info("book returned");
				return null;
			});
		} catch (RepositoryException e) {
			libView.showError(e.getMessage());	
			LOGGER.info("an exception was thrown", e);
		}
	} 
}