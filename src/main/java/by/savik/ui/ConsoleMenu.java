package by.savik.ui;

import by.savik.config.FurnitureModule;
import by.savik.config.WorkerModule;
import by.savik.factory.FurnitureFactory;
import by.savik.factory.WorkerFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.model.Worker;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import by.savik.service.WorkerPostgresService;
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
    private WorkerPostgresService workerPostgresService;
    Injector injector = Guice.createInjector(new FurnitureModule());
    Injector workerInjector = Guice.createInjector(new WorkerModule());


    public void startWorkersMenu() {
        initializeConnectionsToWorkerDatabase();
        while (true) {
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
        }
    }
    public void mainWorkerMenu() {
        System.out.println("==== Worker Manager ====");
        System.out.println("1. Add worker to postgres database.");
        System.out.println("2. Add List of workers to postgres database.");
        System.out.println("3. Export workers by name from postgres database.");
        System.out.println("4. Update workers age by ID.");
        System.out.println("5. Delete worker by ID.");
        System.out.println("6. Exit.");
    }

    private void initializeConnectionsToWorkerDatabase() {
        try {
            workerPostgresService = workerInjector.getInstance(WorkerPostgresService.class);
        }  catch (Exception e) {
            logger.error("Unexpected error during initialization: ", e);
        }
        logger.info("Database connections initialized successfully");
    }

    public void addWorkerToPostgres() {
        try {
            logger.info("Enter the name of worker");
            String name = scanner.next();
            logger.info("Enter the age of worker");
            int age = scanner.nextInt();
            workerPostgresService.addWorker(new Worker(name, age));
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
    }

    public void addAllWorkersToPostgres() {
        List<Worker> workers = new ArrayList<>();
        try {
            logger.info("Enter the number of workers");
            int number = scanner.nextInt();
            workerPostgresService.addAllWorkers(WorkerFactory.nextList(number));
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
    }

    public List<Worker> exportWorkersByName() {
        List<Worker> workersList = new ArrayList<>();
        try {
            logger.info("Enter the name of worker");
            String name = scanner.next();
            workersList = workerPostgresService.getWorkersByName(name);
            workersList.forEach(logger::info);
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
        return workersList;
    }

    public void updateWorkerAgeById() {
        try {
            logger.info("Enter the workers ID");
            int id = scanner.nextInt();
            logger.info("Enter the new age");
            int age = scanner.nextInt();
            workerPostgresService.updateWorkerAgeById(id, age);
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
    }

    public void deleteWorkerById() {
        try {
            logger.info("Enter the workers ID");
            int id = scanner.nextInt();
            workerPostgresService.deleteWorkerById(id);
        } catch (SQLException e) {
            logger.error("The postgres database could not be accessed : ", e);
        } catch (Exception e) {
            logger.error("Unexpected error : ", e);
        }
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
                    /* closeConnections();*/
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
