package com.gurzumihail.library.app.swing.mysql;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.repository.mysql.BookRepositoryMySql;
import com.gurzumihail.library.repository.mysql.UserRepositoryMySql;
import com.gurzumihail.library.transaction_manager.mysql.TransactionManagerMySql;
import com.gurzumihail.library.view.swing.LibraryViewSwing;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class LibrarySwingMySqlApp implements Callable<Void>{

	private static final Logger LOGGER = LogManager.getLogger(LibrarySwingMySqlApp.class);
	
	@Option(names = { "--mysql-host" }, description = "MySql host address")
	private String mySqlHost = "localhost";

	@Option(names = { "--mysql-port" }, description = "MySql host port")
	private int mySqlPort = 3306;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "library";
	
	@Option(names = { "--db-user" }, description = "username")
	private String username = "root";

	@Option(names = { "--db-password" }, description = "password")
	private String password = "password";

	public static void main(String[] args) {
		new CommandLine(new LibrarySwingMySqlApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				String jdbcURL = String.format("jdbc:mysql://%s:%s/%s", mySqlHost, mySqlPort, databaseName);
				Connection connection = DriverManager.getConnection(jdbcURL, username, password);
				UserRepositoryMySql userRepository = new UserRepositoryMySql(connection);
				BookRepositoryMySql bookRepository = new BookRepositoryMySql(connection);
				
				LibraryViewSwing libView = new LibraryViewSwing();
				TransactionManagerMySql transactionManager = new TransactionManagerMySql(userRepository, bookRepository, connection);
				LibraryController libController = new LibraryController(libView, transactionManager);

				libView.setLibraryController(libController);
				libView.setVisible(true);
				
				libController.allUsers();
				libController.allBooks();
			} catch (Exception e) {
				LOGGER.info("an exception was thrown", e);
			}
		});
		return null;
	}
}
