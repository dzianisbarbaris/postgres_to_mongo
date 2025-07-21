package by.savik.repository;

import by.savik.model.Furniture;
import by.savik.model.Type;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class FurnitureMongoRepository {
    private static final String MONGO_COLLECTION = "furniture";
    private final MongoDatabase database;
    private MongoCollection<Document> collection;

    public FurnitureMongoRepository(MongoDatabase database) {
        this.database = database;
        initCollection();
    }

    private void initCollection() {
        collection = database.getCollection(MONGO_COLLECTION);
    }

    public Document furnitureToDocument(Furniture furniture) {
        return new Document("id", furniture.getId())
                .append("type", furniture.getType().name())
                .append("material", furniture.getMaterial())
                .append("price", furniture.getPrice())
                .append("color", furniture.getColor());
    }

    public void importFurniture(List<Furniture> furnitureList) throws MongoException {
        for (Furniture furniture : furnitureList) {
            Document document = new Document("id", furniture.getId())
                    .append("type", furniture.getType().name())
                    .append("material", furniture.getMaterial())
                    .append("price", furniture.getPrice())
                    .append("color", furniture.getColor());
            collection.insertOne(document);
        }
    }

    public void importAllFurniture(List<Furniture> furnitureList) throws MongoException {
        List<Document> documents = new ArrayList<>();
        for (Furniture furniture : furnitureList) {
            documents.add(furnitureToDocument(furniture));
        }
    }

    public List<Furniture> getFurnitureByMaterial(String material) throws MongoException {
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
