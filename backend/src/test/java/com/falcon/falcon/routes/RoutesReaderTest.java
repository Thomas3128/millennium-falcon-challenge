package com.falcon.falcon.routes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RoutesReaderTest {

    private static final Path EXAMPLE1_DB = Path.of("../examples/example1/universe.db");

    @Test
    void reads_every_route_of_the_example_universe() {
        RoutesReader reader = new RoutesReader(EXAMPLE1_DB);

        assertThat(reader.findAll()).containsExactly(
                new Route("Tatooine", "Dagobah", 6),
                new Route("Dagobah", "Endor", 4),
                new Route("Dagobah", "Hoth", 1),
                new Route("Hoth", "Endor", 1),
                new Route("Tatooine", "Hoth", 6));
    }

    @Test
    void rejects_a_database_that_does_not_exist() {
        assertThatThrownBy(() -> new RoutesReader(Path.of("this_database_does_not_exist.db")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void wraps_a_jdbc_failure_when_the_file_is_not_a_database(@TempDir Path tmp) throws IOException {
        Path notADatabase = tmp.resolve("universe.db");
        Files.writeString(notADatabase, "this is plain text, not a SQLite database");

        RoutesReader reader = new RoutesReader(notADatabase);

        assertThatThrownBy(reader::findAll)
                .isInstanceOf(RoutesReaderException.class)
                .hasMessageContaining(notADatabase.toString())
                .hasCauseInstanceOf(SQLException.class);
    }
}
