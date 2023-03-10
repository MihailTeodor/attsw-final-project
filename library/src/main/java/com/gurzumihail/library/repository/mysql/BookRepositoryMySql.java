package com.gurzumihail.library.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.repository.BookRepository;

public class BookRepositoryMySql implements BookRepository {

	private static final String FIND_ALL = "SELECT id, title, author, available, userID FROM book";

	private static final String FIND_BY_ID = "SELECT id, title, author, available, userID FROM book WHERE id=?";

	private static final String INSERT = "INSERT INTO book (id, title, author, available, userId) VALUES(?,?,?,?,?)";

	private static final String UPDATE = "UPDATE book SET title=?, author=?, available=?, userId=? WHERE id=?";

	private static final String DELETE_BY_ID = "DELETE FROM book WHERE id=?";

	private Connection connection;

	public BookRepositoryMySql(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Book> findAll() throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
				ResultSet result = statement.executeQuery();

				List<Book> books = new ArrayList<>();
				while (result.next()) {
					books.add(fromQueryResultToBook(result));
				}
				return books;
		} 
	}

	@Override
	public Book findById(int id) throws SQLException {
		Book book = null;
		try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
				statement.setInt(1, id);
				ResultSet result = statement.executeQuery();
				if (result.next())
					book = fromQueryResultToBook(result);
		} 
		return book;
	}

	@Override
	public void save(Book book) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
				statement.setInt(1, book.getId());
				statement.setString(2, book.getTitle());
				statement.setString(3, book.getAuthor());
				statement.setInt(4, book.isAvailable() ? 1 : 0);
				statement.setInt(5, book.getUserID());
				statement.executeUpdate();
		} 
	}

	@Override
	public void deleteById(int id) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {
			statement.setInt(1, id);
			statement.executeUpdate();
		} 
	}

	@Override
	public void update(Book book) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
				statement.setString(1, book.getTitle());
				statement.setString(2, book.getAuthor());
				statement.setInt(3, book.isAvailable() ? 1 : 0);
				statement.setInt(4, book.getUserID());
				statement.setInt(5, book.getId());
				statement.executeUpdate();
		} 
	}

	private Book fromQueryResultToBook(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		String title = result.getString("title");
		String author = result.getString("author");
		Book book = new Book(id, title, author);
		book.setAvailable(result.getBoolean("available"));
		book.setUserID(result.getInt("userId"));
		return book;
	}
}
