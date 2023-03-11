package com.gurzumihail.library.view.swing.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.mysql.BookRepositoryMySql;
import com.gurzumihail.library.repository.mysql.UserRepositoryMySql;
import com.gurzumihail.library.transaction_manager.mysql.TransactionManagerMySql;
import com.gurzumihail.library.view.swing.LibraryViewSwing;

public class LibraryViewSwingMySqlIT extends AssertJSwingJUnitTestCase{
	
	private static final int USER_ID_1 = 1;
	private static final String USER_STR_ID_1 = "1";
	private static final String USER_NAME_1 = "Mihail";

	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_STR_ID_1 = "1";
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";

    public static MySQLContainer<?> mySql;
	private Connection connection;
	
	private UserRepositoryMySql userRepository;
	private BookRepositoryMySql bookRepository;
	private TransactionManagerMySql transactionManager;
	private LibraryController libController;
	
	private FrameFixture window;
	private LibraryViewSwing libView;
	
	@SuppressWarnings("resource")
	@BeforeClass
	public static void setupBeforeClass() {
		mySql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
				.withDatabaseName("library")
				.withInitScript("database/INIT.sql");

		mySql.start();
	}
	
	@Before
	public void onSetUp() throws SQLException, RepositoryException {
		String rootJdbcURL = String.format("%s?user=%s&password=%s", mySql.getJdbcUrl(), mySql.getUsername(), mySql.getPassword());
		connection = DriverManager.getConnection(rootJdbcURL);
		connection.prepareStatement("DELETE from book").executeUpdate();
		connection.prepareStatement("DELETE from user").executeUpdate();
		userRepository = new UserRepositoryMySql(connection);
		bookRepository = new BookRepositoryMySql(connection);
		transactionManager = new TransactionManagerMySql(userRepository, bookRepository, connection);
		
		GuiActionRunner.execute(() -> {
			libView = new LibraryViewSwing();
			libController = new LibraryController(libView, transactionManager);
			libView.setLibraryController(libController);
			return libView;
		});
		window = new FrameFixture(robot(), libView);
		window.show();
	}
	
	@After
	public void shutDown() throws SQLException {
		if(connection != null && !connection.isClosed())
			connection.close();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		mySql.close();
	}


	@Test
	@GUITest
	public void testAddUser() throws SQLException {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		window.button("addUserButton").click();
		
		assertThat(userRepository.findById(USER_ID_1)).isEqualTo(new User(USER_ID_1, USER_NAME_1, Collections.emptySet()));

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testAddBook() throws SQLException {
		User defaultUser = new User(-1, "default", null);
		userRepository.save(defaultUser);
		
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		window.button("addBookButton").click();
		
		assertThat(bookRepository.findById(BOOK_ID_1)).isEqualTo(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
	
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testDeleteUser() throws SQLException {
		User user = new User(USER_ID_1, USER_NAME_1, new HashSet<>());
		userRepository.save(user);
		
		GuiActionRunner.execute(() -> libController.allUsers());
		
		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").click();

		assertThat(userRepository.findById(USER_ID_1)).isNull();
	
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testDeleteBook() throws SQLException {
		User defaultUser = new User(-1, "default", null);
		userRepository.save(defaultUser);
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		
		GuiActionRunner.execute(() -> libController.allBooks());
		
		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").click();
		assertThat(bookRepository.findById(BOOK_ID_1)).isNull();
		
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testBorrowBook() throws SQLException {
		User defaultUser = new User(-1, "default", null);
		userRepository.save(defaultUser);
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(book);
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		userRepository.save(user);
		
		GuiActionRunner.execute(() -> {
			libController.allUsers();
			libController.allBooks();
		});
		window.list("usersList").selectItem(0);
		window.list("booksList").selectItem(0);

		window.button("borrowBookButton").click();
		
		assertThat(bookRepository.findById(BOOK_ID_1).isAvailable()).isFalse();
		assertThat(bookRepository.findById(BOOK_ID_1).getUserID()).isEqualTo(USER_ID_1);
	
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		assertThat(userRepository.findById(USER_ID_1).getRentedBooks()).containsExactly(book);
		
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testReturnBook() throws SQLException {
		User defaultUser = new User(-1, "default", null);
		userRepository.save(defaultUser);	
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		User user = new User(USER_ID_1, USER_NAME_1, Collections.singleton(book));

		userRepository.save(user);
		bookRepository.save(book);
		
		GuiActionRunner.execute(() -> {
			libController.allUsers();
			libController.allBooks();
		});
		
		window.list("usersList").selectItem(0);
		window.list("borrowedBooksList").selectItem(0);
		window.button("returnBorrowedBookButton").click();
		
		assertThat(userRepository.findById(USER_ID_1).getRentedBooks()).isEmpty();
		assertThat(bookRepository.findById(BOOK_ID_1).isAvailable()).isTrue();
		assertThat(bookRepository.findById(BOOK_ID_1).getUserID()).isEqualTo(-1);

		window.label("errorMessageLabel").requireText(" ");
	}
}
