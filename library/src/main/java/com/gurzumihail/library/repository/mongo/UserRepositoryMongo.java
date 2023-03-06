package com.gurzumihail.library.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.UserRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.Collections;

public class UserRepositoryMongo implements UserRepository {
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String RENTED_BOOKS = "rentedBooks";
	private static final String TITLE = "title";
	private static final String AUTHOR = "author";
	private static final String AVAILABLE = "available";
	private static final String USER_ID = "userId";
	
	private MongoDatabase database;
	private ClientSession session;
	private MongoCollection<Document> userCollection;
	
	public UserRepositoryMongo(MongoClient client, String databaseName, String collectionName, ClientSession session) {
		database = client.getDatabase(databaseName);
		userCollection = database.getCollection(collectionName);
		this.session = session;
	}


	@Override
	public List<User> findAll() {
		return StreamSupport
				.stream(userCollection.find(session).spliterator(), false)
				.map(this::fromDocumentToUser)
				.collect(Collectors.toList());
	}

	@Override
	public User findById(int id) {
		Document d = userCollection.find(session, Filters.eq(ID, id)).first();
		if(d != null)
			return fromDocumentToUser(d);
		return null;
	}

	@Override
	public void save(User user) {
		userCollection.insertOne(session, fromUserToDocument(user));
	}

	@Override
	public void update(User user) {
		userCollection.replaceOne(session, Filters.eq(ID, user.getId()), fromUserToDocument(user));
	}

	@Override
	public void deleteById(int id) {
		userCollection.deleteOne(session, Filters.eq(ID, id));
	}
	
	@Override
	public List<Book> getRentedBooks(int id) {
		Document d = userCollection.find(session, Filters.eq(ID, id)).first();
		if(d != null)
			return d.getList(RENTED_BOOKS, Document.class).stream().map(this::fromDocumentToBook).collect(Collectors.toList());
		return Collections.emptyList();
	}

	private Book fromDocumentToBook(Document d) {
		Book book = new Book(d.getInteger(ID), d.getString(TITLE), d.getString(AUTHOR));
		book.setAvailable(d.getBoolean(AVAILABLE)); 
		book.setUserID(d.getInteger(USER_ID));
		return book;
	}
	
	private Document fromBookToDocument(Book book) {
		return new Document().append(ID, book.getId()).append(TITLE, book.getTitle())
				.append(AUTHOR, book.getAuthor()).append(AVAILABLE, book.isAvailable())
				.append(USER_ID, book.getUserID());
	}


	private User fromDocumentToUser(Document d) {
		return new User(d.getInteger(ID), d.getString(NAME), d.getList(RENTED_BOOKS, 
				Document.class).stream().map(this::fromDocumentToBook).collect(Collectors.toSet()));
	}
	
	private Document fromUserToDocument(User user) {
		return new Document().append(ID, user.getId()).append(NAME, user.getName())
				.append(RENTED_BOOKS, user.getRentedBooks().stream().map(this::fromBookToDocument)
						.collect(Collectors.toList()));
	}



}
