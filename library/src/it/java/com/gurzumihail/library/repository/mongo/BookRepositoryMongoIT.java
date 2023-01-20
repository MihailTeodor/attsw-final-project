package com.gurzumihail.library.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.ServerAddress;
import com.gurzumihail.library.model.Book;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BookRepositoryMongoIT {

	private static final String LIBRARY_DB_NAME = "library";
	private static final String BOOK_COLLECTION_NAME = "book";
	
	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";
	
	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private BookRepositoryMongo bookRepository;
	private MongoCollection<Document> bookCollection;
	private ClientSession session;
	
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getMappedPort(27017)));
		session = client.startSession();
		bookRepository = new BookRepositoryMongo(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME, session);
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		database.drop();
		bookCollection = database.getCollection(BOOK_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
		session.close();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(bookRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		book1.setAvailable(false);
		book1.setUserID(3);
		
		addTestBookToDatabase(book1);
		addTestBookToDatabase(book2);
		
		assertThat(bookRepository.findAll()).containsExactly(book1, book2);
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(bookRepository.findById(BOOK_ID_1)).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		addTestBookToDatabase(book1);
		addTestBookToDatabase(book2);
		
		assertThat(bookRepository.findById(BOOK_ID_2)).isEqualTo(book2);
	}
	
	@Test
	public void testSave() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		book.setAvailable(false);
		book.setUserID(3);
		
		bookRepository.save(book);
		
		assertThat(readAllBooksFromDatabase()).containsExactly(book);
	}
	
	@Test
	public void testUpdate() {
		Book bookToUpdate = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		addTestBookToDatabase(bookToUpdate);
		Book updatedBook = new Book(BOOK_ID_1, BOOK_TITLE_2, BOOK_AUTHOR_2);
		
		bookRepository.update(updatedBook);
		
		assertThat(readAllBooksFromDatabase()).hasSize(1);
		assertThat(readAllBooksFromDatabase().get(0).getId()).isEqualTo(BOOK_ID_1);
	}
	
	@Test
	public void testDeleteById() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		addTestBookToDatabase(book);
		
		bookRepository.deleteById(BOOK_ID_1);
		
		assertThat(readAllBooksFromDatabase()).isEmpty();
	}
	
	private void addTestBookToDatabase(Book book) {
		bookCollection.insertOne(
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
