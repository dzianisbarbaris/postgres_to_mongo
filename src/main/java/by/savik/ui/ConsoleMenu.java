package by.savik.ui;

import by.savik.factory.FurnitureFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private static final Logger logger = LogManager.getLogger(ConsoleMenu.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MONGO_URL = "mongodb://admin:admin123@localhost:27017/?authSource=admin&authMechanism=SCRAM-SHA-1";
    private static final String MONGO_DB = "furniture_db";
    private static final String URL = "jdbc:postgresql://localhost:5432/furniture_db";
    private static final String USER = "user";
    private static final String PASSWORD = "1234";
    private FurniturePostgresService furniturePostgresService;
    private FurnitureMongoService furnitureMongoService;
    private FurniturePostgresToMongoService furniturePostgresToMongoService;
    private Connection postgresConnection;
    private MongoClient mongoClient;

    public void start() {
        initializeConnections();
        while (true) {
            mainMenu();
            logger.info("Select an action: ");
            int action = scanner.nextInt();
            switch (action) {
                case 1:
                    addFurnitureToPostgres();
                    break;
                case 2:
                    transferFurnitureByType();
                    break;
                case 3:
                    exportFurnitureByMaterialFromMongoDB();
                    break;
                case 4:
                    closeConnections();
                    return;
            }
        }
    }

    public void mainMenu() {
        logger.info("==== Furniture Manager ====");
        logger.info("1. Add furniture to postgres database.");
        logger.info("2. Transfer furniture by type from postgres to MongoDB.");
        logger.info("3. Export furniture by material from MongoDB.");
        logger.info("4. Exit.");
    }

    private void initializeConnections() {
        try {
            postgresConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            mongoClient = MongoClients.create(MONGO_URL);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
            FurniturePostgresRepository postgresRepository = new FurniturePostgresRepository(postgresConnection);
            FurnitureMongoRepository mongoRepository = new FurnitureMongoRepository(mongoDatabase);
            furniturePostgresService = new FurniturePostgresService(postgresRepository);
            furnitureMongoService = new FurnitureMongoService(mongoRepository);
            furniturePostgresToMongoService = new FurniturePostgresToMongoService(postgresRepository, mongoRepository);

            logger.info("Database connections initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize PostgreSQL connection: ", e);
        } catch (MongoException e) {
            logger.error("Failed to initialize MongoDB connection: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error during initialization: ", e);
        }
    }

    private void closeConnections() {
        try {
            if (postgresConnection != null && !postgresConnection.isClosed()) {
                postgresConnection.close();
            }
            if (mongoClient != null) {
                mongoClient.close();
            }
            logger.info("Database connections closed");
        } catch (SQLException e) {
            logger.error("Error closing postgres connection: ", e);
        }
    }

    public void addFurnitureToPostgres() {
        try {
            logger.info("Enter the number of furniture items");
            int n = scanner.nextInt();
            logger.info("Adding furniture to postgres database");
            /*for (int i = 0; i < n; i++) {
                Furniture furniture = FurnitureFactory.next();
                furniturePostgresService.addFurniture(furniture);
                logger.info(i + 1 + " " + furniture + " added to database");
            }*/
            List<Furniture> furnitureList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                furnitureList.add(FurnitureFactory.next());
            }
            furniturePostgresService.addAllFurniture(furnitureList);

        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
    }

    public void transferFurnitureByType() {
        List<Furniture> furnitureList = new ArrayList<>();
        try {
            logger.info("Enter the type of furniture items");
            Type type = Type.valueOf(scanner.next().toUpperCase());
            furnitureList = furniturePostgresToMongoService.transferFurnitureByType(type);
            logger.info("Item count with with type - " + type + " = " + furnitureList.size() + " ");
            furnitureList.forEach(System.out::println);
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
    }

    private List<Furniture> exportFurnitureByMaterialFromMongoDB() {
        List<Furniture> furnitureList = new ArrayList<>();
        try {
            logger.info("Enter the material of furniture items");
            String material = scanner.next();
            furnitureList = furnitureMongoService.getFurnitureByMaterial(material);
            logger.info("Item count with material - " + material + " = " + furnitureList.size() + " ");
            furnitureList.forEach(logger::info);
        } catch (MongoException e) {
            logger.error("The mongo database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
        return furnitureList;
    }
}
