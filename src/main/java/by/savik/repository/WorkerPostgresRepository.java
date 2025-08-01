package by.savik.repository;

import by.savik.model.Furniture;
import by.savik.model.Type;
import by.savik.model.Worker;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkerPostgresRepository {
    private final Connection connection;

    @Inject
    public WorkerPostgresRepository(@Named("PostgresWorkerConnection") Connection connection) {
        this.connection = connection;
    }

    public void addWorker(Worker worker) throws SQLException {
        String sql = "INSERT INTO workers (name, age) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, worker.getName());
        preparedStatement.setInt(2, worker.getAge());
        preparedStatement.executeUpdate();
    }

    public void addAllWorkers(List<Worker> workerList) throws SQLException {
        String sql = "INSERT INTO workers (name, age) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Worker worker : workerList) {
            preparedStatement.setString(1, worker.getName());
            preparedStatement.setInt(2, worker.getAge());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    public Worker mapResultSetToWorker(ResultSet resultSet) throws SQLException {
        return new Worker(resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"));
    }

    public List<Worker> getAllWorkers() throws SQLException {
        List<Worker> workerList = new ArrayList<>();
        String sql = "SELECT * FROM workers";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    workerList.add(mapResultSetToWorker(resultSet));
                }
            }
        }
        if (workerList.isEmpty()) {
            System.out.println("Workers not found");
            return null;
        }
        return workerList;
    }

    public List<Worker> getWorkersByName(String name) throws SQLException {
        List<Worker> workerList = new ArrayList<>();
        String sql = "SELECT * FROM workers WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    workerList.add(mapResultSetToWorker(resultSet));
                }
            }
        }
        if (workerList.isEmpty()) {
            System.out.println("Workers with name " + name + " not found");
            return null;
        }
        return workerList;
    }

    public void updateWorkerAge(int id, int age) throws SQLException {
        String sql = "UPDATE workers SET age = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, age);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        }
    }

    public void deleteWorkerById(int id) throws SQLException {
        String sql = "DELETE FROM workers WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        }
    }
}
