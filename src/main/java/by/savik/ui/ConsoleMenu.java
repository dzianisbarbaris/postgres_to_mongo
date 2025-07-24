package by.savik.ui;

import by.savik.config.FurnitureModule;
import by.savik.factory.FurnitureFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private static final Logger logger = LogManager.getLogger(ConsoleMenu.class);
    private static final Scanner scanner = new Scanner(System.in);
    private FurniturePostgresService furniturePostgresService;
    private FurnitureMongoService furnitureMongoService;
    private FurniturePostgresToMongoService transferService;
    Injector injector = Guice.createInjector(new FurnitureModule());

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
                   /* closeConnections();*/
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
            transferService = injector.getInstance(FurniturePostgresToMongoService.class);
            furniturePostgresService = injector.getInstance(FurniturePostgresService.class);
            furnitureMongoService = injector.getInstance(FurnitureMongoService.class);

        } catch (MongoException e) {
            logger.error("Failed to initialize MongoDB connection: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error during initialization: ", e);
        }
        logger.info("Database connections initialized successfully");
    }

   /* private void closeConnections() {
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
    }*/

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
            furnitureList = transferService.transferFurnitureByType(type);
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
