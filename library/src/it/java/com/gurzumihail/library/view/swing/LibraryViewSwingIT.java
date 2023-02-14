package com.gurzumihail.library.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.repository.BookRepository;
import com.gurzumihail.library.repository.RepositoryException;
import com.gurzumihail.library.repository.UserRepository;
import com.gurzumihail.library.transaction_code.TransactionCode;
import com.gurzumihail.library.transaction_manager.TransactionManager;

@RunWith(GUITestRunner.class)
public class LibraryViewSwingIT extends AssertJSwingJUnitTestCase {

	private static final int USER_ID_1 = 1;
	private static final String USER_STR_ID_1 = "1";
	private static final String USER_NAME_1 = "Mihail";

	private static final int USER_ID_2 = 2;
	private static final String USER_NAME_2 = "Teodor";

	private static final int BOOK_ID_1 = 1;
	private static final String BOOK_STR_ID_1 = "1";
	private static final String BOOK_TITLE_1 = "Dune";
	private static final String BOOK_AUTHOR_1 = "Herbert";

	private static final int BOOK_ID_2 = 2;
	private static final String BOOK_TITLE_2 = "Cujo";
	private static final String BOOK_AUTHOR_2 = "King";
	
	
	private AutoCloseable closeable;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private BookRepository bookRepository;
	
	@Mock
	private TransactionManager transactionManager;
	
	@InjectMocks
	private LibraryController libController;
	
	private FrameFixture window;
	private LibraryViewSwing libView;
	
	
	@Before
	public void onSetUp() throws RepositoryException {
		closeable = MockitoAnnotations.openMocks(this);
	
		when(transactionManager.doInTransaction(any()))
		 	.thenAnswer(answer((TransactionCode<?> code) -> code.apply(userRepository, bookRepository)));
		
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
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	

	@Test
	@GUITest
	public void testAllUsers() throws RepositoryException {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
	
		GuiActionRunner.execute(() -> libController.allUsers());
		
		assertThat(window.list("usersList").contents())
			.containsExactly(user1.toString(), user2.toString());
		
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testAllBooks() throws RepositoryException {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
		
		GuiActionRunner.execute(() -> libController.allBooks());
		
		assertThat(window.list("booksList").contents()).containsExactly(book1.toString(), book2.toString());

		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testAddUserButtonSuccess() throws RepositoryException {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		when(userRepository.findById(USER_ID_1)).thenReturn(null);

		window.button("addUserButton").click();
		
		assertThat(window.list("usersList").contents()).containsExactly(new User(USER_ID_1, USER_NAME_1, Collections.emptySet()).toString());
	
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testAddBookButtonSuccess() throws RepositoryException {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(null);
		window.button("addBookButton").click();
		
		assertThat(window.list("booksList").contents()).containsExactly(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1).toString());
	
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testAddUserButtonError() throws RepositoryException {
		User existingUser = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		when(userRepository.findById(USER_ID_1)).thenReturn(existingUser);

		window.button("addUserButton").click();
		
		assertThat(window.list("usersList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Already existing user with id " + USER_STR_ID_1);
	}
	
	@Test
	@GUITest
	public void testAddBookButtonError() throws RepositoryException {
		Book existingBook = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(existingBook);

		window.button("addBookButton").click();
		
		assertThat(window.list("booksList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Already existing book with id " + BOOK_STR_ID_1);
	}	
	
	@Test
	@GUITest
	public void testDeleteUserButtonSuccess() throws RepositoryException {
		User userToDelete = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		GuiActionRunner.execute(
				() -> libController.addUser(userToDelete));
		when(userRepository.findById(USER_ID_1)).thenReturn(userToDelete);
	
		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents()).isEmpty();

		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testDeleteBookButtonSuccess() throws RepositoryException {
		Book bookToDelete = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(
				() -> libController.addBook(bookToDelete));
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(bookToDelete);
		
		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents()).isEmpty();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testUserDeleteButtonError() throws RepositoryException {
		User userToDelete = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		GuiActionRunner.execute(
				() -> libView.getUserModelList().addElement(userToDelete));
		when(userRepository.findById(USER_ID_1)).thenReturn(null);	
		
		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents())	
			.containsExactly(userToDelete.toString());
		window.label("errorMessageLabel").requireText("No existing user with id " + USER_STR_ID_1);
	}

	@Test
	@GUITest
	public void testDeleteBookButtonError() throws RepositoryException {
		Book bookToDelete = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(
				() -> libView.getBookModelList().addElement(bookToDelete));
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(null);
		
		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents())	
			.containsExactly(bookToDelete.toString());
		window.label("errorMessageLabel").requireText("No existing book with id " + BOOK_STR_ID_1);
	}

	@Test
	@GUITest
	public void testBorrowBookButtonSuccess() throws RepositoryException {
		User user = new User(USER_ID_1, USER_NAME_1, new HashSet<>());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(() -> {
			libController.addUser(user);
			libController.addBook(book);
		});
		
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(book);
		window.list("usersList").selectItem(0);
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).containsExactly(book.toString());

		window.label("errorMessageLabel").requireText(" ");
	}
	
	
	@Test
	@GUITest
	public void testBorrowBookButtonError() throws RepositoryException {
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
		when(bookRepository.findById(BOOK_ID_1)).thenReturn(book);
		
		window.list("usersList").selectItem(1);
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Book not available! Borrowed by user with id " + USER_ID_1);
	}
	
	
	@Test
	@GUITest
	public void testReturnBook() throws RepositoryException {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Set<Book> borrowedBooks = new HashSet<>();
		borrowedBooks.add(book);
		User user = new User(USER_ID_1, USER_NAME_1, borrowedBooks);
		book.setAvailable(false);
		book.setUserID(USER_ID_1);
		
		GuiActionRunner.execute(() -> {
			libController.addUser(user);
			libController.addBook(book);
		});
		when(userRepository.getRentedBooks(USER_ID_1)).thenReturn(Arrays.asList(book));
		
		window.list("usersList").selectItem(0);
		window.list("borrowedBooksList").selectItem(0);
		window.button("returnBorrowedBookButton").click();
		
		assertThat(window.list("borrowedBooksList").contents()).isEmpty();
		window.label("errorMessageLabel").requireText(" ");
	}	
}
