package com.gurzumihail.library.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class LibraryViewSwingTest extends AssertJSwingJUnitTestCase{

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

	@Test @GUITest
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
}
