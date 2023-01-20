package com.gurzumihail.library.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class UserRepositoryMongoIT {
	
	private static final String LIBRARY_DB_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	
	private static final int USER_ID_1 = 1;
	private static final String USER_NAME_1 = "Mihail";
	
	private static final int USER_ID_2 = 2;
	private static final String USER_NAME_2 = "Teodor";
	
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
	private UserRepositoryMongo userRepository;
	private MongoCollection<Document> userCollection;
	private ClientSession session;
	
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getMappedPort(27017)));
		session = client.startSession();
		userRepository = new UserRepositoryMongo(client, LIBRARY_DB_NAME, USER_COLLECTION_NAME, session);
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		database.drop();
		userCollection = database.getCollection(USER_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
		session.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(userRepository.findAll()).isEmpty();
	}


	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());				
		addTestUserToDatabase(user1);
		addTestUserToDatabase(user2);
		
		assertThat(userRepository.findAll()).containsExactly(user1, user2);
	}
		
	@Test
	public void testFindByIdNotFound() {
		assertThat(userRepository.findById(USER_ID_1)).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		book.setAvailable(false);
		book.setUserID(USER_ID_2);
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.singleton(book));				
		addTestUserToDatabase(user1);
		addTestUserToDatabase(user2);
		
		assertThat(userRepository.findById(USER_ID_2)).isEqualTo(user2);
		assertThat(userRepository.findById(USER_ID_2).getRentedBooks()).containsExactly(book);
	}

	@Test
	public void testSave() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		userRepository.save(user);
		
		assertThat(readAllUsersFromDatabase()).containsExactly(user);

	}
	
	@Test
	public void testUpdate() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		User userToUpdate = new User(USER_ID_1, USER_NAME_1, Collections.singleton(book1));
		addTestUserToDatabase(userToUpdate);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		User updatedUser = new User(USER_ID_1, USER_NAME_2, Collections.singleton(book2));
		
		userRepository.update(updatedUser);
		
		assertThat(readAllUsersFromDatabase()).hasSize(1);
		assertThat(readAllUsersFromDatabase().get(0).getId()).isEqualTo(USER_ID_1);
		assertThat(readAllUsersFromDatabase().get(0).getName()).isEqualTo(USER_NAME_2);
		assertThat(readAllUsersFromDatabase().get(0).getRentedBooks()).containsExactly(book2);
	}

	
	@Test
	public void testDeleteById() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		addTestUserToDatabase(user);
		
		userRepository.deleteById(USER_ID_1);
		
		assertThat(readAllUsersFromDatabase()).isEmpty();
	}

	private Document fromBookToDocument(Book book) {
		return new Document().append("id", book.getId()).append("title", book.getTitle())
				.append("author", book.getAuthor()).append("available", book.isAvailable())
				.append("userId", book.getUserID());
	}
	
	private Book fromDocumentToBook(Document d) {
		Book book = new Book(d.getInteger("id"), d.getString("title"), d.getString("author"));
		book.setAvailable(d.getBoolean("available")); 
		book.setUserID(d.getInteger("userId"));
		return book;
	}

	private User fromDocumentToUser(Document d) {
		return new User(d.getInteger("id"), d.getString("name"), d.getList("rentedBooks", 
				Document.class).stream().map(this::fromDocumentToBook).collect(Collectors.toSet()));
	}
	
	private void addTestUserToDatabase(User user) {
		userCollection.insertOne(
				new Document()
				.append("id", user.getId())
				.append("name", user.getName())
				.append("rentedBooks", user.getRentedBooks().stream().map(this::fromBookToDocument)
						.collect(Collectors.toList())));
	}

	private List<User> readAllUsersFromDatabase() {
		return StreamSupport
				.stream(userCollection.find().spliterator(), false)
				.map(this::fromDocumentToUser)
				.collect(Collectors.toList());
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
