package com.gurzumihail.library.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;

@RunWith(GUITestRunner.class)
public class LibraryViewSwingTest extends AssertJSwingJUnitTestCase {

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

	private FrameFixture window;

	private LibraryViewSwing libraryView;

	@Mock
	private LibraryController libController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			libraryView = new LibraryViewSwing();
			libraryView.setLibraryController(libController);
			return libraryView;
		});
		window = new FrameFixture(robot(), libraryView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialState() {
		window.label("userLabel").requireText("Insert id and name of the new user");
		window.label("idUserLabel").requireText("id");
		window.textBox("idUserTextField").requireText("").requireEnabled();
		window.label("nameUserLabel").requireText("name");
		window.textBox("nameUserTextField").requireText("").requireEnabled();
		window.button("addUserButton").requireText("Add User").requireDisabled();
		window.list("usersList").requireItemCount(0).requireEnabled();
		window.label("usersListLabel");
		window.button("userDeleteButton").requireDisabled();
		window.list("borrowedBooksList");
		window.list("booksList");
		window.label("bookLabel");
		window.label("idBookLabel");
		window.label("titleBookLabel");
		window.label("authorBookLabel");
		window.textBox("idBookTextField").requireEnabled();
		window.textBox("titleBookTextField").requireEnabled();
		window.textBox("authorBookTextField").requireEnabled();
		window.button("addBookButton").requireDisabled();
		window.label("bookListLabel");
		window.label("borrowedBooksListLabel");
		window.button("returnBorrowedBookButton").requireDisabled();
		window.button("deleteBookButton").requireDisabled();
		window.button("borrowBookButton").requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenIdAndNameAreNotEmptyThenAddUserButtonShouldBeEnabled() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		
		window.button("addUserButton").requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEitherIdOrNameAreBlankThenAddUserButtonShouldBeDisabled() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(" ");
		window.button("addUserButton").requireDisabled();

		window.textBox("idUserTextField").setText("");
		window.textBox("nameUserTextField").setText("");

		window.textBox("idUserTextField").enterText(" ");
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		window.button("addUserButton").requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenIdTitleAndAuthorAreAllNotEmptyThenAddBookShouldBeEnabled() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		
		window.button("addBookButton").requireEnabled();
	}
	
	@Test
	@GUITest
	public void testWhenEitherIdOrTitleOrAuthorAreBlankThenAddBookButtonShouldBeDisabled() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(" ");
		window.textBox("authorBookTextField").enterText(" ");

		window.button("addBookButton").requireDisabled();

		window.textBox("idBookTextField").setText("");
		window.textBox("titleBookTextField").setText("");
		window.textBox("authorBookTextField").setText("");

		window.textBox("idBookTextField").enterText(" ");
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(" ");

		window.button("addBookButton").requireDisabled();	

		window.textBox("idBookTextField").setText("");
		window.textBox("titleBookTextField").setText("");
		window.textBox("authorBookTextField").setText("");

		window.textBox("idBookTextField").enterText(" ");
		window.textBox("titleBookTextField").enterText(" ");
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);

		window.button("addBookButton").requireDisabled();
	}
	
	@Test
	@GUITest
	public void testDeleteUserButtonShouldBeEnabledOnlyWhenUserIsSelected() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		GuiActionRunner.execute(() -> libraryView.getUserModelList().addElement(user));

		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").requireEnabled();

		window.list("usersList").clearSelection();
		window.button("userDeleteButton").requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteBookButtonShouldBeEnabledOnlyWhenBookIsSelected() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(() -> libraryView.getBookModelList().addElement(book));

		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").requireEnabled();

		window.list("booksList").clearSelection();
		window.button("deleteBookButton").requireDisabled();
	}
	
	@Test
	@GUITest
	public void testBorrowBookButtonShouldBeEnabledOnlyWhenUserAndBookIsSelected() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(() -> {
			libraryView.getUserModelList().addElement(user);
			libraryView.getBookModelList().addElement(book);
		});
		
		window.list("usersList").selectItem(0);
		window.button("borrowBookButton").requireDisabled();
		
		window.list("usersList").clearSelection();
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").requireDisabled();
		
		window.list("booksList").clearSelection();
		window.list("usersList").selectItem(0);		
		window.list("booksList").selectItem(0);		
		window.button("borrowBookButton").requireEnabled();		
	}
	
	@Test
	@GUITest
	public void testReturnBorrowedBookButtonShouldBeEnabledOnlyWhenUserAndBorrowedBookIsSelected() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		GuiActionRunner.execute(() -> {
			libraryView.getUserModelList().addElement(user);
			libraryView.getBorrowedBooksModelList().addElement(book);
		});
		
		window.list("usersList").selectItem(0);
		window.button("returnBorrowedBookButton").requireDisabled();
		
		window.list("usersList").clearSelection();
		window.list("borrowedBooksList").selectItem(0);
		window.button("returnBorrowedBookButton").requireDisabled();
		
		window.list("borrowedBooksList").clearSelection();
		window.list("usersList").selectItem(0);		
		window.list("borrowedBooksList").selectItem(0);		
		window.button("returnBorrowedBookButton").requireEnabled();
	}

	
	@Test
	@GUITest
	public void testShowUsersShouldAddUserDescriptionsToTheList() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
	
		GuiActionRunner.execute(
				() -> libraryView.showUsers(Arrays.asList(user1, user2))
		);
		String[] listContents = window.list("usersList").contents();
	
		assertThat(listContents).containsExactly(user1.toString(), user2.toString());
	}
	
	@Test
	@GUITest
	public void testShowBooksShouldAddBookDescriptionsToTheList() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
	
		GuiActionRunner.execute(
				() -> libraryView.showBooks(Arrays.asList(book1, book2))
		);
		String[] listContents = window.list("booksList").contents();
		
		assertThat(listContents).containsExactly(book1.toString(), book2.toString());
	}	
	
	@Test
	@GUITest
	public void testShowBorrowedBooksShouldAddBookDescriptionsToTheList() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
	
		GuiActionRunner.execute(
				() -> libraryView.showBorrowedBooks(Arrays.asList(book1, book2))
		);
		String[] listContents = window.list("borrowedBooksList").contents();
		
		assertThat(listContents).containsExactly(book1.toString(), book2.toString());
	}	

	
	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(
				() -> libraryView.showError("error message")
		);
		
		window.label("errorMessageLabel").requireText("error message");
	}
	
	
	@Test
	@GUITest
	public void testUserAddedShouldAddTheUserToTheList() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		GuiActionRunner.execute(
				() -> libraryView.userAdded(user)
		);
		String[] listContents = window.list("usersList").contents();
		
		assertThat(listContents).containsExactly(user.toString());
	}
	
	@Test
	@GUITest
	public void testUserAddedShouldResetTheErrorLabel() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());

		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.userAdded(user);
		});
		
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testBookAddedShouldAddTheBookToTheList() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(
				() -> libraryView.bookAdded(book)
		);
		String[] listContents = window.list("booksList").contents();
		
		assertThat(listContents).containsExactly(book.toString());
	}

	@Test
	@GUITest
	public void testBookAddedShouldResetTheErrorLabel() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);

		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.bookAdded(book);
		});
		
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testUserDeletedShouldRemoveTheUserFromTheList() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
		GuiActionRunner.execute(() -> {
			DefaultListModel<User> usersModelList = libraryView.getUserModelList();
			usersModelList.addElement(user1);
			usersModelList.addElement(user2);
		});
		
		GuiActionRunner.execute(
				() -> libraryView.userDeleted(new User(USER_ID_1, USER_NAME_1, Collections.emptySet())));
		
		String[] listContents = window.list("usersList").contents();
	
		assertThat(listContents).containsExactly(user2.toString());
	}

	@Test
	@GUITest
	public void testUserDeletedShouldResetTheErrorLabel() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		
		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.userDeleted(user);
		});
		
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testBookDeletedShouldRemoveTheBookFromTheList() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Book> booksModelList = libraryView.getBookModelList();
			booksModelList.addElement(book1);
			booksModelList.addElement(book2);
		});
		
		GuiActionRunner.execute(
				() -> libraryView.bookDeleted(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1)));
		String[] listContents = window.list("booksList").contents();
	
		assertThat(listContents).containsExactly(book2.toString());
	}
	
	@Test
	@GUITest
	public void testBookDeletedShouldResetTheErrorLabel() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.bookDeleted(book);
		});
	
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	@GUITest
	public void testBookBorrowedShouldAddTheBookToTheBorroewedBooksList() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		
		GuiActionRunner.execute(
				() -> libraryView.bookBorrowed(book)
		);
		String[] listContents = window.list("borrowedBooksList").contents();
		
		assertThat(listContents).containsExactly(book.toString());
	}

	@Test
	@GUITest
	public void testBookBorrowedShouldResetTheErrorLabel() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.bookBorrowed(book);
		});
	
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testBookReturnedShouldRemoveTheBookFromBorrowedBooksList() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Book> borrowedBooksModelList = libraryView.getBorrowedBooksModelList();
			borrowedBooksModelList.addElement(book1);
			borrowedBooksModelList.addElement(book2);
		});
		
		GuiActionRunner.execute(
				() -> libraryView.bookReturned(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1)));
		String[] listContents = window.list("borrowedBooksList").contents();
	
		assertThat(listContents).containsExactly(book2.toString());
	}

	@Test
	@GUITest
	public void testBookReturnedShouldResetTheErrorLabel() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(() -> {
			libraryView.showError("Some error occoured!");
			libraryView.bookReturned(book);
		});
	
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testUserUpdatedThrowsException() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		assertThatThrownBy(() -> libraryView.userUpdated(user)).isInstanceOf(UnsupportedOperationException.class).hasMessage("Unsupported operation!");
	}
	
	@Test
	@GUITest
	public void testBookUpdatedThrowsException() {
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		assertThatThrownBy(() -> libraryView.bookUpdated(book)).isInstanceOf(UnsupportedOperationException.class).hasMessage("Unsupported operation!");
	}
	
	@Test
	@GUITest
	public void testAddUserButtonShouldDelegateToLibraryControllerAddUser() {
		window.textBox("idUserTextField").enterText(USER_STR_ID_1);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);

		window.button("addUserButton").click();

		verify(libController).addUser(new User(USER_ID_1, USER_NAME_1, new HashSet<>()));
	}

	@Test
	@GUITest
	public void testAddBookButtonShouldDelegateToLibraryControllerAddBook() {
		window.textBox("idBookTextField").enterText(BOOK_STR_ID_1);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);

		window.button("addBookButton").click();

		verify(libController).addBook(new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1));
	}

	@Test
	@GUITest
	public void testUserDeleteButtonShouldDelegateToLibraryControllerDeleteUser() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<User> userModelList = libraryView.getUserModelList();
			userModelList.addElement(user1);
			userModelList.addElement(user2);
		});
		window.list("usersList").selectItem(1);
		window.button("userDeleteButton").click();
		
		verify(libController).deleteUser(user2);
	}

	@Test
	@GUITest
	public void testDeleteBookButtonShouldDelegateToLibraryControllerDeleteBook() {
		Book book1 = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
		Book book2 = new Book(BOOK_ID_2, BOOK_TITLE_2, BOOK_AUTHOR_2);
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<Book> bookModelList = libraryView.getBookModelList();
			bookModelList.addElement(book1);
			bookModelList.addElement(book2);
		});
		window.list("booksList").selectItem(1);
		window.button("deleteBookButton").click();
	
		verify(libController).deleteBook(book2);
	}
	
	@Test
	@GUITest
	public void testBorrowBookButtonShouldDelegateToLibraryControllerBorrowBook() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<User> userModelList = libraryView.getUserModelList();
			DefaultListModel<Book> bookModelList = libraryView.getBookModelList();
			userModelList.addElement(user);
			bookModelList.addElement(book);
		});
		window.list("usersList").selectItem(0);
		window.list("booksList").selectItem(0);
		window.button("borrowBookButton").click();
		
		verify(libController).borrowBook(user, book);
	}
	
	
	@Test
	@GUITest
	public void testReturnBorrowedBookButtonShouldDelegateToLabraryControllerReturnBook() {
		User user = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		Book book = new Book(BOOK_ID_1, BOOK_TITLE_1, BOOK_AUTHOR_1);
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<User> userModelList = libraryView.getUserModelList();
			DefaultListModel<Book> borrowedBooksModelList = libraryView.getBorrowedBooksModelList();
			userModelList.addElement(user);
			borrowedBooksModelList.addElement(book);
		});
		window.list("usersList").selectItem(0);
		window.list("borrowedBooksList").selectItem(0);
		window.button("returnBorrowedBookButton").click();
		
		verify(libController).returnBook(user, book);
	}
	
	@Test
	@GUITest
	public void testWhenUserSelectedShouldDelegateToLibraryControllerAllBorrowedBooks() {
		User user1 = new User(USER_ID_1, USER_NAME_1, Collections.emptySet());
		User user2 = new User(USER_ID_2, USER_NAME_2, Collections.emptySet());
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<User> userModelList = libraryView.getUserModelList();
			userModelList.addElement(user1);
			userModelList.addElement(user2);
		});
		window.list("usersList").selectItem(1);
		
		verify(libController).allBorrowedBooks(user2);
	}
}
