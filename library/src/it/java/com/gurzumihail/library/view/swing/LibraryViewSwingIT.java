package com.gurzumihail.library.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.mongo.TransactionManagerMongo;
import com.gurzumihail.library.view.LibraryView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;

@RunWith(GUITestRunner.class)
public class LibraryViewSwingIT extends AssertJSwingJUnitTestCase {

	private static final String LIBRARY_DB_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	private static final String BOOK_COLLECTION_NAME = "book";

	private static final int USER_ID_1 = 1;
	private static final String USER_STR_ID_1 = "1";
	private static final String USER_NAME_1 = "Mihail";

	private static final int USER_ID_2 = 2;
	private static final String USER_STR_ID_2 = "2";
	private static final String USER_NAME_2 = "Teodor";

	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_STR_ID_1 = "1";
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";

	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_STR_ID_2 = "2";
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";
	
	
	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private UserRepositoryMongo userRepository;
	private BookRepositoryMongo bookRepository;
	private ClientSession session;
	private TransactionManagerMongo transactionManager;
	private LibraryController libController;
	private FrameFixture window;
	private LibraryViewSwing libView;
	
	
	@SuppressWarnings("deprecation")
	@Before
	public void onSetUp() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(),
						mongo.getMappedPort(27017)));
		session = client.startSession();
		MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
		userRepository = new UserRepositoryMongo(client, LIBRARY_DB_NAME, USER_COLLECTION_NAME, session);
		bookRepository = new BookRepositoryMongo(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME, session);
		transactionManager = new TransactionManagerMongo(userRepository, bookRepository, session);

		database.drop();
		
		GuiActionRunner.execute(() ->{
			libView = new LibraryViewSwing();
			libController = new LibraryController(libView, transactionManager);
			libView.setLibraryController(libController);
			return libView;
		});
		window = new FrameFixture(robot(), libView);
		window.show();
	}
	
	@After
	public void shutDownServer() {
		client.close();
		session.close();
	}
	

	@Test
	@GUITest
	public void testAllUsers() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		userRepository.save(user1);
		userRepository.save(user2);
		
		GuiActionRunner.execute(() -> libController.allUsers());
		
		assertThat(window.list("usersList").contents())
			.containsExactly(user1.toString(), user2.toString());
	}
	
	@Test
	@GUITest
	public void testAllBooks() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		bookRepository.save(book1);
		bookRepository.save(book2);
		
		GuiActionRunner.execute(() -> libController.allBooks());
		
		assertThat(window.list("booksList").contents()).containsExactly(book1.toString(), book2.toString());
	}
	
	@Test
	@GUITest
	public void testAddUserButtonSuccess() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);

		window.button("addUserButton").click();
		
		assertThat(window.list("usersList").contents()).containsExactly(new User(USER_ID_1, USER_NAME_1, Collections.emptySet()).toString());
	}
	
	@Test
	@GUITest
	public void testAddBookButtonSuccess() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);

		window.button("addBookButton").click();
		
		assertThat(window.list("booksList").contents()).containsExactly(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1).toString());
	}
	
	@Test
	@GUITest
	public void testAddUserButtonError() {
		User existingUser = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		userRepository.save(existingUser);
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);

		window.button("addUserButton").click();
		
		assertThat(window.list("usersList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Already existing user with id " + USER_STR_ID_1);
	}
	
	@Test
	@GUITest
	public void testAddBookButtonError() {
		Book existingBook = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		bookRepository.save(existingBook);
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);

		window.button("addBookButton").click();
		
		assertThat(window.list("booksList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Already existing book with id " + BOOK_STR_ID_1);
	}	
	
	@Test
	@GUITest
	public void testDeleteUserButtonSuccess() {
		User userToDelete = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		GuiActionRunner.execute(
				() -> libController.addUser(userToDelete));
		
		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents()).isEmpty();
	}
	
	@Test
	@GUITest
	public void testDeleteBookButtonSuccess() {
		Book bookToDelete = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(
				() -> libController.addBook(bookToDelete));
	
		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testUserDeleteButtonError() {
		User userToDelete = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		GuiActionRunner.execute(
				() -> libView.getUserModelList().addElement(userToDelete));
	
		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents())	
			.containsExactly(userToDelete.toString());
		window.label("errorMessageLabel").requireText("No existing user with id " + USER_STR_ID_1);
	}

	@Test
	@GUITest
	public void testDeleteBookButtonError() {
		Book bookToDelete = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(
				() -> libView.getBookModelList().addElement(bookToDelete));

		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents())	
			.containsExactly(bookToDelete.toString());
		window.label("errorMessageLabel").requireText("No existing book with id " + BOOK_STR_ID_1);
	}

	@Test
	@GUITest
	public void testBorrowBookButtonSuccess() {
		User user = new User(USER_ID_1, USER_NAME_1, new HashSet<>());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(() -> {
			libController.addUser(user);
			libController.addBook(book);
		});
		
		window.list("usersList").selectItem(0);
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).containsExactly(book.toString());
	}
	
	
	@Test
	@GUITest
	public void testBorrowBookButtonError() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.singleton(book));
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		
		GuiActionRunner.execute(() -> {
			libController.addUser(user1);
			libController.addUser(user2);
			libController.addBook(book);
		});
		
		window.list("usersList").selectItem(1);
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Book not available! Borrowed by user with id " + USER_ID_1);
	}
	
	
	@Test
	@GUITest
	public void testReturnBook() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		User user = new User(USER_ID_1, USER_NAME_1, Collections.singleton(book));
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		
		GuiActionRunner.execute(() -> {
			libController.addUser(user);
			libController.addBook(book);
		});
		
		window.list("usersList").selectItem(0);
		window.list("borrowedBooksList").selectItem(0);
		window.button("returnBorrowedBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).isEmpty();
	}	
}
