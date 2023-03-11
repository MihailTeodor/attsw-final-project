package com.gurzumihail.library.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.UserRepository;

public class UserRepositoryMySql implements UserRepository {

	private static final String FIND_ALL = "SELECT id, name FROM user";

	private static final String FIND_BY_ID = "SELECT id, name FROM user WHERE id=?";

	private static final String INSERT = "INSERT INTO user (id, name) VALUES(?,?)";

	private static final String UPDATE = "UPDATE user SET name=? WHERE id=?";

	private static final String DELETE_BY_ID = "DELETE FROM user WHERE id=?";

	private static final String FIND_BY_USER_ID = "SELECT id, title, author, available, userID FROM book WHERE userID=?";

	private Connection connection;

	public UserRepositoryMySql(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<User> findAll() throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
				ResultSet result = statement.executeQuery();

				List<User> users = new ArrayList<>();
				while (result.next()) {
					users.add(fromQueryResultToUser(result));
				}
				return users;
		} 
	}

	@Override
	public User findById(int id) throws SQLException {
		User user = null;	
		try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
				statement.setInt(1, id);
				ResultSet result = statement.executeQuery();
				if (result.next())
					user = fromQueryResultToUser(result);
		} 
		return user;
	}

	@Override
	public void save(User user) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
				statement.setInt(1, user.getId());
				statement.setString(2, user.getName());
				statement.executeUpdate();
		} 
	}

	@Override
	public void update(User user) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
				statement.setString(1, user.getName());
				statement.setInt(2, user.getId());
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
	public List<Book> getRentedBooks(int id) throws SQLException {
			try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)) {
				statement.setInt(1, id);
				ResultSet result = statement.executeQuery();
				List<Book> books = new ArrayList<>();
				while (result.next())
					books.add(fromQueryResultToBook(result));
				return books;
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

	private User fromQueryResultToUser(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		String name = result.getString("name");
		Set<Book> booksRented = new HashSet<>(getRentedBooks(id));
		return new User(id, name, booksRented);
	}
}
