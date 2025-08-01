package by.savik.config;

import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import by.savik.service.WorkerPostgresService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class FurnitureModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger(FurnitureModule.class);
    private final Properties properties;

    public FurnitureModule() {
        this.properties = loadProperties();
    }
//с интерфейсом
    /*@Override
    protected void configure() {
        bind(FurnitureRepository.class)
                .annotatedWith(Names.named("Postgres")
                .to(FurniturePostgresRepository.class);

        bind(FurnitureRepository.class)
                .annotatedWith(Names.named("Mongo")
                .to(FurnitureMongoRepository.class);
    }*/


    // без интерфейса
    @Override
    protected void configure() {
        bind(FurnitureMongoRepository.class);
        bind(FurniturePostgresRepository.class);
        bind(FurnitureMongoService.class);
        bind(FurniturePostgresService.class);
        bind(FurniturePostgresToMongoService.class);
        bind(WorkerPostgresService.class);
    }

    @Provides
    @Named("PostgresConnection")
    Connection providePostgresConnection() throws SQLException {
        String URL = properties.getProperty("postgres.url");
        String USER = properties.getProperty("postgres.user");
        String PASSWORD = properties.getProperty("postgres.password");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Provides
    @Named("PostgresWorkerConnection")
    Connection providePostgresWorkerConnection() throws SQLException {
        String URL = properties.getProperty("postgres.worker.url");
        String USER = properties.getProperty("postgres.user");
        String PASSWORD = properties.getProperty("postgres.password");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Provides
    @Named("MongoCollection")
    MongoCollection<Document> provideMongoCollection() {
        String MONGO_COLLECTION = properties.getProperty("mongo.collection");
        String MONGO_URL = properties.getProperty("mongo.url");
        String MONGO_DB = properties.getProperty("mongo.dbname");
        MongoClient mongoClient = MongoClients.create(MONGO_URL);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
        return mongoDatabase.getCollection(MONGO_COLLECTION);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Не удалось найти файл application.properties");
                throw new RuntimeException("Файл application.properties не найден");
            }
            props.load(input);
            logger.info("Файл application.properties успешно загружен");
        }
        catch (IOException e) {
            logger.error("Ошибка при загрузке файла application.properties");
            throw new RuntimeException("Ошибка при загрузке файла application.properties");
        }
        return props;
    }

}
