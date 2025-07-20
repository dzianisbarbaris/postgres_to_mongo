package by.savik.model;

public class Furniture {
    private int id;
    private final Type type;
    private final String material;
    private final int price;
    private final String color;

    public Furniture(int id, Type type, String material, int price, String color) {
        this.id = id;
        this.type = type;
        this.material = material;
        this.price = price;
        this.color = color;
    }

    public Furniture(Type type, String material, int price, String color) {
        this.type = type;
        this.material = material;
        this.price = price;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getMaterial() {
        return material;
    }

    public int getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Furniture item: (type = " + type + ", material = " + material + ", price = " + price + " RUB, color = " + color + ") ";
    }
}
