package by.savik.service;

import by.savik.model.Furniture;
import by.savik.model.Type;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FurnitureService {

    public static void addFurniture(Connection connection, Furniture furniture) throws SQLException {
        String sql = "INSERT INTO furniture (type, material, price, color) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, furniture.getType().name());
            preparedStatement.setString(2, furniture.getMaterial());
            preparedStatement.setInt(3, furniture.getPrice());
            preparedStatement.setString(4, furniture.getColor());
            preparedStatement.executeUpdate();
        }
    }

    public static List<Furniture> getFurnitureByType(Connection connection, Type type) throws SQLException {
        List<Furniture> furnitureList = new ArrayList<>();
        String sql = "SELECT * FROM furniture WHERE type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
            }
            return furnitureList;
        }
    }

    public static void addFurnitureToMongoDB(MongoCollection<Document> collection, Furniture furniture) throws MongoException {
        Document document = new Document("id", furniture.getId())
                .append("type", furniture.getType().name())
                .append("material", furniture.getMaterial())
                .append("price", furniture.getPrice())
                .append("color", furniture.getColor());
        collection.insertOne(document);
    }

    public static List<Furniture> getItemByMaterial(MongoCollection<Document> collection, String material) throws MongoException {
        List<Furniture> furnitureList = new ArrayList<>();
        FindIterable<Document> documents = collection.find(Filters.eq("material", material));
        for (Document document : documents) {
            Furniture furniture = new Furniture(document.getInteger("id"),
                    Type.valueOf(document.getString("type")),
                    document.getString("material"),
                    document.getInteger("price"),
                    document.getString("color"));
            furnitureList.add(furniture);
        }
        if (furnitureList.isEmpty()) {
            System.out.println("Furniture item with material " + material + " not found");
        }
        return furnitureList;
    }
}
