package by.savik.model;

public class Worker {
    private int id;
    private final String name;
    private final int age;

    public Worker(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public Worker(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
