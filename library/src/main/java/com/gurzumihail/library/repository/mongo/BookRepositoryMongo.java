package com.gurzumihail.library.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.repository.BookRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class BookRepositoryMongo implements BookRepository {
	
	private static final String ID = "id";
	private static final String AUTHOR = "author";
	private static final String TITLE = "title";
	private static final String AVAILABLE = "available";
	private static final String USER_ID = "userId";
	
	
	private MongoDatabase database;
	private ClientSession session;
	private MongoCollection<Document> bookCollection;
	
	public BookRepositoryMongo(MongoClient client, String databaseName, String collectionName, ClientSession session) {
		database = client.getDatabase(databaseName);
		bookCollection = database.getCollection(collectionName);
		this.session = session;
	}

	@Override
	public List<Book> findAll() {
		return StreamSupport
				.stream(bookCollection.find(session).spliterator(), false)
				.map(this::fromDocumentToBook)
				.collect(Collectors.toList());
	}

	@Override
	public Book findById(int id) {
		Document d = bookCollection.find(session, Filters.eq(ID, id)).first();
		if(d != null)
			return fromDocumentToBook(d);
		return null;
	}

	@Override
	public void save(Book book) {
		bookCollection.insertOne(session, fromBookToDocument(book));
	}

	@Override
	public void update(Book book) {
		bookCollection.replaceOne(session, Filters.eq(ID, book.getId()), fromBookToDocument(book));
	}

	@Override
	public void deleteById(int id) {
		bookCollection.deleteOne(session, Filters.eq(ID, id));
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
}