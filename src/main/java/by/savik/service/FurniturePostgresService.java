package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurniturePostgresRepository;
import com.mongodb.MongoException;

import java.sql.SQLException;
import java.util.List;

public class FurniturePostgresService {

    private final FurniturePostgresRepository furniturePostgresRepository;

    public FurniturePostgresService(FurniturePostgresRepository furniturePostgresRepository) {
        this.furniturePostgresRepository = furniturePostgresRepository;
    }

    public void addFurniture(Furniture furniture) throws SQLException {
        furniturePostgresRepository.addFurniture(furniture);
    }

    public List<Furniture> getFurnitureByType(Type type) throws SQLException {
        return furniturePostgresRepository.getFurnitureByType(type);
    }
}
