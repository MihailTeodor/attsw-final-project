package com.gurzumihail.library.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.BookRepository;
import com.gurzumihail.library.repository.UserRepository;
import com.gurzumihail.library.transaction_code.TransactionCode;
import com.gurzumihail.library.transaction_manager.TransactionManager;
import com.gurzumihail.library.view.LibraryView;

public class LibraryControllerTest {
	
	private static final int USER_ID_1 = 1;
	private static final int USER_ID_2 = 2;
	
	private static final int BOOK_ID_1 = 1;
	private static final int BOOK_ID_2 = 2;
	
	private static final String USER_NAME_1 = "Mihail";
	private static final String USER_NAME_2 = "Teodor";
	
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_TITLE_2 = "Cujo";
	
	private static final String BOOK_AUTHOR_1 = "Herbert";
	private static final String BOOK_AUTHOR_2 = "King";
	
	private static final int DEFAULT_USER_ID = -1;

	private AutoCloseable closeable;
	
	@Mock
	private LibraryView libView;
	
	@Mock 
	private UserRepository userRepository;
	
	@Mock
	private BookRepository bookRepository;
	
	@Mock
	private TransactionManager transactionManager;
	
	@InjectMocks
	private LibraryController libController;
	
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		
		when(transactionManager.doInTransaction(any())).thenAnswer(
				answer((TransactionCode<?> code) -> code.apply(userRepository, bookRepository)));
	}
	
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	
	@Test
	public void testAllUsers() {
		List<User> users = asList(createTestUser(USER_ID_1, USER_NAME_1));
		when(userRepository.findAll()).thenReturn(users);
		
		libController.allUsers();
		
		verify(transactionManager).doInTransaction(any());		
		verify(userRepository).findAll();
		verify(libView).showUsers(users);
	}

	
	@Test
	public void testAllBooks() {
		List<Book> books = asList(createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
		when(bookRepository.findAll()).thenReturn(books);
		
		libController.allBooks();
		
		verify(transactionManager).doInTransaction(any());		
		verify(bookRepository).findAll();
		verify(libView).showBooks(books);
	}

	
	@Test
	public void testAddUserWhenUserAlreadyExists() {
		User existingUser = createTestUser(USER_ID_1, USER_NAME_1);
		User userToAdd = createTestUser(USER_ID_1, USER_NAME_2);
		when(userRepository.findById(USER_ID_1)).thenReturn(existingUser);
		
		libController.addUser(userToAdd);
		
		verify(transactionManager).doInTransaction(any());
		verify(userRepository).findById(USER_ID_1);
		verify(libView).showError("Already existing user with id 1");
		verifyNoMoreInteractions(ignoreStubs(userRepository, libView));		 
	}
	
	
	@Test
	public void testAddUserWhenUserDoesNotAlreadyExist() {
		User userToAdd = createTestUser(USER_ID_1, USER_NAME_1);
		when(userRepository.findById(USER_ID_1)).thenReturn(null);
		
		libController.addUser(userToAdd);
		
		InOrder inOrder = Mockito.inOrder(transactionManager, userRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		inOrder.verify(userRepository).findById(USER_ID_1);
		inOrder.verify(userRepository).save(userToAdd);
		inOrder.verify(libView).userAdded(userToAdd);
	}

	
	@Test
	public void testAddBookWhenBookAlreadyExists() {
		Book existingBook = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book bookToAdd = createTestBook(BOOK_ID_1, BOOK_TITLE_2, BOOK_AUTHOR_2);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(existingBook);
		
		libController.addBook(bookToAdd);
		
		verify(transactionManager).doInTransaction(any());
		verify(bookRepository).findById(BOOK_ID_1);
		verify(libView).showError("Already existing book with id 1");
		verifyNoMoreInteractions(ignoreStubs(bookRepository, libView));		 
	}
	

	@Test
	public void testAddBookWhenBookDoesNotAlreadyExist() {
		Book bookToAdd = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(null);
		
		libController.addBook(bookToAdd);
		
		InOrder inOrder = Mockito.inOrder(transactionManager, bookRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		inOrder.verify(bookRepository).findById(BOOK_ID_1);
		inOrder.verify(bookRepository).save(bookToAdd);
		inOrder.verify(libView).bookAdded(bookToAdd);
	}


	@Test
	public void testDeleteUserWhenUserDoesNotExist() {
		User userToDelete = createTestUser(USER_ID_1, USER_NAME_1);
		when(userRepository.findById(USER_ID_1))
		.thenReturn(null);
		
		libController.deleteUser(userToDelete);
		
		verify(transactionManager).doInTransaction(any());
		verify(userRepository).findById(USER_ID_1);
		verify(libView).showError("No existing user with id 1");
		verifyNoMoreInteractions(ignoreStubs(userRepository, libView));
	}

	
	@Test
	public void testDeleteUserWhenExistsAndHasBorrowedBooks() {
		User userToDelete = createTestUser(USER_ID_1, USER_NAME_1);
		Book book = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Set<Book> borrowedBooks = new HashSet<>();
		borrowedBooks.add(book);
		userToDelete.setRentedBooks(borrowedBooks);
		when(userRepository.findById(USER_ID_1)).thenReturn(userToDelete);
		
		libController.deleteUser(userToDelete);
		
		verify(transactionManager).doInTransaction(any());
		verify(userRepository).findById(USER_ID_1);
		verify(libView).showError("Before deleting user return all borrowed books!");
		verifyNoMoreInteractions(ignoreStubs(userRepository, libView));
	}
	
	
	@Test
	public void testDeleteUserWhenUserExistsAndHasReturnedAllBorrowedBooks() {
		User userToDelete = createTestUser(USER_ID_1, USER_NAME_1);
		Set<Book> rentedBooks = new HashSet<>();
		userToDelete.setRentedBooks(rentedBooks);
		when(userRepository.findById(USER_ID_1)).thenReturn(userToDelete);
		
		libController.deleteUser(userToDelete);
		
		InOrder inOrder = Mockito.inOrder(transactionManager, userRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		inOrder.verify(userRepository).findById(USER_ID_1);
		inOrder.verify(userRepository).deleteById(USER_ID_1);
		inOrder.verify(libView).userDeleted(userToDelete);
	}


	@Test
	public void testDeleteBookWhenBookDoesNotExist() {
		Book bookToDelete = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(null);
		
		libController.deleteBook(bookToDelete);
		
		verify(transactionManager).doInTransaction(any());
		verify(bookRepository).findById(BOOK_ID_1);
		verify(libView).showError("No existing book with id 1");
		verifyNoMoreInteractions(ignoreStubs(bookRepository, libView));
	}
	
	
	@Test
	public void testDeleteBookWhenBookIsBorrowed() {
		Book bookToDelete = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookToDelete.setAvailable(false);
		bookToDelete.setUserID(USER_ID_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(bookToDelete);
		
		libController.deleteBook(bookToDelete);
		
		verify(transactionManager).doInTransaction(any());
		verify(bookRepository).findById(BOOK_ID_1);
		verify(libView).showError("Cannot cancel this book! Book borrowed by user with id: 1");
		verifyNoMoreInteractions(ignoreStubs(bookRepository, libView));
	}
	
	
	@Test 
	public void testDeleteBookWhenBookExistsAndNotBorrowed() {
		Book bookToDelete = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(bookToDelete);
		
		libController.deleteBook(bookToDelete);
		
		InOrder inOrder = Mockito.inOrder(transactionManager, bookRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		inOrder.verify(bookRepository).findById(BOOK_ID_1);
		inOrder.verify(bookRepository).deleteById(BOOK_ID_1);
		inOrder.verify(libView).bookDeleted(bookToDelete);
	}

	
	@Test
	public void testBorrowBookWhenBookIsNotAvailable() {
		User user = createTestUser(USER_ID_1, USER_NAME_1); 
		Book book = createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		book.setAvailable(false);
		book.setUserID(2);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(book);
		
		libController.borrowBook(user, book);
		
		verify(transactionManager).doInTransaction(any());
		verify(bookRepository).findById(BOOK_ID_1);
		verify(libView).showError("Book not available! Borrowed by user with id 2");
		verifyNoMoreInteractions(ignoreStubs(bookRepository, libView));
	}
	
	
	@Test
	public void testBorrowBookWhenBookIsAvailable() {
		User user = spy(createTestUser(USER_ID_1, USER_NAME_1)); 
		Book book = spy(createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(book);
		
		libController.borrowBook(user, book);
		
		InOrder inOrder = inOrder(transactionManager, book, bookRepository, user, userRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		inOrder.verify(bookRepository).findById(BOOK_ID_1);
		inOrder.verify(book).setAvailable(false);
		inOrder.verify(book).setUserID(USER_ID_1);
		inOrder.verify(bookRepository).update(book);
		assertThat(user.getRentedBooks()).containsExactly(book);
		inOrder.verify(userRepository).update(user);
		inOrder.verify(libView).bookBorrowed(book);
	}
	
	
	@Test
	public void testReturnBook() {
		User user = spy(createTestUser(USER_ID_1, USER_NAME_1));
		user.setId(1);
		Book book = spy(createTestBook(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
		book.setUserID(1);
		Set<Book> rentedBooks = new HashSet<>();
		rentedBooks.add(book);
		Book bookToNotReturn = createTestBook(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		rentedBooks.add(bookToNotReturn);
		user.setRentedBooks(rentedBooks);
		
		libController.returnBook(user, book);
		
		InOrder inOrder = inOrder(transactionManager, userRepository, book, bookRepository, libView);
		inOrder.verify(transactionManager).doInTransaction(any());
		assertThat(user.getRentedBooks()).containsExactly(bookToNotReturn);
		inOrder.verify(userRepository).update(user);
		inOrder.verify(book).setAvailable(true);
		inOrder.verify(book).setUserID(DEFAULT_USER_ID);
		inOrder.verify(bookRepository).update(book);
		inOrder.verify(libView).bookReturned(book);
	}
	
	
	private User createTestUser(int id, String name) {
		User user = new User(id, name, new HashSet<>());
		return user ;
	}
	 
	private Book createTestBook(int id, String title, String author) {
		Book book = new Book(id, title, author);
		return book;
	}
	 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
