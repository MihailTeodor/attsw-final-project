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
		Document d = bookCollection.find(session, Filters.eq("id", id)).first();
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
		bookCollection.replaceOne(session, Filters.eq("id", book.getId()), fromBookToDocument(book));
	}

	@Override
	public void deleteById(int id) {
		bookCollection.deleteOne(session, Filters.eq("id", id));
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
}