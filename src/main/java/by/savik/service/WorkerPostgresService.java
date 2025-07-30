package by.savik.service;

import by.savik.model.Worker;
import by.savik.repository.WorkerPostgresRepository;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class WorkerPostgresService {
    private static final Logger logger = LogManager.getLogger(WorkerPostgresService.class);
    private final WorkerPostgresRepository workerPostgresRepository;

    @Inject
    public WorkerPostgresService(WorkerPostgresRepository workerPostgresRepository) {
        this.workerPostgresRepository = workerPostgresRepository;
    }

    public void addWorker (Worker worker) throws SQLException {
        workerPostgresRepository.addWorker(worker);
    }

    public void addAllWorkers(List<Worker> workers) throws SQLException {
        workerPostgresRepository.addAllWorkers(workers);
    }

    public List<Worker> getWorkersByName(String name) throws SQLException {
        return workerPostgresRepository.getWorkersByName(name);
    }

    public void updateWorkerAgeById(int id, int age) throws SQLException {
        workerPostgresRepository.updateWorkerAge(id, age);
    }

    public void deleteWorkerById(int id) throws SQLException {
        workerPostgresRepository.deleteWorkerById(id);
    }
}
