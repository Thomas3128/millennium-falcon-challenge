package com.falcon.falcon.routes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.falcon.falcon.utils.Require;

public class RoutesReader {

    private final Path dbPath;
    
    public RoutesReader(Path dbPath) {
        Require.that(dbPath != null, "database path is required");
        Require.that(Files.exists(dbPath), "database file does not exist: " + dbPath);
        this.dbPath = dbPath;
    }

    public List<Route> findAll() {
        String url = "jdbc:sqlite:" + dbPath;
        try (Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT origin, destination, travel_time FROM routes")) {

            List<Route> routes = new ArrayList<>();
            while (rs.next()) {
                routes.add(new Route(rs.getString("origin"),rs.getString("destination"),rs.getInt("travel_time")));
            }
            return routes;

        } catch (SQLException e) {
            throw new RoutesReaderException("Cannot read routes from " + dbPath, e);
        }
    }

}
