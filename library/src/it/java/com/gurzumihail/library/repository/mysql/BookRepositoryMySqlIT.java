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
import com.gurzumihail.library.repository.mysql.BookRepositoryMySql;

public class BookRepositoryMySqlIT {
	
	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";
	
	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";

	private static MySQLContainer<?> mySql;
	private Connection connection;
	
	private BookRepositoryMySql bookRepository;
	
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
		
		bookRepository = new BookRepositoryMySql(connection);
		addTestUserToDatabase(new User(-1, "test", null));
		
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
		assertThat(bookRepository.findAll()).isEmpty();
	}
	
	@Test
	public void  testFindAllWhenDatabaseIsNotEmpty() throws RepositoryException {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		book1.setAvailable(false);
		book1.setUserID(3);
		User user = new User(3, "Mihail", Collections.emptySet());
		addTestUserToDatabase(user);
		addTestBookToDatabase(book1);
		addTestBookToDatabase(book2);
		
		assertThat(bookRepository.findAll()).containsExactly(book1, book2);
	}
	
	@Test
	public void testFindAllWhenExceptionIsThrown() throws SQLException {
		connection.close();
		
		assertThatThrownBy(() -> bookRepository.findAll()).isInstanceOf(RepositoryException.class);
	}
	
	
	@Test
	public void testFindByIdNotFound() throws RepositoryException {
		assertThat(bookRepository.findById(BOOK_ID_1)).isNull();

	}
	
	@Test
	public void testFindByIdFound() throws RepositoryException {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		addTestBookToDatabase(book1);
		addTestBookToDatabase(book2);
	
		assertThat(bookRepository.findById(BOOK_ID_2)).isEqualTo(book2);
	}
	
	@Test
	public void testFindByIdWhenExceptionIsThrown() throws SQLException {

		connection.close();
		
		assertThatThrownBy(() -> bookRepository.findById(BOOK_ID_1)).isInstanceOf(RepositoryException.class);
	}
	
	@Test
	public void testSave() throws RepositoryException {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		book2.setAvailable(false);
		
		bookRepository.save(book1);
		bookRepository.save(book2);
		
		assertThat(getAllBooksFromDatabase()).containsExactly(book1, book2);
	}
	
	@Test
	public void testSaveWhenExceptionIsThrown() throws SQLException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		connection.close();
		
		assertThatThrownBy(() -> bookRepository.save(book)).isInstanceOf(RepositoryException.class);
	}
	
	@Test
	public void testUpdate() throws RepositoryException {
		Book bookToUpdate1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book bookToUpdate2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		addTestBookToDatabase(bookToUpdate1);
		addTestBookToDatabase(bookToUpdate2);
		Book updatedBook1 = new Book(BOOK_ID_1, BOOK_TITLE_2, BOOK_AUTHOR_2);
		Book updatedBook2 = new Book(BOOK_ID_2, BOOK_TITLE_1, BOOK_AUTHOR_1);
		updatedBook2.setAvailable(false);
		
		bookRepository.update(updatedBook1);
		bookRepository.update(updatedBook2);
		
		assertThat(getAllBooksFromDatabase()).containsExactly(updatedBook1, updatedBook2);
	}
	
	@Test
	public void testUpdateWhenExceptionIsThrown() throws SQLException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		connection.close();
		
		assertThatThrownBy(() -> bookRepository.update(book)).isInstanceOf(RepositoryException.class);
	}


	@Test
	public void testDeleteById() throws RepositoryException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		addTestBookToDatabase(book);
		
		bookRepository.deleteById(BOOK_ID_1);
		
		assertThat(getAllBooksFromDatabase()).isEmpty();
	}
	
	@Test
	public void testDeleteByIdWhenExceptionIsThrown() throws SQLException {
		connection.close();
		
		assertThatThrownBy(() -> bookRepository.deleteById(BOOK_ID_1)).isInstanceOf(RepositoryException.class);
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
	
	private Book fromQueryResultToBook(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		String title = result.getString("title");
		String author = result.getString("author");
		Book book = new Book(id, title, author);
		book.setAvailable(result.getBoolean("available"));
		book.setUserID(result.getInt("userId"));
		return book;
	}
	
	private List<Book> getAllBooksFromDatabase() throws RepositoryException{
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM book");
			ResultSet result = statement.executeQuery();
			
			List<Book> books = new ArrayList<>();
			while(result.next()) {
				books.add(fromQueryResultToBook(result));
			}
			return books;
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}
	
}
