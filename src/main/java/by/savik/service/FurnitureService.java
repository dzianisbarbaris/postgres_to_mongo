package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import com.mongodb.MongoException;

import java.sql.SQLException;
import java.util.List;

public class FurnitureService {
    private FurnitureMongoRepository furnitureMongoRepository;
    private FurniturePostgresRepository furniturePostgresRepository;

    public FurnitureService(FurnitureMongoRepository furnitureMongoRepository) {
        this.furnitureMongoRepository = furnitureMongoRepository;
    }

    public FurnitureService(FurniturePostgresRepository furniturePostgresRepository) {
        this.furniturePostgresRepository = furniturePostgresRepository;
    }

    public void addFurniture(Furniture furniture) throws SQLException {
        furniturePostgresRepository.addFurniture(furniture);
    }

    public List<Furniture> getFurnitureByType(Type type) throws SQLException {
        return furniturePostgresRepository.getFurnitureByType(type);
    }

    public void addFurnitureToMongoDB(Furniture furniture) throws MongoException {
        furnitureMongoRepository.addFurnitureToMongoDB(furniture);
    }

    public List<Furniture> getFurnitureByMaterial(String material) throws MongoException {
        return furnitureMongoRepository.getFurnitureByMaterial(material);
    }
}
