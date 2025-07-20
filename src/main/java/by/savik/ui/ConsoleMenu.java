package by.savik.ui;

import by.savik.factory.FurnitureFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.repository.FurniturePostgresToMongoRepository;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
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
            System.out.print("Select an action: ");
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
        System.out.println("==== Furniture Manager ====");
        System.out.println("1. Add furniture to postgres database.");
        System.out.println("2. Transfer furniture by type from postgres to MongoDB.");
        System.out.println("3. Export furniture by material from MongoDB.");
        System.out.println("4. Exit.");
    }

    private void initializeConnections() {
        try {
            postgresConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            mongoClient = MongoClients.create(MONGO_URL);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
            FurniturePostgresRepository postgresRepository = new FurniturePostgresRepository(postgresConnection);
            FurnitureMongoRepository mongoRepository = new FurnitureMongoRepository(mongoDatabase);
            FurniturePostgresToMongoRepository postgresToMongoRepository = new FurniturePostgresToMongoRepository(postgresConnection, mongoDatabase);
            furniturePostgresService = new FurniturePostgresService(postgresRepository);
            furnitureMongoService = new FurnitureMongoService(mongoRepository);
            furniturePostgresToMongoService = new FurniturePostgresToMongoService(postgresToMongoRepository);

            System.out.println("Database connections initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize PostgreSQL connection: " + e.getMessage());
        } catch (MongoException e) {
            System.err.println("Failed to initialize MongoDB connection: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during initialization: " + e.getMessage());
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
            System.out.println("Database connections closed");
        } catch (SQLException e) {
            System.err.println("Error closing postgres connection: " + e.getMessage());
        }
    }

    public void addFurnitureToPostgres() {
        try {
            System.out.println("Enter the number of furniture items");
            int n = scanner.nextInt();
            System.out.println("Adding furniture to postgres database");
            for (int i = 0; i < n; i++) {
                Furniture furniture = FurnitureFactory.next();
                furniturePostgresService.addFurniture(furniture);
                System.out.println(i + 1 + " " + furniture + " added to database");
            }
        } catch (SQLException e) {
            System.err.println("The postgres database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
    }

    public List<Furniture> transferFurnitureByType() {
        List<Furniture> furnitureList = new ArrayList<>();
        try {
            System.out.println("Enter the type of furniture items");
            Type type = Type.valueOf(scanner.next().toUpperCase());
            furnitureList = furniturePostgresToMongoService.transferFurnitureByType(type);
            System.out.println("Item count with with type - " + type + " = " + furnitureList.size() + " ");
            furnitureList.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("The postgres database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
        return furnitureList;
    }

    private List<Furniture> exportFurnitureByMaterialFromMongoDB() {
        List<Furniture> furnitureList = new ArrayList<>();
        try {
            System.out.println("Enter the material of furniture items");
            String material = scanner.next();
            furnitureList = furnitureMongoService.getFurnitureByMaterial(material);
            System.out.println("Item count with material - " + material + " = " + furnitureList.size() + " ");
            furnitureList.forEach(System.out::println);
        } catch (MongoException e) {
            System.err.println("The mongo database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
        return furnitureList;
    }
}
