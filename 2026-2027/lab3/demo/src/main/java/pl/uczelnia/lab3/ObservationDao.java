package pl.uczelnia.lab3;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ObservationDao {

    public long insert(Connection conn, Observation obs) throws SQLException {
        String sql = "INSERT INTO observation(indicator_id, period, region, value_numeric) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, obs.indicatorId());
            stmt.setString(2, obs.period());
            stmt.setString(3, obs.region());
            stmt.setBigDecimal(4, obs.value());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Nie udało się uzyskać wygenerowanego klucza obserwacji");
    }

    public void insertBatch(Connection conn, List<Observation> observations) throws SQLException {
        String sql = "INSERT INTO observation(indicator_id, period, region, value_numeric) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Observation obs : observations) {
                stmt.setLong(1, obs.indicatorId());
                stmt.setString(2, obs.period());
                stmt.setString(3, obs.region());
                stmt.setBigDecimal(4, obs.value());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public List<Observation> findByIndicator(Connection conn, long indicatorId) throws SQLException {
        String sql = """
                SELECT o.id, o.indicator_id, i.code AS indicator_code, o.period, o.region, o.value_numeric
                FROM observation o
                JOIN indicator i ON i.id = o.indicator_id
                WHERE o.indicator_id = ?
                ORDER BY o.period, o.region
                """;
        List<Observation> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, indicatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Observation> findAllWithIndicators(Connection conn) throws SQLException {
        String sql = """
                SELECT o.id, o.indicator_id, i.code AS indicator_code, o.period, o.region, o.value_numeric
                FROM observation o
                JOIN indicator i ON i.id = o.indicator_id
                ORDER BY o.period, i.code, o.region
                """;
        List<Observation> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean delete(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM observation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Observation mapRow(ResultSet rs) throws SQLException {
        BigDecimal val = rs.getBigDecimal("value_numeric");
        return new Observation(
                rs.getLong("id"),
                rs.getLong("indicator_id"),
                rs.getString("indicator_code"),
                rs.getString("period"),
                rs.getString("region"),
                val != null ? val : BigDecimal.ZERO
        );
    }
}
