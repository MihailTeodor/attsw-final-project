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

public class UserRepositoryMongo implements UserRepository {
	
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
		Document d = userCollection.find(session, Filters.eq("id", id)).first();
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
		userCollection.replaceOne(session, Filters.eq("id", user.getId()), fromUserToDocument(user));
	}

	@Override
	public void deleteById(int id) {
		userCollection.deleteOne(session, Filters.eq("id", id));
	}
	
	private Book fromDocumentToBook(Document d) {
		Book book = new Book(d.getInteger("id"), d.getString("title"), d.getString("author"));
		book.setAvailable(d.getBoolean("available")); 
		book.setUserID(d.getInteger("userId"));
		return book;
	}
	
	private Document fromBookToDocument(Book book) {
		return new Document().append("id", book.getId()).append("title", book.getTitle())
				.append("author", book.getAuthor()).append("available", book.isAvailable())
				.append("userId", book.getUserID());
	}


	private User fromDocumentToUser(Document d) {
		return new User(d.getInteger("id"), d.getString("name"), d.getList("rentedBooks", 
				Document.class).stream().map(this::fromDocumentToBook).collect(Collectors.toSet()));
	}
	
	private Document fromUserToDocument(User user) {
		return new Document().append("id", user.getId()).append("name", user.getName())
				.append("rentedBooks", user.getRentedBooks().stream().map(this::fromBookToDocument)
						.collect(Collectors.toList()));
	}

}
