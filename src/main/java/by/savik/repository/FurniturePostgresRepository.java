package by.savik.repository;

import by.savik.model.Furniture;
import by.savik.model.Type;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FurniturePostgresRepository {
    private final Connection connection;

    @Inject
    public FurniturePostgresRepository(@Named("PostgresConnection") Connection connection) {
        this.connection = connection;
    }

    public void addFurniture(Furniture furniture) throws SQLException {
        String sql = "INSERT INTO furniture (type, material, price, color) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, furniture.getType().name());
        preparedStatement.setString(2, furniture.getMaterial());
        preparedStatement.setInt(3, furniture.getPrice());
        preparedStatement.setString(4, furniture.getColor());
        preparedStatement.executeUpdate();
    }

    public void addAllFurniture(List<Furniture> furnitureList) throws SQLException {
        String sql = "INSERT INTO furniture (type, material, price, color) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Furniture furniture : furnitureList) {
            preparedStatement.setString(1, furniture.getType().name());
            preparedStatement.setString(2, furniture.getMaterial());
            preparedStatement.setInt(3, furniture.getPrice());
            preparedStatement.setString(4, furniture.getColor());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    public List<Furniture> getFurnitureByType(Type type) throws SQLException {
        List<Furniture> furnitureList = new ArrayList<>();
        String sql = "SELECT * FROM furniture WHERE type = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, type.name());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Furniture furniture = new Furniture(resultSet.getInt("id"),
                    Type.valueOf(resultSet.getString("type")),
                    resultSet.getString("material"),
                    resultSet.getInt("price"),
                    resultSet.getString("color"));
            furnitureList.add(furniture);
        }
        if (furnitureList.isEmpty()) {
            System.out.println("Furniture item type " + type + " not found");
            return null;
        }
        return furnitureList;
    }

    public Furniture mapResultSetToFurniture(ResultSet resultSet) throws SQLException {
        return new Furniture(resultSet.getInt("id"),
                Type.valueOf(resultSet.getString("type")),
                resultSet.getString("material"),
                resultSet.getInt("price"),
                resultSet.getString("color"));
    }

    public List<Furniture> getFurnitureByMaterial(String material) throws SQLException {
        List<Furniture> furnitureList = new ArrayList<>();
        String sql = "SELECT * FROM furniture WHERE material = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, material);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    furnitureList.add(mapResultSetToFurniture(resultSet));
                }
            }
        }
        return furnitureList;
    }

    public List<Furniture> getAllFurniture() throws SQLException {
        List<Furniture> furnitureList = new ArrayList<>();
        String sql = "SELECT * FROM furniture";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    furnitureList.add(mapResultSetToFurniture(resultSet));
                }
            }
        }
        return furnitureList;
    }
}

