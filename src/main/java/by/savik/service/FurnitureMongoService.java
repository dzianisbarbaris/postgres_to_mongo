package by.savik.service;

import by.savik.model.Furniture;
import by.savik.repository.FurnitureMongoRepository;
import com.mongodb.MongoException;

import java.util.List;

public class FurnitureMongoService {
    private final FurnitureMongoRepository furnitureMongoRepository;

    public FurnitureMongoService(FurnitureMongoRepository furnitureMongoRepository) {
        this.furnitureMongoRepository = furnitureMongoRepository;
    }

    public void addFurnitureToMongoDB(Furniture furniture) throws MongoException {
        furnitureMongoRepository.addFurnitureToMongoDB(furniture);
    }

    public List<Furniture> getItemByMaterial(String material) throws MongoException {
        return furnitureMongoRepository.getItemByMaterial(material);
    }
}
