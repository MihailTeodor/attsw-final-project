package com.gurzumihail.library.app.swing.mongo;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;


/*
 * to be able to run these tests singularly:
 * 
 * docker run -d -p 27017:27017 -p 27018:27018 -p 27019:27019 --rm candis/mongo-replica-set 
 * 
 * and wait for the container to be up and running (30 to 60 sec) 	
 */

@RunWith(GUITestRunner.class)
public class LibrarySwingMongoAppE2E extends AssertJSwingJUnitTestCase {

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String DATABASE_NAME = "library";
	private static final String USER_COLLECTION_NAME = "user";
	private static final String BOOK_COLLECTION_NAME = "book";
	
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
	
	private MongoClient mongoClient;
	private FrameFixture window;
	
	@Override
	protected void onSetUp() throws Exception {
		 mongoClient = new MongoClient(new ServerAddress(MONGO_HOST, MONGO_PORT));
		 mongoClient.getDatabase(DATABASE_NAME).drop();
		 
		 addTestUserToDatabase(new User(USER_FIXTURE_1_ID, USER_FIXTURE_1_NAME, Collections.emptySet()));
		 addTestUserToDatabase(new User(USER_FIXTURE_2_ID, USER_FIXTURE_2_NAME, Collections.emptySet()));
		 
		 addTestBookToDatabase(new Book(BOOK_FIXTURE_1_ID, BOOK_FIXTURE_1_TITLE, BOOK_FIXTURE_1_AUTHOR));
		 addTestBookToDatabase(new Book(BOOK_FIXTURE_2_ID, BOOK_FIXTURE_2_TITLE, BOOK_FIXTURE_2_AUTHOR));

		 
		 
		 application("com.gurzumihail.library.app.swing.mongo.LibrarySwingMongoApp")
		 	.withArgs(
		 			"--mongo-host=" + MONGO_HOST,
		 			"--mongo-port=" + MONGO_PORT,
		 			"--db-name=" + DATABASE_NAME,
		 			"--db-user-collection=" + USER_COLLECTION_NAME,
		 			"--db-book-collection=" + BOOK_COLLECTION_NAME
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
	protected void onTearDown() {
		mongoClient.close();
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
	public void testAddUserButtonError() {
		window.textBox("idUserTextField").enterText(USER_FIXTURE_1_STRING_ID);
		window.textBox("nameUserTextField").enterText(USER_NAME_1);
		window.button("addUserButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
			.contains("Already existing user with id " + USER_FIXTURE_1_STRING_ID);
		
	}
	
	@Test
	@GUITest
	public void testAddBookButtonError() {
		window.textBox("idBookTextField").enterText(BOOK_FIXTURE_1_STRING_ID);
		window.textBox("titleBookTextField").enterText(BOOK_TITLE_1);
		window.textBox("authorBookTextField").enterText(BOOK_AUTHOR_1);
		window.button("addBookButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
		.contains("Already existing book with id " + BOOK_FIXTURE_1_STRING_ID);
	
	}
	
	@Test
	@GUITest
	public void testBorrowBookButton() {
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
	public void testReturnBookButton() {
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
	public void testDeleteUserButtonSuccess() {
		window.list("usersList")
			.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		
		window.button("userDeleteButton").click();
		
		assertThat(window.list("usersList").contents())
			.noneMatch(e -> e.contains(USER_FIXTURE_1_NAME));
	}
	
	@Test
	@GUITest
	public void testDeleteBookButtonSuccess() {
		window.list("booksList")
			.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("deleteBookButton").click();
		
		assertThat(window.list("booksList").contents())
			.noneMatch(e -> e.contains(BOOK_FIXTURE_1_TITLE));
	}
	
	@Test
	@GUITest
	public void testDeleteUserButtonWhenUserDoesNotExistError() {
		window.list("usersList")
		.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		removeTestUserFromDatabase(USER_FIXTURE_1_ID);
		
		window.button("userDeleteButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
			.contains("No existing user with id " + USER_FIXTURE_1_STRING_ID);
	}
	
	@Test
	@GUITest
	public void testDeleteUserButtonWhenUserHasBorrowedBooksError() {
		window.list("usersList")
		.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		window.list("booksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("borrowBookButton").click();
		window.button("userDeleteButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
			.contains("Before deleting user return all borrowed books!");
	}
	
	@Test
	@GUITest
	public void testDeleteBookButtonWhenBookDoesNotExistError() {
		window.list("booksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		removeTestBookFromDatabase(BOOK_FIXTURE_1_ID);
		
		window.button("deleteBookButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
			.contains("No existing book with id " + BOOK_FIXTURE_1_STRING_ID);
	}

	@Test
	@GUITest
	public void testDeleteBookButtonWhenBookStillBorrowedError() {
		window.list("usersList")
		.selectItem(Pattern.compile(".*" + USER_FIXTURE_1_NAME + ".*"));
		window.list("booksList")
		.selectItem(Pattern.compile(".*" + BOOK_FIXTURE_1_TITLE + ".*"));
		
		window.button("borrowBookButton").click();
		window.button("deleteBookButton").click();
		
		assertThat(window.label("errorMessageLabel").text())
			.contains("Cannot cancel this book! Book borrowed by user with id: " + USER_FIXTURE_1_STRING_ID);
		
	}
	
	
	private void addTestUserToDatabase(User user) {
		mongoClient
			.getDatabase(DATABASE_NAME)
			.getCollection(USER_COLLECTION_NAME)
			.insertOne(fromUserToDocument(user));
	}
	
	private void addTestBookToDatabase(Book book) {
		mongoClient.getDatabase(DATABASE_NAME)
		.getCollection(BOOK_COLLECTION_NAME)
		.insertOne(fromBookToDocument(book));
	}
	
	private Document fromUserToDocument(User user) {
		return new Document().append("id", user.getId()).append("name", user.getName())
				.append("rentedBooks", user.getRentedBooks().stream().map(this::fromBookToDocument)
						.collect(Collectors.toList()));
	}
	
	private Document fromBookToDocument(Book book) {
		return new Document().append("id", book.getId()).append("title", book.getTitle())
				.append("author", book.getAuthor()).append("available", book.isAvailable())
				.append("userId", book.getUserID());
	}

	private void removeTestUserFromDatabase(int id) {
		mongoClient
			.getDatabase(DATABASE_NAME)
			.getCollection(USER_COLLECTION_NAME)
			.deleteOne(Filters.eq("id", id));
	}

	private void removeTestBookFromDatabase(int id) {
		mongoClient
			.getDatabase(DATABASE_NAME)
			.getCollection(BOOK_COLLECTION_NAME)
			.deleteOne(Filters.eq("id", id));
	}

}
