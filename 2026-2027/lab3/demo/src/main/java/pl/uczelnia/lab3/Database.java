package pl.uczelnia.lab3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    public static final String DEFAULT_URL = "jdbc:sqlite:data/economy.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {
            // ServiceLoader will attempt loading if Class.forName fails
        }
    }

    private Database() {}

    public static Connection open() throws SQLException {
        return open(DEFAULT_URL);
    }

    public static Connection open(String url) throws SQLException {
        if (url.startsWith("jdbc:sqlite:") && !url.contains(":memory:")) {
            String pathPart = url.substring("jdbc:sqlite:".length());
            Path dbPath = Path.of(pathPart);
            Path parent = dbPath.getParent();
            if (parent != null) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    throw new IllegalStateException("Nie można utworzyć katalogu bazy danych: " + parent, e);
                }
            }
        }

        Connection conn = DriverManager.getConnection(url);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException ex) {
            conn.close();
            throw ex;
        }
        return conn;
    }

    public static void initSchema(Connection conn) throws SQLException {
        String ddl = """
                CREATE TABLE IF NOT EXISTS indicator (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    code TEXT NOT NULL UNIQUE,
                    name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    unit TEXT NOT NULL
                );

                CREATE TABLE IF NOT EXISTS observation (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    indicator_id INTEGER NOT NULL,
                    period TEXT NOT NULL,
                    region TEXT NOT NULL,
                    value_numeric NUMERIC NOT NULL,
                    FOREIGN KEY (indicator_id) REFERENCES indicator(id) ON DELETE CASCADE,
                    UNIQUE (indicator_id, period, region)
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(ddl);
        }
    }
}
