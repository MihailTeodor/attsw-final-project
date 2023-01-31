package com.gurzumihail.library.transaction_manager.mySql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.MySql.BookRepositoryMySql;
import com.gurzumihail.library.repository.MySql.UserRepositoryMySql;

public class TransactionManagerMySqlIT {
	
	private static final int USER_ID_1 = 1;
	private static final String USER_NAME_1 = "Mihail";
	
	private static final int USER_ID_2 = 2;
	private static final String USER_NAME_2 = "Teodor";
	
	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";

	
	private static MySQLContainer<?> mySql;
	
	
	private Connection connection;
	
	private UserRepositoryMySql userRepository;
	private BookRepositoryMySql bookRepository;
	private TransactionManagerMySql transactionManager;
	
	@SuppressWarnings("resource")
	@BeforeClass
	public static void setupBeforeClass() {
		mySql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
				.withDatabaseName("library")
				.withUsername("root")
				.withPassword("password")
				.withInitScript("database/INIT.sql");
		mySql.start();
	}
	
	@Before
	public void setup() throws SQLException, RepositoryException {
		String rootJdbcURL = String.format("%s?user=%s&password=%s", mySql.getJdbcUrl(), mySql.getUsername(), mySql.getPassword());
		connection = spy(DriverManager.getConnection(rootJdbcURL));
		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
		
		userRepository = new UserRepositoryMySql(connection);
		bookRepository = new BookRepositoryMySql(connection);
		transactionManager = new TransactionManagerMySql(userRepository, bookRepository, connection);
	}
	
	@After
	public void tearDown() throws SQLException {
		if(connection != null && !connection.isClosed())
			connection.close();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		mySql.close();
	}


	@Test
	public void testDoInTransaction() throws RepositoryException, SQLException {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		Boolean result = transactionManager.doInTransaction((userRepository, bookRepository) -> {
			addTestUserToDatabase(user);
			return true;
		});
		assertThat(result).isTrue();
		assertThat(getAllUsersFromDatabase()).containsExactly(user);
		verify(connection).commit();
		verify(connection).setAutoCommit(true);
	}

	@Test
	public void testDoInTransactionWhenMySqlExceptionOccoursThenRepositoryExceptionIsThrown() throws SQLException {
		Book book = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		
		assertThatThrownBy(() -> transactionManager.doInTransaction((userRepository, bookRepository) -> {
			addTestBookToDatabase(book);
			return null;
		})).isInstanceOf(RepositoryException.class);
		verify(connection).setAutoCommit(true);
	}
	
	@Test
	public void testDoInTransactionWhenExceptionIsThrownDuringRollback() throws SQLException {
		Book book = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		doThrow(new SQLException("Exception during rollback!")).when(connection).rollback(any());
		
			
		assertThatThrownBy(() -> transactionManager.doInTransaction((userRepository, bookRepository) -> {
			addTestBookToDatabase(book);
			return null;
		})).isInstanceOf(RepositoryException.class).hasMessage("Exception during rollback!");
	}
	
	@Test
	public void testDoInTransactionWhenExceptionIsThrownDoNotCommit() throws RepositoryException {
		Book book = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		User user = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		
		try {
			transactionManager.doInTransaction((userRepository, bookRepository) -> {
				addTestUserToDatabase(user);
				addTestBookToDatabase(book);
				return null;
			});
		} catch (RepositoryException e) {
		}
		assertThat(getAllUsersFromDatabase()).isEmpty();
	}
	
	
	
	
	private List<User> getAllUsersFromDatabase() throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM user");
			ResultSet result = statement.executeQuery();
			
			List<User> users = new ArrayList<>();
			while(result.next()) {
				users.add(fromQueryResultToUser(result));
			}
			return users;
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
		
	}

	
	private void addTestUserToDatabase(User user) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO user (id, name) VALUES(?,?)");
			statement.setInt(1,  user.getId());
			statement.setString(2, user.getName());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}


	private User fromQueryResultToUser(ResultSet result) throws SQLException, RepositoryException{
			int id = result.getInt("id");
			String name = result.getString("name");
			return new User(id, name, Collections.emptySet());
	}
	

	private void addTestBookToDatabase(Book book) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO book (id, title, author, available, userId) VALUES(?,?,?,?,?)");
			statement.setInt(1, book.getId());
			statement.setString(2, book.getTitle());
			statement.setString(3, book.getAuthor());
			statement.setInt(4, book.isAvailable()? 1 : 0);
			statement.setInt(5,	book.getUserID());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}	
}


