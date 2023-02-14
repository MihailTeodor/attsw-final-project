package com.gurzumihail.library.view.swing.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.mongo.TransactionManagerMongo;
import com.gurzumihail.library.view.swing.LibraryViewSwing;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;

public class LibraryViewSwingMongoIT extends AssertJSwingJUnitTestCase{

	private static final String LIBRARY_DB_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	private static final String BOOK_COLLECTION_NAME = "book";

	private static final int USER_ID_1 = 1;
	private static final String USER_STR_ID_1 = "1";
	private static final String USER_NAME_1 = "Mihail";

	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_STR_ID_1 = "1";
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";

	@ClassRule
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:6.0.3");
	
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
		userRepository = new UserRepositoryMongo(client, LIBRARY_DB_NAME, USER_COLLECTION_NAME, session);
		bookRepository = new BookRepositoryMongo(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME, session);
		transactionManager = new TransactionManagerMongo(userRepository, bookRepository, session);
		for(User user : userRepository.findAll()) {
			userRepository.deleteById(user.getId());
		}
		for(Book book : bookRepository.findAll()) {
			bookRepository.deleteById(book.getId());
		}
		window = new FrameFixture(robot(), GuiActionRunner.execute(() ->{
			libView = new LibraryViewSwing();
			libController = new LibraryController(libView, transactionManager);
			libView.setLibraryController(libController);
			return libView;
		}));
		window.show();
	}
	
	@After
	public void shutDownServer() {
		client.close();
		session.close();
	}

	@Test
	@GUITest
	public void testAddUser() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		window.button("addUserButton").click();
		
		assertThat(userRepository.findById(USER_ID_1)).isEqualTo(new User(USER_ID_1, USER_NAME_1, Collections.emptySet()));
		
		window.label("errorMessageLabel").requireText(" ");
	}


	@Test
	@GUITest
	public void testAddBook() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		window.button("addBookButton").click();
		
		assertThat(bookRepository.findById(BOOK_ID_1)).isEqualTo(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
		
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testDeleteUser() {
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
	public void testDeleteBook() {
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
	public void testBorrowBook() {
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
	public void testReturnBook() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		User user = new User(USER_ID_1, USER_NAME_1, Collections.singleton(book));

		bookRepository.save(book);
		userRepository.save(user);
		
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
