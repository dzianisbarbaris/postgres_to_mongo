package by.savik.ui;

import by.savik.config.FurnitureModule;
import by.savik.factory.FurnitureFactory;
import by.savik.factory.WorkerFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.model.Worker;
import by.savik.service.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
    private static final Logger logger = LogManager.getLogger(ConsoleMenu.class);
    private static final Scanner scanner = new Scanner(System.in);
    private FurniturePostgresService furniturePostgresService;
    private FurnitureMongoService furnitureMongoService;
    private FurniturePostgresToMongoService transferService;
    private WorkerPostgresService workerPostgresService;
    private FurnitureStreamService furnitureStreamService;
    Injector injector = Guice.createInjector(new FurnitureModule());


    /*public void startWorkersMenu() {
        initializeConnections();
        while (true) {
            try {
                mainWorkerMenu();
                logger.info("Select an action");
                int action = scanner.nextInt();
                switch (action) {
                    case 1:
                        addWorkerToPostgres();
                        break;
                    case 2:
                        addAllWorkersToPostgres();
                        break;
                    case 3:
                        exportWorkersByName();
                        break;
                    case 4:
                        updateWorkerAgeById();
                        break;
                    case 5:
                        deleteWorkerById();
                        break;
                    case 6:
                        return;
                }
            } catch (SQLException e) {
                logger.error("The postgres database could not be accessed : ", e);
            } catch (Exception e) {
                logger.error("Unexpected error : ", e);
            }
        }
    }*/

    private void initializeConnections() {
        try {
            transferService = injector.getInstance(FurniturePostgresToMongoService.class);
            furniturePostgresService = injector.getInstance(FurniturePostgresService.class);
            furnitureMongoService = injector.getInstance(FurnitureMongoService.class);
            furnitureStreamService = injector.getInstance(FurnitureStreamService.class);
            workerPostgresService = injector.getInstance(WorkerPostgresService.class);
        } catch (MongoException e) {
            logger.error("Failed to initialize MongoDB connection: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error during initialization: ", e);
        }
        logger.info("Database connections initialized successfully");
    }

    /*public void mainWorkerMenu() {
        System.out.println("==== Worker Manager ====");
        System.out.println("1. Add worker to postgres database.");
        System.out.println("2. Add List of workers to postgres database.");
        System.out.println("3. Export workers by name from postgres database.");
        System.out.println("4. Update workers age by ID.");
        System.out.println("5. Delete worker by ID.");
        System.out.println("6. Exit.");
    }

    public void addWorkerToPostgres() throws SQLException {
        logger.info("Enter the name of worker");
        String name = scanner.next();
        logger.info("Enter the age of worker");
        int age = scanner.nextInt();
        workerPostgresService.addWorker(new Worker(name, age));
    }

    public void addAllWorkersToPostgres() throws SQLException {
        logger.info("Enter the number of workers");
        int number = scanner.nextInt();
        workerPostgresService.addAllWorkers(WorkerFactory.nextList(number));
    }

    public List<Worker> exportWorkersByName() throws SQLException {
        List<Worker> workersList = new ArrayList<>();
        logger.info("Enter the name of the worker");
        String name = scanner.next();
        workersList = workerPostgresService.getWorkersByName(name);
        workersList.forEach(logger::info);
        return workersList;
    }

    public void updateWorkerAgeById() throws SQLException {
        logger.info("Enter the workers ID");
        int id = scanner.nextInt();
        logger.info("Enter the new age");
        int age = scanner.nextInt();
        workerPostgresService.updateWorkerAgeById(id, age);
    }

    public void deleteWorkerById() throws SQLException {
        logger.info("Enter the workers ID");
        int id = scanner.nextInt();
        workerPostgresService.deleteWorkerById(id);
    }


    public void startFurnitureMenu() {
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
                    *//* closeConnections();*//*
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
            for (int i = 0; i < n; i++) {
                Furniture furniture = FurnitureFactory.next();
                furniturePostgresService.addFurniture(furniture);
                logger.info(i + 1 + " " + furniture + " added to database");
            }
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
    }*/


    public void startFurnitureStreamMenu() {
        initializeConnections();
        while (true) {
            try {
                mainFurnitureStreamMenu();
                logger.info("Select an action: ");
                int action = scanner.nextInt();
                switch (action) {
                    case 1:
                        List<Furniture> furnitureByType = furnitureStreamService.filterFurnitureByType(Type.CHAIR);
                        furnitureByType.forEach(logger::info);
                        break;
                    case 2:
                        List<Furniture> furnitureByColor = furnitureStreamService.filterFurnitureByColor("red");
                        furnitureByColor.forEach(logger::info);
                        break;
                    case 3:
                        long countByAge = furnitureStreamService.countWorkersByAge(22);
                        logger.info(countByAge);
                        break;
                    case 4:
                        Integer maxPrice = furnitureStreamService.getFurnitureMaxPrice();
                        logger.info(maxPrice);
                        break;
                    case 5:
                        List<String> namesList = furnitureStreamService.gerWorkersNames();
                        namesList.forEach(logger::info);
                        break;
                    case 6:
                        Map<Type, Long> mapFurnitureByType = furnitureStreamService.countFurnitureByType();
                        logger.info(mapFurnitureByType);
                        break;
                    case 7:
                        Map<String, Double> mapPriceByMaterial = furnitureStreamService.avgPriceByMaterial();
                        logger.info(mapPriceByMaterial);
                        break;
                    case 8:
                        List<Worker> workersByAgeAndName = furnitureStreamService.sortWorkersByAgeAndName();
                        workersByAgeAndName.forEach(logger::info);
                        break;
                    case 9:
                        Map<String, Integer> mapPriceByColor = furnitureStreamService.totalPriceByColor();
                        logger.info(mapPriceByColor);
                        break;
                    case 10:
                        return;
                }
            } catch (SQLException e) {
                logger.error("The postgres database could not be accessed : ", e);
            } catch (Exception e) {
                logger.error("Unexpected error : ", e);
            }
        }
    }

    public void mainFurnitureStreamMenu() {
        System.out.println("==== Furniture Stream Manager ====");
        System.out.println("1. Filter furniture by type Chair.");
        System.out.println("2. Filter furniture by color.");
        System.out.println("3. Young workers count.");
        System.out.println("4. Get furniture max price.");
        System.out.println("5. Get List of workers names.");
        System.out.println("6. Count furniture by type.");
        System.out.println("7. Get average price by material.");
        System.out.println("8. Sort workers by age and name.");
        System.out.println("9. Get furniture total price by color.");
        System.out.println("10. Exit.");
    }

}
