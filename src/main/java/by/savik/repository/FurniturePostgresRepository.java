package by.savik.repository;

import by.savik.model.Furniture;

import java.sql.*;

public class FurniturePostgresRepository {
    private final Connection connection;

    public FurniturePostgresRepository(Connection connection) {
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
}

