package com.gurzumihail.library.view.swing;

import java.util.Collections;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;

@RunWith(GUITestRunner.class)
public class LibraryViewSwingTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private LibraryViewSwing libraryView;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			libraryView = new LibraryViewSwing();
			return libraryView;
		});
		window = new FrameFixture(robot(), libraryView);
		window.show();
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
		window.textBox("idUserTextField").enterText("1");
		window.textBox("nameUserTextField").enterText("Mihail");
		
		window.button("addUserButton").requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEitherIdOrNameAreBlankThenAddUserButtonShouldBeDisabled() {
		window.textBox("idUserTextField").enterText("1");
		window.textBox("nameUserTextField").enterText(" ");
		window.button("addUserButton").requireDisabled();

		window.textBox("idUserTextField").setText("");
		window.textBox("nameUserTextField").setText("");

		window.textBox("idUserTextField").enterText(" ");
		window.textBox("nameUserTextField").enterText("Mihail");
		window.button("addUserButton").requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenIdTitleAndAuthorAreAllNotEmptyThenAddBookShouldBeEnabled() {
		window.textBox("idBookTextField").enterText("1");
		window.textBox("titleBookTextField").enterText("Cujo");
		window.textBox("authorBookTextField").enterText("King");
		
		window.button("addBookButton").requireEnabled();
	}
	
	@Test
	@GUITest
	public void testWhenEitherIdOrTitleOrAuthorAreBlankThenAddBookButtonShouldBeDisabled() {
		window.textBox("idBookTextField").enterText("1");
		window.textBox("titleBookTextField").enterText(" ");
		window.textBox("authorBookTextField").enterText(" ");

		window.button("addBookButton").requireDisabled();

		window.textBox("idBookTextField").setText("");
		window.textBox("titleBookTextField").setText("");
		window.textBox("authorBookTextField").setText("");

		window.textBox("idBookTextField").enterText(" ");
		window.textBox("titleBookTextField").enterText("Cujo");
		window.textBox("authorBookTextField").enterText(" ");

		window.button("addBookButton").requireDisabled();	

		window.textBox("idBookTextField").setText("");
		window.textBox("titleBookTextField").setText("");
		window.textBox("authorBookTextField").setText("");

		window.textBox("idBookTextField").enterText(" ");
		window.textBox("titleBookTextField").enterText(" ");
		window.textBox("authorBookTextField").enterText("King");

		window.button("addBookButton").requireDisabled();
	}
	
	@Test
	@GUITest
	public void testDeleteUserButtonShouldBeEnabledOnlyWhenUserIsSelected() {
		User user = new User(1, "Mihail", Collections.emptySet());
		GuiActionRunner.execute(() -> libraryView.getUserModelList().addElement(user));

		window.list("usersList").selectItem(0);
		window.button("userDeleteButton").requireEnabled();

		window.list("usersList").clearSelection();
		window.button("userDeleteButton").requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteBookButtonShouldBeEnabledOnlyWhenBookIsSelected() {
		Book book = new Book(1, "Cujo", "King");
		GuiActionRunner.execute(() -> libraryView.getBookModelList().addElement(book));

		window.list("booksList").selectItem(0);
		window.button("deleteBookButton").requireEnabled();

		window.list("booksList").clearSelection();
		window.button("deleteBookButton").requireDisabled();
	}
	
	@Test
	@GUITest
	public void testBorrowBookButtonShouldBeEnabledOnlyWhenUserAndBookIsSelected() {
		User user = new User(1, "Mihail", Collections.emptySet());
		Book book = new Book(1, "Cujo", "King");
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
		User user = new User(1, "Mihail", Collections.emptySet());
		Book book = new Book(1, "Cujo", "King");
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
