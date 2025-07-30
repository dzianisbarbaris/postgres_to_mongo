package by.savik.factory;

import by.savik.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkerFactory {
    private static final Random r = new Random();
    private static final List<String> names = List.of("Alex", "Misha", "Bogdan", "Valera", "Denis");

    public static Worker next() {
        String name = names.get(r.nextInt(names.size()));
        int age = r.nextInt(20, 55);
        return new Worker(name, age);
    }

    public static List<Worker> nextList(int n) {
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < n; i++){
            String name = names.get(r.nextInt(names.size()));
            int age = r.nextInt(20, 55);
            Worker worker = new Worker(name, age);
            workers.add(worker);
        }
        return workers;
    }

}
