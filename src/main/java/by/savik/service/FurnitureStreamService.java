package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.model.Worker;
import by.savik.repository.FurniturePostgresRepository;
import by.savik.repository.WorkerPostgresRepository;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class FurnitureStreamService {
    private static final Logger logger = LogManager.getLogger(FurnitureStreamService.class);
    private final FurniturePostgresRepository furniturePostgresRepository;
    private final WorkerPostgresRepository workerPostgresRepository;

    @Inject
    public FurnitureStreamService(FurniturePostgresRepository furniturePostgresRepository, WorkerPostgresRepository workerPostgresRepository) {
        this.furniturePostgresRepository = furniturePostgresRepository;
        this.workerPostgresRepository = workerPostgresRepository;
    }

    public List<Furniture> filterFurnitureByType(Type type) throws SQLException {
        return furniturePostgresRepository.getAllFurniture()
                .stream().filter(furniture -> furniture.getType().equals(type)).toList();
    }

    public List<Furniture> filterFurnitureByColor(String color) throws SQLException {
        return furniturePostgresRepository.getAllFurniture().stream()
                .filter(furniture -> furniture.getColor().equalsIgnoreCase(color)).toList();
    }

    public Integer getFurnitureMaxPrice() throws SQLException {
        OptionalInt optional = furniturePostgresRepository.getAllFurniture().stream()
                .mapToInt(Furniture::getPrice).max();
        if (optional.isEmpty()){
            return null;
        };
        return optional.getAsInt();
    }

    public Map<Type, Long> countFurnitureByType() throws SQLException {
        return furniturePostgresRepository.getAllFurniture().stream()
                .collect(Collectors.groupingBy(Furniture::getType, Collectors.counting()));
    }

    public Map<String, Double> avgPriceByMaterial() throws SQLException {
        return furniturePostgresRepository.getAllFurniture().stream()
                .collect(Collectors.groupingBy(Furniture::getMaterial, Collectors.averagingDouble(Furniture::getPrice)));
    }

    public Map<String, Integer> totalPriceByColor() throws SQLException {
        return furniturePostgresRepository.getAllFurniture().stream()
                .collect(Collectors.groupingBy(Furniture::getColor, Collectors.summingInt(Furniture::getPrice)));
    }


    public long countWorkersByAge(int age) throws SQLException{
        return workerPostgresRepository.getAllWorkers().stream()
                .filter(worker -> worker.getAge() < age).count();
    }

    public List<String> gerWorkersNames() throws SQLException {
        return workerPostgresRepository.getAllWorkers().stream()
                .map(Worker::getName).toList();
    }

    public List<Worker> sortWorkersByAgeAndName() throws SQLException {
        return workerPostgresRepository.getAllWorkers().stream()
                .sorted(Comparator.comparing(Worker::getAge))
                .sorted(Comparator.comparing(Worker::getName))
                .toList();
    }


}
