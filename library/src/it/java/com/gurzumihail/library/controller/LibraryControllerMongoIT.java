package com.gurzumihail.library.controller;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.mongo.TransactionManagerMongo;
import com.gurzumihail.library.view.LibraryView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;

public class LibraryControllerMongoIT {

	private static final String LIBRARY_DB_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	private static final String BOOK_COLLECTION_NAME = "book";

	private static final int USER_ID_1 = 1;
	private static final String USER_NAME_1 = "Mihail";
	
	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";
	

	
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	@SuppressWarnings("unused")
	private AutoCloseable closeable;
	
	@Mock
	private LibraryView libView;
	
	private MongoClient client;
	private UserRepositoryMongo userRepository;
	private BookRepositoryMongo bookRepository;
	private ClientSession session;
	private TransactionManagerMongo transactionManager;
	private LibraryController libController;
	
	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getMappedPort(27017)));
		session = client.startSession();
		closeable = MockitoAnnotations.openMocks(this);
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		userRepository = new UserRepositoryMongo(client, LIBRARY_DB_NAME, USER_COLLECTION_NAME, session);
		bookRepository = new BookRepositoryMongo(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME, session);
		transactionManager = new TransactionManagerMongo(userRepository, bookRepository, session);

		database.drop();
		libController = new LibraryController(libView, transactionManager);
		
	}
	
	@After
	public void tearDown() {
		client.close();
		session.close();
	}
	
	@Test
	public void testAllUsers() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		userRepository.save(user);
		
		libController.allUsers();
		
		verify(libView).showUsers(Arrays.asList(user));		
	}
	
	@Test
	public void testAllBooks() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		
		libController.allBooks();
		
		verify(libView).showBooks(Arrays.asList(book));
	}
	
	@Test
	public void testAddUser() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		libController.addUser(user);
		
		verify(libView).userAdded(user);
	}
	
	@Test
	public void testAddBook() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		
		libController.addBook(book);
		
		verify(libView).bookAdded(book);
	}
	
	@Test
	public void testDeleteUser() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		userRepository.save(user);
		
		libController.deleteUser(user);
		
		verify(libView).userDeleted(user);
	}
	
	@Test
	public void testDeleteBook() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		
		libController.deleteBook(book);
		
		verify(libView).bookDeleted(book);
	}
	
	@Test
	public void testBorrowBook() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		Set<Book> rentedBooks= new HashSet<>();		
		User user = new User(USER_ID_1, USER_NAME_1, rentedBooks);
		userRepository.save(user);
		
		libController.borrowBook(user, book);
		
		verify(libView).bookBorrowed(book);
	}

	@Test
	public void testReturnBook() {
		Book bookToReturn = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookToReturn.setAvailable(false);
		bookToReturn.setUserID(USER_ID_1);
		bookRepository.save(bookToReturn);
		Set<Book> rentedBooks = new HashSet<>();
		rentedBooks.add(bookToReturn);
		User user = new User(USER_ID_1, USER_NAME_1, rentedBooks);
		userRepository.save(user);
		
		libController.returnBook(user, bookToReturn);
		
		verify(libView).bookReturned(bookToReturn);
	}
}
