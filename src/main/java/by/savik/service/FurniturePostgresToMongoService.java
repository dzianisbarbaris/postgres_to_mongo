package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurnitureMongoRepository;
import by.savik.repository.FurniturePostgresRepository;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class FurniturePostgresToMongoService {
    private static final Logger logger = LogManager.getLogger(FurniturePostgresToMongoService.class);
    private final FurniturePostgresRepository furniturePostgresRepository;
    private final FurnitureMongoRepository furnitureMongoRepository;

    @Inject
    public FurniturePostgresToMongoService(FurniturePostgresRepository furniturePostgresRepository, FurnitureMongoRepository furnitureMongoRepository) {
        this.furniturePostgresRepository = furniturePostgresRepository;
        this.furnitureMongoRepository = furnitureMongoRepository;
    }

    public List<Furniture> transferFurnitureByType(Type type) throws SQLException, MongoException {
        List<Furniture> furnitureList = furniturePostgresRepository.getFurnitureByType(type);
        if (!furnitureList.isEmpty()){
            furnitureMongoRepository.importAllFurniture(furnitureList);
        }
        return furnitureList;
    }
}
