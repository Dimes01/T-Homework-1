package utilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class PostgresContainer {

    private static PostgreSQLContainer<?> postgresContainer;

    @Value("{spring.datasource.url}")
    private static String databaseName;

    @Value("{spring.datasource.username}")
    private static String username;

    @Value("{spring.datasource.password}")
    private static String password;

    @BeforeAll
    public static void startContainer() {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName(databaseName)
                .withUsername(username)
                .withPassword(password);
        postgresContainer.start();
    }

    @DynamicPropertySource
    private static void properties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    }

    @AfterAll
    public static void stopContainer() {
        postgresContainer.stop();
    }
}
