package by.savik.config;

import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.service.FurnitureMongoService;
import by.savik.service.FurniturePostgresService;
import by.savik.service.FurniturePostgresToMongoService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FurnitureModule extends AbstractModule {


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
    }

    @Provides
    @Named("PostgresConnection")
    Connection providePostgresConnection() throws SQLException {
        String URL = "jdbc:postgresql://localhost:5432/furniture_db";
        String USER = "user";
        String PASSWORD = "1234";
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Provides
    @Named("MongoCollection")
    MongoCollection<Document> provideMongoCollection() {
        String MONGO_COLLECTION = "furniture";
        String MONGO_URL = "mongodb://admin:admin123@localhost:27017/?authSource=admin&authMechanism=SCRAM-SHA-1";
        String MONGO_DB = "furniture_db";
        MongoClient mongoClient = MongoClients.create(MONGO_URL);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
        return mongoDatabase.getCollection(MONGO_COLLECTION);
    }

}
