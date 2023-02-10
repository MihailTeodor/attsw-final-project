package com.gurzumihail.library.view.swing;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.view.LibraryView;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class LibraryViewSwing extends JFrame implements LibraryView{

	private static final long serialVersionUID = 7607615145913897146L;

	private static final Logger LOGGER = LogManager.getLogger(LibraryViewSwing.class);

	private JPanel contentPane;
	private JLabel idUserLabel;
	private JTextField idUserTextField;
	private JLabel nameUserLabel;
	private JTextField nameUserTextField;
	private JLabel userLabel;
	private JButton addUserButton;
	private JScrollPane scrollPane;
	private JLabel usersListLabel;
	private JButton userDeleteButton;
	private JScrollPane scrollPane_1;
	private JTextField idBookTextField;
	private JTextField titleBookTextField;
	private JTextField authorBookTextField;
	private JButton addBookButton;
	private JScrollPane scrollPane_2;
	private JLabel bookLabel;
	private JLabel idBookLabel;
	private JLabel titleBookLabel;
	private JLabel authorBookLabel;
	private JLabel bookListLabel;
	private JLabel borrowedBooksListLabel;
	private JButton returnBorrowedBookButton;
	private JButton deleteBookButton;
	private JButton borrowBookButton;
	private JLabel errorMessageLabel;
	
	private JList<User> usersList;
	private DefaultListModel<User> usersModelList;
	
	private JList<Book> booksList;
	private DefaultListModel<Book> booksModelList;
	
	private JList<Book> borrowedBooksList;
	private DefaultListModel<Book> borrowedBooksModelList;
	
	DefaultListModel<User> getUserModelList() {
		return usersModelList;
	}
	
	DefaultListModel<Book> getBookModelList() {
		return booksModelList;
	}

	DefaultListModel<Book> getBorrowedBooksModelList() {
		return borrowedBooksModelList;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LibraryViewSwing frame = new LibraryViewSwing();
					frame.setVisible(true);
				} catch (Exception e) {
					LOGGER.info("an exception was thrown", e);;
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LibraryViewSwing() {
		
		
		KeyAdapter btnAddUserEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addUserButton.setEnabled(
						!idUserTextField.getText().trim().isEmpty() &&
						!nameUserTextField.getText().trim().isEmpty()
						);
			}
		};
		
		KeyAdapter btnAddBookEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addBookButton.setEnabled(
						!idBookTextField.getText().trim().isEmpty() &&
						!titleBookTextField.getText().trim().isEmpty() &&
						!authorBookTextField.getText().trim().isEmpty()
						);
			}
		};
		
		setTitle("Library View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1110, 771);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{52, 218, 117, 271, 101, 80, 12, 36, 157, 0};
		gbl_contentPane.rowHeights = new int[]{15, 18, 18, 17, 25, 15, 344, 25, 30, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		userLabel = new JLabel("Insert id and name of the new user");
		userLabel.setName("userLabel");
		GridBagConstraints gbc_userLabel = new GridBagConstraints();
		gbc_userLabel.anchor = GridBagConstraints.NORTH;
		gbc_userLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_userLabel.insets = new Insets(0, 0, 5, 5);
		gbc_userLabel.gridwidth = 2;
		gbc_userLabel.gridx = 0;
		gbc_userLabel.gridy = 0;
		contentPane.add(userLabel, gbc_userLabel);
		
		bookLabel = new JLabel("Insert id, title and author of the new book\n");
		bookLabel.setName("bookLabel");
		GridBagConstraints gbc_bookLabel = new GridBagConstraints();
		gbc_bookLabel.anchor = GridBagConstraints.NORTH;
		gbc_bookLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_bookLabel.insets = new Insets(0, 0, 5, 0);
		gbc_bookLabel.gridwidth = 4;
		gbc_bookLabel.gridx = 5;
		gbc_bookLabel.gridy = 0;
		contentPane.add(bookLabel, gbc_bookLabel);
		
		idUserTextField = new JTextField();
		idUserTextField.addKeyListener(btnAddUserEnabler);
		
		idUserLabel = new JLabel("id");
		idUserLabel.setName("idUserLabel");
		GridBagConstraints gbc_idUserLabel = new GridBagConstraints();
		gbc_idUserLabel.fill = GridBagConstraints.VERTICAL;
		gbc_idUserLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idUserLabel.gridx = 0;
		gbc_idUserLabel.gridy = 1;
		contentPane.add(idUserLabel, gbc_idUserLabel);
		idUserTextField.setName("idUserTextField");
		GridBagConstraints gbc_idUserTextField = new GridBagConstraints();
		gbc_idUserTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idUserTextField.insets = new Insets(0, 0, 5, 5);
		gbc_idUserTextField.gridx = 1;
		gbc_idUserTextField.gridy = 1;
		contentPane.add(idUserTextField, gbc_idUserTextField);
		idUserTextField.setColumns(10);
		
		idBookTextField = new JTextField();
		idBookTextField.addKeyListener(btnAddBookEnabler);
		idBookTextField.setName("idBookTextField");
		idBookTextField.setColumns(10);
		GridBagConstraints gbc_idBookTextField = new GridBagConstraints();
		gbc_idBookTextField.insets = new Insets(0, 0, 5, 0);
		gbc_idBookTextField.gridwidth = 4;
		gbc_idBookTextField.gridx = 5;
		gbc_idBookTextField.gridy = 1;
		contentPane.add(idBookTextField, gbc_idBookTextField);
		
		idBookLabel = new JLabel("id");
		idBookLabel.setName("idBookLabel");
		GridBagConstraints gbc_idBookLabel = new GridBagConstraints();
		gbc_idBookLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_idBookLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idBookLabel.gridx = 5;
		gbc_idBookLabel.gridy = 1;
		contentPane.add(idBookLabel, gbc_idBookLabel);
		
		nameUserLabel = new JLabel("name");
		nameUserLabel.setName("nameUserLabel");
		GridBagConstraints gbc_nameUserLabel = new GridBagConstraints();
		gbc_nameUserLabel.anchor = GridBagConstraints.EAST;
		gbc_nameUserLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameUserLabel.gridx = 0;
		gbc_nameUserLabel.gridy = 2;
		contentPane.add(nameUserLabel, gbc_nameUserLabel);
		
		nameUserTextField = new JTextField();
		nameUserTextField.addKeyListener(btnAddUserEnabler);
		nameUserTextField.setName("nameUserTextField");
		GridBagConstraints gbc_nameUserTextField = new GridBagConstraints();
		gbc_nameUserTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameUserTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nameUserTextField.gridx = 1;
		gbc_nameUserTextField.gridy = 2;
		contentPane.add(nameUserTextField, gbc_nameUserTextField);
		nameUserTextField.setColumns(10);
		
		titleBookTextField = new JTextField();
		titleBookTextField.addKeyListener(btnAddBookEnabler);
		titleBookTextField.setName("titleBookTextField");
		titleBookTextField.setColumns(10);
		GridBagConstraints gbc_titleBookTextField = new GridBagConstraints();
		gbc_titleBookTextField.insets = new Insets(0, 0, 5, 0);
		gbc_titleBookTextField.gridwidth = 4;
		gbc_titleBookTextField.gridx = 5;
		gbc_titleBookTextField.gridy = 2;
		contentPane.add(titleBookTextField, gbc_titleBookTextField);
		
		titleBookLabel = new JLabel("title");
		titleBookLabel.setName("titleBookLabel");
		GridBagConstraints gbc_titleBookLabel = new GridBagConstraints();
		gbc_titleBookLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_titleBookLabel.insets = new Insets(0, 0, 5, 5);
		gbc_titleBookLabel.gridx = 5;
		gbc_titleBookLabel.gridy = 2;
		contentPane.add(titleBookLabel, gbc_titleBookLabel);
		
		addUserButton = new JButton("Add User");
		addUserButton.setName("addUserButton");
		addUserButton.setEnabled(false);
		GridBagConstraints gbc_addUserButton = new GridBagConstraints();
		gbc_addUserButton.anchor = GridBagConstraints.WEST;
		gbc_addUserButton.insets = new Insets(0, 0, 5, 5);
		gbc_addUserButton.gridheight = 2;
		gbc_addUserButton.gridx = 1;
		gbc_addUserButton.gridy = 3;
		contentPane.add(addUserButton, gbc_addUserButton);
		
		authorBookLabel = new JLabel("author");
		authorBookLabel.setName("authorBookLabel");
		GridBagConstraints gbc_authorBookLabel = new GridBagConstraints();
		gbc_authorBookLabel.anchor = GridBagConstraints.NORTH;
		gbc_authorBookLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_authorBookLabel.insets = new Insets(0, 0, 5, 5);
		gbc_authorBookLabel.gridx = 5;
		gbc_authorBookLabel.gridy = 3;
		contentPane.add(authorBookLabel, gbc_authorBookLabel);
		
		authorBookTextField = new JTextField();
		authorBookTextField.addKeyListener(btnAddBookEnabler);
		authorBookTextField.setName("authorBookTextField");
		authorBookTextField.setColumns(10);
		GridBagConstraints gbc_authorBookTextField = new GridBagConstraints();
		gbc_authorBookTextField.fill = GridBagConstraints.VERTICAL;
		gbc_authorBookTextField.insets = new Insets(0, 0, 5, 0);
		gbc_authorBookTextField.gridwidth = 4;
		gbc_authorBookTextField.gridx = 5;
		gbc_authorBookTextField.gridy = 3;
		contentPane.add(authorBookTextField, gbc_authorBookTextField);
		
		addBookButton = new JButton("Add Book");
		addBookButton.setName("addBookButton");
		addBookButton.setEnabled(false);
		GridBagConstraints gbc_addBookButton = new GridBagConstraints();
		gbc_addBookButton.anchor = GridBagConstraints.NORTH;
		gbc_addBookButton.insets = new Insets(0, 0, 5, 0);
		gbc_addBookButton.gridwidth = 4;
		gbc_addBookButton.gridx = 5;
		gbc_addBookButton.gridy = 4;
		contentPane.add(addBookButton, gbc_addBookButton);
		
		usersListLabel = new JLabel("All Users");
		usersListLabel.setName("usersListLabel");
		GridBagConstraints gbc_usersListLabel = new GridBagConstraints();
		gbc_usersListLabel.anchor = GridBagConstraints.NORTH;
		gbc_usersListLabel.insets = new Insets(0, 0, 5, 5);
		gbc_usersListLabel.gridx = 1;
		gbc_usersListLabel.gridy = 5;
		contentPane.add(usersListLabel, gbc_usersListLabel);
		
		borrowedBooksListLabel = new JLabel("Select a book in the list to return");
		borrowedBooksListLabel.setName("borrowedBooksListLabel");
		GridBagConstraints gbc_borrowedBooksListLabel = new GridBagConstraints();
		gbc_borrowedBooksListLabel.anchor = GridBagConstraints.NORTH;
		gbc_borrowedBooksListLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_borrowedBooksListLabel.insets = new Insets(0, 0, 5, 5);
		gbc_borrowedBooksListLabel.gridx = 3;
		gbc_borrowedBooksListLabel.gridy = 5;
		contentPane.add(borrowedBooksListLabel, gbc_borrowedBooksListLabel);
		
		bookListLabel = new JLabel("All Books");
		bookListLabel.setName("bookListLabel");
		GridBagConstraints gbc_bookListLabel = new GridBagConstraints();
		gbc_bookListLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_bookListLabel.insets = new Insets(0, 0, 5, 0);
		gbc_bookListLabel.gridwidth = 3;
		gbc_bookListLabel.gridx = 6;
		gbc_bookListLabel.gridy = 5;
		contentPane.add(bookListLabel, gbc_bookListLabel);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.anchor = GridBagConstraints.EAST;
		gbc_scrollPane.fill = GridBagConstraints.VERTICAL;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 6;
		contentPane.add(scrollPane, gbc_scrollPane);
		usersModelList = new DefaultListModel<>();
		usersList = new JList<>(usersModelList);
		usersList.addListSelectionListener(
				e -> {
					userDeleteButton.setEnabled(usersList.getSelectedIndex() != -1);
					borrowBookButton.setEnabled(
							(usersList.getSelectedIndex() != -1) &&
							(booksList.getSelectedIndex() != -1));
				});
		
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(usersList);
		usersList.setName("usersList");
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.gridx = 3;
		gbc_scrollPane_1.gridy = 6;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		borrowedBooksModelList = new DefaultListModel<>();
		borrowedBooksList = new JList<Book>(borrowedBooksModelList);
		borrowedBooksList.addListSelectionListener(
				e -> {
					returnBorrowedBookButton.setEnabled(
							(usersList.getSelectedIndex() != -1) &&
							(borrowedBooksList.getSelectedIndex() != -1));
				});
		borrowedBooksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(borrowedBooksList);
		borrowedBooksList.setName("borrowedBooksList");
		
		scrollPane_2 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.anchor = GridBagConstraints.WEST;
		gbc_scrollPane_2.fill = GridBagConstraints.VERTICAL;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_2.gridwidth = 4;
		gbc_scrollPane_2.gridx = 5;
		gbc_scrollPane_2.gridy = 6;
		contentPane.add(scrollPane_2, gbc_scrollPane_2);
		
		booksModelList = new DefaultListModel<>();
		booksList = new JList<>(booksModelList);
		booksList.addListSelectionListener(
				e -> {
					deleteBookButton.setEnabled(booksList.getSelectedIndex() != -1);
					borrowBookButton.setEnabled(
							(usersList.getSelectedIndex() != -1) &&
							(booksList.getSelectedIndex() != -1));
				});
		
		booksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(booksList);
		booksList.setName("booksList");
		
		userDeleteButton = new JButton("Delete Selected");
		userDeleteButton.setName("userDeleteButton");
		userDeleteButton.setEnabled(false);
		GridBagConstraints gbc_userDeleteButton = new GridBagConstraints();
		gbc_userDeleteButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_userDeleteButton.insets = new Insets(0, 0, 5, 5);
		gbc_userDeleteButton.gridx = 1;
		gbc_userDeleteButton.gridy = 7;
		contentPane.add(userDeleteButton, gbc_userDeleteButton);
		
		returnBorrowedBookButton = new JButton("Return");
		returnBorrowedBookButton.setName("returnBorrowedBookButton");
		returnBorrowedBookButton.setEnabled(false);
		GridBagConstraints gbc_returnBorrowedBookButton = new GridBagConstraints();
		gbc_returnBorrowedBookButton.anchor = GridBagConstraints.NORTH;
		gbc_returnBorrowedBookButton.insets = new Insets(0, 0, 5, 5);
		gbc_returnBorrowedBookButton.gridx = 3;
		gbc_returnBorrowedBookButton.gridy = 7;
		contentPane.add(returnBorrowedBookButton, gbc_returnBorrowedBookButton);
		
		borrowBookButton = new JButton("Borrow");
		borrowBookButton.setName("borrowBookButton");
		borrowBookButton.setEnabled(false);
		GridBagConstraints gbc_borrowBookButton = new GridBagConstraints();
		gbc_borrowBookButton.anchor = GridBagConstraints.NORTH;
		gbc_borrowBookButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_borrowBookButton.insets = new Insets(0, 0, 5, 5);
		gbc_borrowBookButton.gridwidth = 2;
		gbc_borrowBookButton.gridx = 5;
		gbc_borrowBookButton.gridy = 7;
		contentPane.add(borrowBookButton, gbc_borrowBookButton);
		
		deleteBookButton = new JButton("Delete");
		deleteBookButton.setName("deleteBookButton");
		deleteBookButton.setEnabled(false);
		GridBagConstraints gbc_deleteBookButton = new GridBagConstraints();
		gbc_deleteBookButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_deleteBookButton.insets = new Insets(0, 0, 5, 0);
		gbc_deleteBookButton.gridx = 8;
		gbc_deleteBookButton.gridy = 7;
		contentPane.add(deleteBookButton, gbc_deleteBookButton);
		
		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		GridBagConstraints gbc_errorMessageLabel = new GridBagConstraints();
		gbc_errorMessageLabel.fill = GridBagConstraints.BOTH;
		gbc_errorMessageLabel.gridwidth = 8;
		gbc_errorMessageLabel.gridx = 1;
		gbc_errorMessageLabel.gridy = 8;
		contentPane.add(errorMessageLabel, gbc_errorMessageLabel);
	}

	@Override
	public void showUsers(List<User> users) {
		users.stream().forEach(usersModelList::addElement);
	}

	@Override
	public void showBooks(List<Book> books) {
		books.stream().forEach(booksModelList::addElement);		
	}

	@Override
	public void showBorrowedBooks(List<Book> borrowedBooks) {
		borrowedBooks.stream().forEach(borrowedBooksModelList::addElement);		
	}

	@Override
	public void userAdded(User user) {
		usersModelList.addElement(user);
		resetErrorLabel();
	}

	@Override
	public void userUpdated(User user) {
		throw new UnsupportedOperationException("Unsupported operation!");
	}

	@Override
	public void userDeleted(User user) {
		usersModelList.removeElement(user);
		resetErrorLabel();
	}

	@Override
	public void bookAdded(Book book) {
		booksModelList.addElement(book);
		resetErrorLabel();		
	}

	@Override
	public void bookUpdated(Book book) {
		throw new UnsupportedOperationException("Unsupported operation!");
	}

	@Override
	public void bookDeleted(Book book) {
		booksModelList.removeElement(book);
		resetErrorLabel();
	}

	@Override
	public void bookBorrowed(Book book) {
		throw new UnsupportedOperationException("Unsupported operation!");
	}

	@Override
	public void bookReturned(Book book) {
		throw new UnsupportedOperationException("Unsupported operation!");
	}

	@Override
	public void showError(String message) {
		errorMessageLabel.setText(message);
	}

	private void resetErrorLabel() {
		errorMessageLabel.setText(" ");
	}
}
