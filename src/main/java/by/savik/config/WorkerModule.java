package by.savik.config;

import by.savik.repository.WorkerPostgresRepository;
import by.savik.service.WorkerPostgresService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class WorkerModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger(WorkerModule.class);
    private final Properties properties;

    public WorkerModule() {
        this.properties = loadProperties();
    }

    @Override
    protected void configure() {
        bind(WorkerPostgresRepository.class);
        bind(WorkerPostgresService.class);
    }

    @Provides
    @Named("PostgresWorkerDataBaseConnection")
    Connection providePostgresConnection() throws SQLException {
        String URL = properties.getProperty("postgres.worker.url");
        String USER = properties.getProperty("postgres.user");
        String PASSWORD = properties.getProperty("postgres.password");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Не удалось найти файл application.properties");
                throw new RuntimeException("Файл application.properties не найден");
            }
            props.load(input);
            logger.info("Файл application.properties успешно загружен");
        }
        catch (IOException e) {
            logger.error("Ошибка при загрузке файла application.properties");
            throw new RuntimeException("Ошибка при загрузке файла application.properties");
        }
        return props;
    }
}
