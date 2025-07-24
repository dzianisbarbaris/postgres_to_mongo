package by.savik.service;

import by.savik.model.Furniture;
import by.savik.repository.FurnitureMongoRepository;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

public class FurnitureMongoService {
    private static final Logger logger = LogManager.getLogger(FurnitureMongoService.class);
    private final FurnitureMongoRepository furnitureMongoRepository;

    @Inject
    public FurnitureMongoService(FurnitureMongoRepository furnitureMongoRepository) {
        this.furnitureMongoRepository = furnitureMongoRepository;
    }

    public void importFurniture(List<Furniture> furnitureList) throws MongoException {
        furnitureMongoRepository.importFurniture(furnitureList);
    }

    public List<Furniture> getFurnitureByMaterial(String material) throws MongoException {
        return furnitureMongoRepository.getFurnitureByMaterial(material);
    }
}
