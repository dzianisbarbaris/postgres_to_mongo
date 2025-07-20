package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurniturePostgresToMongoRepository;
import com.mongodb.MongoException;

import java.sql.SQLException;
import java.util.List;

public class FurniturePostgresToMongoService {
    private final FurniturePostgresToMongoRepository furniturePostgresToMongoRepository;

    public FurniturePostgresToMongoService(FurniturePostgresToMongoRepository furniturePostgresToMongoRepository) {
        this.furniturePostgresToMongoRepository = furniturePostgresToMongoRepository;
    }

    public List<Furniture> transferFurnitureByType(Type type) throws SQLException, MongoException {
        return furniturePostgresToMongoRepository.transferFurnitureByType(type);
    }
}
