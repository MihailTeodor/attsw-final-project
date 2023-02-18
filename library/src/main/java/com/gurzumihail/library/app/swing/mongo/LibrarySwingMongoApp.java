package com.gurzumihail.library.app.swing.mongo;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.gurzumihail.library.controller.LibraryController;
import com.gurzumihail.library.repository.mongo.BookRepositoryMongo;
import com.gurzumihail.library.repository.mongo.UserRepositoryMongo;
import com.gurzumihail.library.transaction_manager.mongo.TransactionManagerMongo;
import com.gurzumihail.library.view.swing.LibraryViewSwing;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*

docker run -d -p 27017:27017 -p 27018:27018 -p 27019:27019 candis/mongo-replica-set 

docker run -p 27017:27017 --rm mongo:6.0.3 

*/
@Command(mixinStandardHelpOptions = true)
public class LibrarySwingMongoApp implements Callable<Void> {
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "library";
	
	@Option(names = { "--db-user-collection" }, description = "user collection name")
	private String userCollectionName = "user";
	
	@Option(names = { "--db-book-collection" }, description = "book collection name")
	private String bookCollectionName = "book";
	
	public static void main(String[] args) {
		new CommandLine(new LibrarySwingMongoApp()).execute(args);
	}
//	private static final String LIBRARY_DB_NAME = "library";
//	private static final String USER_COLLECTION_NAME = "user";
//	private static final String BOOK_COLLECTION_NAME = "book";
//	

	private static final Logger LOGGER = LogManager.getLogger(LibraryViewSwing.class);

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				ClientSession session = client.startSession();
				MongoDatabase database = client.getDatabase(databaseName);
				List<String> existingCollections = database.listCollectionNames().into(new ArrayList<>());
				if(!existingCollections.contains(userCollectionName))
					database.createCollection(userCollectionName);
				if(!existingCollections.contains(bookCollectionName))
					database.createCollection(bookCollectionName);
//				database.drop();
//				MongoCollection<Document> userCollection = database.getCollection(userCollectionName);
//				MongoCollection<Document> bookCollection = database.getCollection(bookCollectionName);
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
