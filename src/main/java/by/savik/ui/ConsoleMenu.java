package by.savik.ui;

import by.savik.factory.FurnitureFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.service.FurnitureService;
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
    private FurnitureService furnitureService;

    public void start() {
        while (true) {
            mainMenu();
            System.out.print("Select an action: ");
            int action = scanner.nextInt();
            switch (action) {
                case 1:
                    addFurnitureToPostgres();
                    break;
                case 2:
                    importFurnitureToMongo(exportFurnitureByType());
                    break;
                case 3:
                    exportFurnitureByMaterialFromMongoDB();
                    break;
                case 4:
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

    public void addFurnitureToPostgres() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            FurniturePostgresRepository furniturePostgresRepository = new FurniturePostgresRepository(connection);
            furnitureService = new FurnitureService(furniturePostgresRepository);
            System.out.println("Enter the number of furniture items");
            int n = scanner.nextInt();
            System.out.println("Adding furniture to postgres database");
            for (int i = 0; i < n; i++) {
                FurnitureFactory furnitureFactory = new FurnitureFactory();
                Furniture furniture = furnitureFactory.next();
                furnitureService.addFurniture(furniture);
                System.out.println(i + 1 + " " + furniture + " added to database");
            }
        } catch (SQLException e) {
            System.err.println("The postgres database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
    }

    public List<Furniture> exportFurnitureByType() {
        List<Furniture> furnitureList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            FurniturePostgresRepository furniturePostgresRepository = new FurniturePostgresRepository(connection);
            furnitureService = new FurnitureService(furniturePostgresRepository);
            System.out.println("Enter the type of furniture items");
            Type type = Type.valueOf(scanner.next().toUpperCase());
            furnitureList = furnitureService.getFurnitureByType(type);
            System.out.println("Item count with type - " + Type.CHAIR + " = " + furnitureList.size() + " ");
        } catch (SQLException e) {
            System.err.println("The postgres database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
        return furnitureList;
    }

    public void importFurnitureToMongo(List<Furniture> furnitureList) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URL)) {
            MongoDatabase database = mongoClient.getDatabase(MONGO_DB);
            FurnitureMongoRepository furnitureMongoRepository = new FurnitureMongoRepository(database);
            furnitureService = new FurnitureService(furnitureMongoRepository);
            for (Furniture furniture : furnitureList) {
                furnitureService.addFurnitureToMongoDB(furniture);
                System.out.println("Item " + furniture + " added to database");
            }
        } catch (MongoException e) {
            System.err.println("The mongo database could not be accessed : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
    }

    private List<Furniture> exportFurnitureByMaterialFromMongoDB() {
        List<Furniture> furnitureList = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(MONGO_URL)) {
            MongoDatabase database = mongoClient.getDatabase(MONGO_DB);
            FurnitureMongoRepository furnitureMongoRepository = new FurnitureMongoRepository(database);
            furnitureService = new FurnitureService(furnitureMongoRepository);
            System.out.println("Enter the material of furniture items");
            String material = scanner.next();
            furnitureList = furnitureService.getFurnitureByMaterial(material);
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
