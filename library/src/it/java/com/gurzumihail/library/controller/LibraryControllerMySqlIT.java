package com.gurzumihail.library.controller;

import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.MySql.BookRepositoryMySql;
import com.gurzumihail.library.repository.MySql.UserRepositoryMySql;
import com.gurzumihail.library.transaction_manager.mySql.TransactionManagerMySql;
import com.gurzumihail.library.view.LibraryView;

public class LibraryControllerMySqlIT {

	private static final int USER_ID_1 = 1;
	private static final String USER_NAME_1 = "Mihail";
	
	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";


    public static MySQLContainer<?> mySql;
	
	@SuppressWarnings("unused")
	private AutoCloseable closeable;
	
	@Mock
	private LibraryView libView;
	
	private Connection connection;
	private UserRepositoryMySql userRepository;
	private BookRepositoryMySql bookRepository;
	private TransactionManagerMySql transactionManager;
	private LibraryController libController;
	
	@SuppressWarnings("resource")
	@BeforeClass
	public static void setupBeforeClass() {
		mySql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
				.withDatabaseName("library")
				.withInitScript("database/INIT.sql");

		mySql.start();
	}
	
	@Before
	public void setup() throws SQLException, RepositoryException {
		String rootJdbcURL = String.format("%s?user=%s&password=%s", mySql.getJdbcUrl(), mySql.getUsername(), mySql.getPassword());
		connection = DriverManager.getConnection(rootJdbcURL);
		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
		
		closeable = MockitoAnnotations.openMocks(this);
		userRepository = new UserRepositoryMySql(connection);
		bookRepository = new BookRepositoryMySql(connection);
		transactionManager = new TransactionManagerMySql(userRepository, bookRepository, connection);
		libController = new LibraryController(libView, transactionManager);
	}
	
	@After
	public void tearDown() throws SQLException {
		if(connection != null && !connection.isClosed())
			connection.close();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		mySql.close();
	}

	@Test
	public void testAllUsers() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		userRepository.save(user);
		
		libController.allUsers();
		
		verify(libView).showUsers(Arrays.asList(user));
	}

	@Test
	public void testAllBooks() throws RepositoryException {
		addDefaultUser();
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		
		libController.allBooks();
		
		verify(libView).showBooks(Arrays.asList(book));
	}
	
	
	@Test
	public void testAddUser() {
		User user = new User(USER_ID_1, USER_NAME_1, null);
		
		libController.addUser(user);
		
		verify(libView).userAdded(user);
	}
	
	
	@Test
	public void testAddBook() throws RepositoryException {
		addDefaultUser();
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		
		libController.addBook(book);
		
		verify(libView).bookAdded(book);
	}
	
	
	@Test
	public void testDeleteUser() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, null);
		userRepository.save(user);
		
		libController.deleteUser(user);
		
		verify(libView).userDeleted(user);
	}

	
	@Test
	public void testDeleteBook() throws RepositoryException {
		addDefaultUser();
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		
		libController.deleteBook(book);
		
		verify(libView).bookDeleted(book);
	}
	
	
	@Test
	public void testBorrowBook() throws RepositoryException {
		addDefaultUser();
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		Set<Book> rentedBooks= new HashSet<>();
		User user = new User(USER_ID_1, USER_NAME_1, rentedBooks);
		userRepository.save(user);
		
		libController.borrowBook(user, book);
		
		verify(libView).bookBorrowed(book);
	}
	
	@Test
	public void testReturnBook() throws RepositoryException {
		addDefaultUser();
		Book bookToReturn = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookToReturn.setAvailable(false);
		bookToReturn.setUserID(USER_ID_1);
		Set<Book> rentedBooks = new HashSet<>();
		rentedBooks.add(bookToReturn);
		User user = new User(USER_ID_1, USER_NAME_1, rentedBooks);
		userRepository.save(user);
		bookRepository.save(bookToReturn);
		
		libController.returnBook(user, bookToReturn);
		
		verify(libView).bookReturned(bookToReturn);
		
	}
	

	private void addDefaultUser() throws RepositoryException {
		User user = new User(-1, USER_NAME_1, Collections.emptySet());
		
		userRepository.save(user);
	}	
}
