package by.savik.manager;

import by.savik.factory.FurnitureFactory;
import by.savik.model.Furniture;
import by.savik.model.Type;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FurnitureManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/furniture_db";
    private static final String USER = "user";
    private static final String PASSWORD = "1234";


    private static final String MONGO_URL = "mongodb://admin:admin123@localhost:27017/?authSource=admin&authMechanism=SCRAM-SHA-1";
    private static final String MONGO_DB = "furniture_db";
    private static final String MONGO_COLLECTION = "furniture";


    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD); MongoClient mongoClient = MongoClients.create(MONGO_URL)) {

            System.out.println("Connection to the databases is successful");


            System.out.println("Adding furniture to postgres database");

            for (int i = 0; i < 5000; i++) {
                Furniture furniture = FurnitureFactory.next();
                addFurniture(connection, furniture);
                System.out.println(i + 1 + " " + furniture + " added to database");
            }

            System.out.println("Exporting furniture by type");
            List<Furniture> furnitureListByType = getFurnitureByType(connection, Type.CHAIR);
            System.out.println("Item count with type - " + Type.CHAIR + " = " + furnitureListByType.size() + " ");
            furnitureListByType.forEach(System.out::println);

            MongoDatabase database = mongoClient.getDatabase(MONGO_DB);
            MongoCollection<Document> collection = database.getCollection(MONGO_COLLECTION);
            System.out.println("Importing furniture list to MongoDB");
            for (Furniture furniture : furnitureListByType) {
                addFurnitureToMongoDB(collection, furniture);
                System.out.println("Item " + furniture + " added to database");
            }

        } catch (SQLException e) {
            System.err.println("The database could not be accessed : " + e.getMessage());
        } catch (MongoException e) {
            System.err.println("MongoDB Error : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error : " + e.getMessage());
        }
    }


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

    private static void addFurnitureToMongoDB(MongoCollection<Document> collection, Furniture furniture) throws MongoException {
        Document document = new Document("id", furniture.getId())
                .append("type", furniture.getType().name())
                .append("material", furniture.getMaterial())
                .append("price", furniture.getPrice())
                .append("color", furniture.getColor());
        collection.insertOne(document);
    }

    private static List<Furniture> getItemByMaterial(MongoCollection<Document> collection, String material) throws MongoException {
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
