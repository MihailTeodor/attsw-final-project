package com.gurzumihail.library.repository.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

public class UserRepositoryMySqlIT {
	
	private static final int USER_ID_1 = 1;
	private static final String USER_NAME_1 = "Mihail";
	
	private static final int USER_ID_2 = 2;
	private static final String USER_NAME_2 = "Teodor";
	
	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";
	

    public static MySQLContainer<?> mySql;
	private Connection connection;
	
	private UserRepositoryMySql userRepository;
	
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
		connection = DriverManager.getConnection(rootJdbcURL);
		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
		
		userRepository = new UserRepositoryMySql(connection);
		
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
	public void testFindAllWhenDatabaseIsEmpty() throws RepositoryException {
		assertThat(userRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() throws RepositoryException {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		addTestUserToDatabase(user1);
		addTestUserToDatabase(user2);
		
		assertThat(userRepository.findAll()).containsExactly(user1, user2);
	}

	@Test
	public void testFindAllWhenExceptionIsThrown() throws SQLException {

		connection.close();
		
		assertThatThrownBy(() -> userRepository.findAll()).isInstanceOf(RepositoryException.class);
	}

	@Test
	public void testFindByIdNotFound() throws RepositoryException {
		assertThat(userRepository.findById(USER_ID_1)).isNull();
	}
	
	@Test
	public void testFindByIdFound() throws RepositoryException {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		addTestUserToDatabase(user1);
		addTestUserToDatabase(user2);
		
		assertThat(userRepository.findById(USER_ID_2)).isEqualTo(user2);
	}

	@Test
	public void testFindByIdWhenExceptionIsThrown() throws SQLException {

		connection.close();
		
		assertThatThrownBy(() -> userRepository.findById(USER_ID_1)).isInstanceOf(RepositoryException.class);
	}

	@Test
	public void testSave() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		userRepository.save(user);
		
		assertThat(getAllUsersFromDatabase()).containsExactly(user);
	}
	
	@Test
	public void testSaveWhenExceptionIsThrown() throws SQLException {
		User user = new User(USER_ID_1, USER_NAME_1, null);
		connection.close();
		
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(RepositoryException.class);
	}
	
	@Test
	public void testUpdate() throws RepositoryException {
		User userToUpdate = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		addTestUserToDatabase(userToUpdate);
		User updatedUser = new User(USER_ID_1, USER_NAME_2, Collections.emptySet());
		
		userRepository.update(updatedUser);
		
		assertThat(getAllUsersFromDatabase()).containsExactly(updatedUser);
	}
	
	@Test
	public void testUpdateWhenExceptionIsThrown() throws SQLException {
		User user = new User(USER_ID_1, USER_NAME_1, null);
		connection.close();
		
		assertThatThrownBy(() -> userRepository.update(user)).isInstanceOf(RepositoryException.class);
	}

	@ Test
	public void testDeleteById() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		addTestUserToDatabase(user);
		
		userRepository.deleteById(USER_ID_1);
		
		assertThat(getAllUsersFromDatabase()).isEmpty();
	}
	
	@Test
	public void testDeleteByIdWhenExceptionIsThrown() throws SQLException {

		connection.close();
		
		assertThatThrownBy(() -> userRepository.deleteById(USER_ID_1)).isInstanceOf(RepositoryException.class);
	}

	@Test
	public void testGetRentedBooksWhenUserHasNoBooksRented() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		addTestUserToDatabase(user);
		
		assertThat(userRepository.getRentedBooks(1)).isEmpty();
	}
	
	@Test
	public void testGetRentedBooksWhenUserHasBooksRented() throws RepositoryException {
		User user = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		addTestUserToDatabase(user);
		Book rentedBook = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		rentedBook.setAvailable(false);
		rentedBook.setUserID(USER_ID_2);
		addTestBookToDatabase(rentedBook);
		
		assertThat(userRepository.getRentedBooks(USER_ID_2)).containsExactly(rentedBook);
	}

	@Test
	public void testGetRentedBooksWhenExceptionIsThrown() throws SQLException {

		connection.close();
		
		assertThatThrownBy(() -> userRepository.getRentedBooks(USER_ID_1)).isInstanceOf(RepositoryException.class);
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
