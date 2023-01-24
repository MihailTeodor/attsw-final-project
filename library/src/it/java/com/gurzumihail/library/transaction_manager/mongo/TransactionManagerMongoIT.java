package com.gurzumihail.library.transaction_manager.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.TransactionException;
import com.gurzumihail.library.view.LibraryView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TransactionManagerMongoIT {

	private static final String LIBRARY_DB_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	private static final String BOOK_COLLECTION_NAME = "book";

	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	
	@Mock
	private LibraryView libView;
	
	private MongoClient client;
	private UserRepositoryMongo userRepository;
	private BookRepositoryMongo bookRepository;
	private MongoCollection<Document> bookCollection;
	private ClientSession session;
	private TransactionManagerMongo transactionManager;
	
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getMappedPort(27017)));
		session = client.startSession();
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		bookCollection = database.getCollection(BOOK_COLLECTION_NAME);
		userRepository = new UserRepositoryMongo(client, LIBRARY_DB_NAME, USER_COLLECTION_NAME, session);
		bookRepository = new BookRepositoryMongo(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME, session);
		transactionManager = new TransactionManagerMongo(userRepository, bookRepository, session);

		database.drop();
		
	}
	
	@After
	public void tearDown() {
		client.close();
		session.close();
	}

	@Test
	public void testDoInTransaction() throws TransactionException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		
		transactionManager.doInTransaction((userRepository, bookRepository) -> {
			addTestBookToDatabase(book);
			return null;
		});

		assertThat(readAllBooksFromDatabase()).containsExactly(book);
	}
	
	@Test
	public void testDoInTransactionWhenTransactionExceptionIsThrown() throws TransactionException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);

		assertThatThrownBy(() -> transactionManager.doInTransaction((userRepository, bookRepository) -> {
			addTestBookToDatabase(book);
			throw new RuntimeException("Exception occoured!");
		})).isInstanceOf(TransactionException.class).hasMessage("Exception occoured!");
		assertThat(session.hasActiveTransaction()).isFalse();
		assertThat(readAllBooksFromDatabase()).isEmpty();
	}

	
	private void addTestBookToDatabase(Book book) {
		bookCollection.insertOne(session,
				new Document()
					.append("id", book.getId())
					.append("title", book.getTitle())
					.append("author", book.getAuthor())
					.append("available", book.isAvailable())
					.append("userId", book.getUserID()));
	}

	private Book fromDocumentToBook(Document d) {
		Book book = new Book(d.getInteger("id"), d.getString("title"), d.getString("author"));
		book.setAvailable(d.getBoolean("available"));
		book.setUserID(d.getInteger("userId"));
		return book; 
	}
	
	private List<Book> readAllBooksFromDatabase() {
		return StreamSupport
				.stream(bookCollection.find().spliterator(), false)
				.map(this::fromDocumentToBook)
				.collect(Collectors.toList());
	}
}
