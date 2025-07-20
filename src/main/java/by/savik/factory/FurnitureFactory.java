package by.savik.factory;

import by.savik.model.Furniture;
import by.savik.model.Type;

import java.util.List;
import java.util.Random;

public class FurnitureFactory {
    private static final Random random = new Random();
    private static final List<String> materials = List.of("wood", "steel", "plastics", "stone", "glass");
    private static final List<String> colors = List.of("black", "red", "white", "green", "blue");

    public FurnitureFactory() {
    }

    public static Furniture next() {
        String material = materials.get(random.nextInt(materials.size()));
        String color = colors.get(random.nextInt(colors.size()));
        Type type = Type.randomType();
        int price = random.nextInt(1000, 10000);
        return new Furniture(type, material, price, color);
    }
}
