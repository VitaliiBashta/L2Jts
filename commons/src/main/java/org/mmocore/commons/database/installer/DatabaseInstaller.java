package org.mmocore.commons.database.installer;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.file.Path;

/**
 * @author Java-man
 */
public final class DatabaseInstaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInstaller.class);

    private DatabaseInstaller() {
    }

    public static void start(final DataSource dataSource, final Path databaseFilesDir) {
        LOGGER.info("Installing tables in database.");

        final Flyway flyway = new Flyway();
        flyway.setValidateOnMigrate(false); //TODO[Hack]: У клиента возник косяк с валидацией. Протестировать установку.
        flyway.setDataSource(dataSource);
        flyway.setLocations("filesystem:" + databaseFilesDir);
        flyway.migrate();
    }
}
