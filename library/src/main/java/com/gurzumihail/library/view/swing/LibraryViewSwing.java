package com.gurzumihail.library.view.swing;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.gurzumihail.library.model.Book;
import com.gurzumihail.library.model.User;
import com.gurzumihail.library.view.LibraryView;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class LibraryViewSwing extends JFrame implements LibraryView{

	private JPanel contentPane;
	private JTextField idUserTextField;
	private JTextField nameUserTextField;
	private JLabel userLabel;
	private JTextField idBookTextField;
	private JTextField titleBookTextField;
	private JTextField authorBookTextField;

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
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LibraryViewSwing() {
		setTitle("Library View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1110, 771);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel idUserLabel = new JLabel("id");
		idUserLabel.setName("idUserLabel");
		idUserLabel.setBounds(28, 33, 13, 17);
		contentPane.add(idUserLabel);
		
		idUserTextField = new JTextField();
		idUserTextField.setName("idUserTextField");
		idUserTextField.setBounds(81, 33, 185, 17);
		contentPane.add(idUserTextField);
		idUserTextField.setColumns(10);
		
		JLabel nameUserLabel = new JLabel("name");
		nameUserLabel.setName("nameUserLabel");
		nameUserLabel.setBounds(25, 62, 39, 15);
		contentPane.add(nameUserLabel);
		
		nameUserTextField = new JTextField();
		nameUserTextField.setName("nameUserTextField");
		nameUserTextField.setBounds(81, 62, 185, 17);
		contentPane.add(nameUserTextField);
		nameUserTextField.setColumns(10);
		
		userLabel = new JLabel("Insert id and name of the new user");
		userLabel.setName("userLabel");
		userLabel.setBounds(12, 0, 271, 15);
		contentPane.add(userLabel);
		
		JButton addUserButton = new JButton("Add User");
		addUserButton.setName("addUserButton");
		addUserButton.setEnabled(false);
		addUserButton.setBounds(99, 104, 117, 25);
		contentPane.add(addUserButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(28, 189, 271, 344);
		contentPane.add(scrollPane);
		
		JList usersList = new JList();
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(usersList);
		usersList.setName("usersList");
		
		JLabel usersListLabel = new JLabel("All Users");
		usersListLabel.setName("usersListLabel");
		usersListLabel.setBounds(115, 162, 117, 15);
		contentPane.add(usersListLabel);
		
		JButton userDeleteButton = new JButton("Delete Selected");
		userDeleteButton.setName("userDeleteButton");
		userDeleteButton.setEnabled(false);
		userDeleteButton.setBounds(81, 545, 157, 25);
		contentPane.add(userDeleteButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(416, 189, 271, 344);
		contentPane.add(scrollPane_1);
		
		JList borrowedBooksList = new JList();
		borrowedBooksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(borrowedBooksList);
		borrowedBooksList.setName("borrowedBooksList");
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(788, 189, 271, 344);
		contentPane.add(scrollPane_2);
		
		JList booksList = new JList();
		booksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(booksList);
		booksList.setName("booksList");
		
		JLabel bookLabel = new JLabel("Insert id, title and author of the new book\n");
		bookLabel.setName("bookLabel");
		bookLabel.setBounds(788, 0, 310, 15);
		contentPane.add(bookLabel);
		
		JLabel idBookLabel = new JLabel("id");
		idBookLabel.setName("idBookLabel");
		idBookLabel.setBounds(798, 34, 70, 15);
		contentPane.add(idBookLabel);
		
		JLabel titleBookLabel = new JLabel("title");
		titleBookLabel.setName("titleBookLabel");
		titleBookLabel.setBounds(798, 62, 70, 15);
		contentPane.add(titleBookLabel);
		
		JLabel authorBookLabel = new JLabel("author");
		authorBookLabel.setName("authorBookLabel");
		authorBookLabel.setBounds(798, 91, 70, 15);
		contentPane.add(authorBookLabel);
		
		idBookTextField = new JTextField();
		idBookTextField.setName("idBookTextField");
		idBookTextField.setColumns(10);
		idBookTextField.setBounds(857, 32, 185, 17);
		contentPane.add(idBookTextField);
		
		titleBookTextField = new JTextField();
		titleBookTextField.setName("titleBookTextField");
		titleBookTextField.setColumns(10);
		titleBookTextField.setBounds(857, 61, 185, 17);
		contentPane.add(titleBookTextField);
		
		authorBookTextField = new JTextField();
		authorBookTextField.setName("authorBookTextField");
		authorBookTextField.setColumns(10);
		authorBookTextField.setBounds(857, 91, 185, 17);
		contentPane.add(authorBookTextField);
		
		JButton addBookButton = new JButton("Add Book");
		addBookButton.setName("addBookButton");
		addBookButton.setEnabled(false);
		addBookButton.setBounds(867, 120, 117, 25);
		contentPane.add(addBookButton);
		
		JLabel bookListLabel = new JLabel("All Books");
		bookListLabel.setName("bookListLabel");
		bookListLabel.setBounds(893, 162, 100, 15);
		contentPane.add(bookListLabel);
		
		JLabel borrowedBooksListLabel = new JLabel("Select a book in the list to return");
		borrowedBooksListLabel.setName("borrowedBooksListLabel");
		borrowedBooksListLabel.setBounds(431, 162, 256, 15);
		contentPane.add(borrowedBooksListLabel);
		
		JButton returnBorrowedBookButton = new JButton("Return");
		returnBorrowedBookButton.setName("returnBorrowedBookButton");
		returnBorrowedBookButton.setEnabled(false);
		returnBorrowedBookButton.setBounds(496, 545, 117, 25);
		contentPane.add(returnBorrowedBookButton);
		
		JButton deleteBookButton = new JButton("Delete");
		deleteBookButton.setName("deleteBookButton");
		deleteBookButton.setEnabled(false);
		deleteBookButton.setBounds(941, 545, 117, 25);
		contentPane.add(deleteBookButton);
		
		JButton borrowBookButton = new JButton("Borrow");
		borrowBookButton.setName("borrowBookButton");
		borrowBookButton.setEnabled(false);
		borrowBookButton.setBounds(788, 545, 117, 25);
		contentPane.add(borrowBookButton);
		
		JLabel errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		errorMessageLabel.setBounds(99, 582, 934, 30);
		contentPane.add(errorMessageLabel);
	}

	@Override
	public void showUsers(List<User> users) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBooks(List<Book> books) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userAdded(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userUpdated(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userDeleted(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookAdded(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookUpdated(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookDeleted(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookBorrowed(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookReturned(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String message) {
		// TODO Auto-generated method stub
		
	}
}
