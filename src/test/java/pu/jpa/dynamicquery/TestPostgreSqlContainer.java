package pu.jpa.dynamicquery;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * @author Plamen Uzunov
 */
public class TestPostgreSqlContainer extends PostgreSQLContainer<TestPostgreSqlContainer> {

    public static final String IMAGE_VERSION = "postgres:16-alpine";
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
                .withCopyFileToContainer(
                    MountableFile.forClasspathResource(
                        "init-db.sql"), "/docker-entrypoint-initdb.d/"
                )
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
