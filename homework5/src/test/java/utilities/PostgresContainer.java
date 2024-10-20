package utilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class PostgresContainer {

    private static PostgreSQLContainer<?> postgresContainer;

    @BeforeAll
    static void startContainer() {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgresContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        postgresContainer.stop();
    }

    public static String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    public static String getUsername() {
        return postgresContainer.getUsername();
    }

    public static String getPassword() {
        return postgresContainer.getPassword();
    }
}
