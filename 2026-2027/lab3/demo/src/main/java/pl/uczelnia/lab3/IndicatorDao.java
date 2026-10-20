package pl.uczelnia.lab3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IndicatorDao {

    public long insert(Connection conn, Indicator indicator) throws SQLException {
        String sql = "INSERT INTO indicator(code, name, category, unit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, indicator.code());
            stmt.setString(2, indicator.name());
            stmt.setString(3, indicator.category());
            stmt.setString(4, indicator.unit());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Nie udało się uzyskać wygenerowanego klucza wskaźnika");
    }

    public Optional<Indicator> findById(Connection conn, long id) throws SQLException {
        String sql = "SELECT id, code, name, category, unit FROM indicator WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Indicator> findByCode(Connection conn, String code) throws SQLException {
        String sql = "SELECT id, code, name, category, unit FROM indicator WHERE code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Indicator> findAll(Connection conn) throws SQLException {
        String sql = "SELECT id, code, name, category, unit FROM indicator ORDER BY code";
        List<Indicator> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean update(Connection conn, Indicator indicator) throws SQLException {
        String sql = "UPDATE indicator SET code = ?, name = ?, category = ?, unit = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, indicator.code());
            stmt.setString(2, indicator.name());
            stmt.setString(3, indicator.category());
            stmt.setString(4, indicator.unit());
            stmt.setLong(5, indicator.id());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM indicator WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Indicator mapRow(ResultSet rs) throws SQLException {
        return new Indicator(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("unit")
        );
    }
}
