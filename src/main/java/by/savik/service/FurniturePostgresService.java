package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.repository.FurniturePostgresRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class FurniturePostgresService {
    private static final Logger logger = LogManager.getLogger(FurniturePostgresService.class);
    private final FurniturePostgresRepository furniturePostgresRepository;

    @Inject
    public FurniturePostgresService(FurniturePostgresRepository furniturePostgresRepository) {
        this.furniturePostgresRepository = furniturePostgresRepository;
    }


    public void addAllFurniture (List<Furniture> furnitureList) throws SQLException {
        furniturePostgresRepository.addAllFurniture(furnitureList);
    }

    public void addFurniture(Furniture furniture) throws SQLException {
        furniturePostgresRepository.addFurniture(furniture);
    }
}
