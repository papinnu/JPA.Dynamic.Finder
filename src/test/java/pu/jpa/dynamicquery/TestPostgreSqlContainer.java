package pu.jpa.dynamicquery;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * @author Plamen Uzunov
 */
public class TestPostgreSqlContainer extends PostgreSQLContainer<TestPostgreSqlContainer> {
    public static final String IMAGE_VERSION = "postgres:16-alpine";
    public static final int POSTGRES_PORT = 54321;
    private static TestPostgreSqlContainer container;

    private TestPostgreSqlContainer(String dockerImageName) {
        super(dockerImageName);
    }

    /**
     * Returns a singleton {@code TestPostgreSqlContainer} instance.
     *
     * @return - singleton instance.
     */
    public static TestPostgreSqlContainer getInstance() {
        if (container == null) {
            container = new TestPostgreSqlContainer(IMAGE_VERSION)
//                .withExposedPorts(5432, POSTGRES_PORT)
//                .withDatabaseName("jpadynamicqb")
//                .withUsername("test")
//                .withPassword("1234567890")
//                .withEnv("PGPASSWORD", "1234567890")
            ;
            container.start();
        }

        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
    }

}
