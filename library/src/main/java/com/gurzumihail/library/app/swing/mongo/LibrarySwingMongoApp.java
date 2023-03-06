package com.gurzumihail.library.app.swing.mongo;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.mongo.TransactionManagerMongo;
import com.gurzumihail.library.view.swing.LibraryViewSwing;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class LibrarySwingMongoApp implements Callable<Void> {
	
	private static final Logger LOGGER = LogManager.getLogger(LibrarySwingMongoApp.class);
	private static final String LOCALHOST = "localhost";
	
	@Option(names = { "--mongo-host-1" }, description = "MongoDB host-1 address")
	private String mongoHost1 = LOCALHOST;
	
	@Option(names = { "--mongo-host-2" }, description = "MongoDB host-2 address")
	private String mongoHost2 = LOCALHOST;

	@Option(names = { "--mongo-host-3" }, description = "MongoDB host-3 address")
	private String mongoHost3 = LOCALHOST;

	@Option(names = { "--mongo-port-1" }, description = "MongoDB host-1 port")
	private int mongoPort1 = 27017;

	@Option(names = { "--mongo-port-2" }, description = "MongoDB host-2 port")
	private int mongoPort2 = 27018;
	
	@Option(names = { "--mongo-port-3" }, description = "MongoDB host-3 port")
	private int mongoPort3 = 27019;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "library";
	
	@Option(names = { "--db-user-collection" }, description = "user collection name")
	private String userCollectionName = "user";
	
	@Option(names = { "--db-book-collection" }, description = "book collection name")
	private String bookCollectionName = "book";
	
	public static void main(String[] args) {
		new CommandLine(new LibrarySwingMongoApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = new MongoClient(Arrays.asList(
						   new ServerAddress(mongoHost1, mongoPort1),
						   new ServerAddress(mongoHost2, mongoPort2),
						   new ServerAddress(mongoHost3, mongoPort3)));
						 
				ClientSession session = client.startSession();
				MongoDatabase database = client.getDatabase(databaseName);
				List<String> existingCollections = database.listCollectionNames().into(new ArrayList<>());
				if(!existingCollections.contains(userCollectionName))
					database.createCollection(userCollectionName);
				if(!existingCollections.contains(bookCollectionName))
					database.createCollection(bookCollectionName);
				UserRepositoryMongo userRepoMongo = new UserRepositoryMongo(
						client, databaseName, userCollectionName, session);
				BookRepositoryMongo bookRepoMongo = new BookRepositoryMongo(
						client, databaseName, bookCollectionName, session);

				LibraryViewSwing libView = new LibraryViewSwing();
				TransactionManagerMongo transactionManager = new TransactionManagerMongo(userRepoMongo, bookRepoMongo, session);
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
