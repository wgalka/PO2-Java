package pl.uczelnia.lab3;

import java.math.BigDecimal;

public record Observation(
        long id,
        long indicatorId,
        String indicatorCode,
        String period,
        String region,
        BigDecimal value
) {
    public Observation {
        if (period == null || period.isBlank()) {
            throw new IllegalArgumentException("Okres obserwacji nie może być pusty");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region nie może być pusty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Wartość obserwacji nie może być null");
        }
    }
}
