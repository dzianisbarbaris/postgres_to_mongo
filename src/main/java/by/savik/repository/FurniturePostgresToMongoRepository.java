package by.savik.repository;

import by.savik.model.Furniture;
import by.savik.model.Type;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FurniturePostgresToMongoRepository {
    private static final String MONGO_COLLECTION = "furniture";
    private final Connection connection;
    private final MongoDatabase database;
    private MongoCollection<Document> collection;

    public FurniturePostgresToMongoRepository(Connection connection, MongoDatabase database) {
        this.connection = connection;
        this.database = database;
        initCollection();
    }

    private void initCollection() {
        collection = database.getCollection(MONGO_COLLECTION);
    }

    public List<Furniture> transferFurnitureByType(Type type) throws SQLException, MongoException {
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
        for (Furniture furniture : furnitureList) {
            Document document = new Document("id", furniture.getId())
                    .append("type", furniture.getType().name())
                    .append("material", furniture.getMaterial())
                    .append("price", furniture.getPrice())
                    .append("color", furniture.getColor());
            collection.insertOne(document);
        }
        return furnitureList;
    }
}
