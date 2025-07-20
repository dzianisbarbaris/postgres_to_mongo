package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurniturePostgresRepository;

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
}
