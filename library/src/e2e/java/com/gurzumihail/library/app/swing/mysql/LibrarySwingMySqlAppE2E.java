package com.gurzumihail.library.app.swing.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.RepositoryException;

@RunWith(GUITestRunner.class)
public class LibrarySwingMySqlAppE2E extends AssertJSwingJUnitTestCase {

	private static final String MY_SQL_HOST = "localhost";
	private static final int MY_SQL_PORT = 3306;
	private static final String DATABASE_NAME = "library";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "password";
	
	private static final String INSERT_USER = "INSERT INTO user (id, name) VALUES(?,?)";
	private static final String INSERT_BOOK = "INSERT INTO book (id, title, author, available, userId) VALUES(?,?,?,?,?)";
	private static final String DELETE_USER_BY_ID = "DELETE FROM user WHERE id=?";
	private static final String DELETE_BOOK_BY_ID = "DELETE FROM book WHERE id=?";

	
	private static final int DEFAULT_USER_ID = -1;
	private static final String DEFAULT_USER_NAME = "default-user";
	
	private static final int USER_FIXTURE_1_ID = 11;
	private static final String USER_FIXTURE_1_STRING_ID = "11";
	private static final String USER_FIXTURE_1_NAME = "user-test1";
	
	private static final int USER_FIXTURE_2_ID = 23;
	private static final String USER_FIXTURE_2_STRING_ID = "23";
	private static final String USER_FIXTURE_2_NAME = "user-test2";
	
	private static final int BOOK_FIXTURE_1_ID = 11;
	private static final String BOOK_FIXTURE_1_STRING_ID = "11";
	private static final String BOOK_FIXTURE_1_TITLE = "cujo";
	private static final String BOOK_FIXTURE_1_AUTHOR = "king";
	
	private static final int BOOK_FIXTURE_2_ID = 23;
	private static final String BOOK_FIXTURE_2_STRING_ID = "23";
	private static final String BOOK_FIXTURE_2_TITLE = "dune";
	private static final String BOOK_FIXTURE_2_AUTHOR = "herbert";
	
	private static final String USER_STR_ID_1 = "1";
	private static final String USER_NAME_1 = "Mihail";

	private static final String BOOK_STR_ID_1 = "1";
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";


	private Connection connection;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		String jdbcURL = String.format("jdbc:mysql://%s:%s/%s", MY_SQL_HOST, MY_SQL_PORT, DATABASE_NAME);
		connection = DriverManager.getConnection(jdbcURL, USERNAME, PASSWORD);

		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
	
		addTestUserToDatabase(new User(DEFAULT_USER_ID, DEFAULT_USER_NAME, Collections.emptySet()));
		addTestUserToDatabase(new User(USER_FIXTURE_1_ID, USER_FIXTURE_1_NAME, Collections.emptySet()));
		addTestUserToDatabase(new User(USER_FIXTURE_2_ID, USER_FIXTURE_2_NAME, Collections.emptySet()));

		addTestBookToDatabase(new Book(BOOK_FIXTURE_1_ID, BOOK_FIXTURE_1_TITLE, BOOK_FIXTURE_1_AUTHOR));
		addTestBookToDatabase(new Book(BOOK_FIXTURE_2_ID, BOOK_FIXTURE_2_TITLE, BOOK_FIXTURE_2_AUTHOR));

		 application("com.gurzumihail.library.app.swing.mysql.LibrarySwingMySqlApp")
		 	.withArgs(
		 			"--mysql-host=" + MY_SQL_HOST,
		 			"--mysql-port=" + MY_SQL_PORT,
		 			"--db-name=" + DATABASE_NAME,
		 			"--db-user=" + USERNAME,
		 			"--db-password=" + PASSWORD
		 	)
		 	.start();

		 window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			 @Override
			 protected boolean isMatching(JFrame frame) {
				 return "Library View".equals(frame.getTitle()) && frame.isShowing();
			 }
		}).using(robot());

	}
	
	@Override
	protected void onTearDown() throws SQLException {
		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
		connection.close();
	}


	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("usersList").contents())
			.anySatisfy(e -> assertThat(e)
					.contains(USER_FIXTURE_1_STRING_ID, USER_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e)
					.contains(USER_FIXTURE_2_STRING_ID, USER_FIXTURE_2_NAME));
		
		assertThat(window.list("booksList").contents())
		.anySatisfy(e -> assertThat(e)
				.contains(BOOK_FIXTURE_1_STRING_ID, BOOK_FIXTURE_1_TITLE, BOOK_FIXTURE_1_AUTHOR))
		.anySatisfy(e -> assertThat(e)
				.contains(BOOK_FIXTURE_2_STRING_ID, BOOK_FIXTURE_2_TITLE, BOOK_FIXTURE_2_AUTHOR));
	}
	
	@Test
	@GUITest
	public void testAddUserButtonSuccess() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		window.button("addUserButton").click();
		
		assertThat(window.list("usersList").contents())
			.anySatisfy(e -> assertThat(e).contains(USER_STR_ID_1, USER_NAME_1));
	}
	
	@Test
	@GUITest
	public void testAddBookButtonSuccess() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		window.button("addBookButton").click();
		
		assertThat(window.list("booksList").contents())
			.anySatisfy(e -> assertThat(e).contains(BOOK_STR_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
	}
	
	@Test
	@GUITest
	public void testBorrowBook() {
		window.list("usersList")
		.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		window.list("booksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("borrowBookButton").click();

		assertThat(window.list("borrowedBooksList").contents())
			.anySatisfy(e -> assertThat(e).contains(BOOK_FIXTURE_1_TITLE, USER_FIXTURE_1_STRING_ID));
	}
	
	@Test
	@GUITest
	public void testReturnBook() {
		window.list("usersList")
		.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		window.list("booksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		window.button("borrowBookButton").click();
		window.list("borrowedBooksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("returnBorrowedBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents())
		.noneMatch(e -> e.contains(BOOK_FIXTURE_1_TITLE));
		
	}
	
	@Test
	@GUITest
	public void testDeleteUser() {
		window.list("usersList")
			.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents())
			.noneMatch(e -> e.contains(USER_FIXTURE_1_NAME));
	}
	
	@Test
	@GUITest
	public void testDeleteBook() {
		window.list("booksList")
			.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents())
			.noneMatch(e -> e.contains(BOOK_FIXTURE_1_TITLE));
	}

	private void addTestUserToDatabase(User user) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement(INSERT_USER);
			statement.setInt(1,  user.getId());
			statement.setString(2, user.getName());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}
	
	private void addTestBookToDatabase(Book book) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement(INSERT_BOOK);
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

	private void removeTestUserFromDatabase(int id) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement(DELETE_USER_BY_ID);
			statement.setInt(1,  id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	private void removeTestBookFromDatabase(int id) throws RepositoryException {
		try {
			PreparedStatement statement = connection.prepareStatement(DELETE_BOOK_BY_ID);
			statement.setInt(1,  id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}
}
