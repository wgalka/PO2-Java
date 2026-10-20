package pl.uczelnia.lab3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EconomicDataService {
    private final IndicatorDao indicatorDao;
    private final ObservationDao observationDao;

    public EconomicDataService(IndicatorDao indicatorDao, ObservationDao observationDao) {
        this.indicatorDao = indicatorDao;
        this.observationDao = observationDao;
    }

    public long saveSeriesInTransaction(Connection conn, Indicator indicator, List<Observation> observations)
            throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            Optional<Indicator> existing = indicatorDao.findByCode(conn, indicator.code());
            long indicatorId;
            if (existing.isPresent()) {
                indicatorId = existing.get().id();
            } else {
                indicatorId = indicatorDao.insert(conn, indicator);
            }

            List<Observation> readyToInsert = new ArrayList<>();
            for (Observation obs : observations) {
                readyToInsert.add(new Observation(
                        0,
                        indicatorId,
                        indicator.code(),
                        obs.period(),
                        obs.region(),
                        obs.value()
                ));
            }
            observationDao.insertBatch(conn, readyToInsert);

            conn.commit();
            return indicatorId;
        } catch (SQLException | RuntimeException ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public List<BigDecimal> computePeriodChanges(List<Observation> sortedSeries) {
        List<BigDecimal> changes = new ArrayList<>();
        if (sortedSeries == null || sortedSeries.size() < 2) {
            return changes;
        }
        for (int i = 1; i < sortedSeries.size(); i++) {
            BigDecimal prev = sortedSeries.get(i - 1).value();
            BigDecimal curr = sortedSeries.get(i).value();
            if (prev.compareTo(BigDecimal.ZERO) == 0) {
                changes.add(BigDecimal.ZERO);
            } else {
                BigDecimal diff = curr.subtract(prev);
                BigDecimal pct = diff.divide(prev, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                changes.add(pct);
            }
        }
        return changes;
    }
}
