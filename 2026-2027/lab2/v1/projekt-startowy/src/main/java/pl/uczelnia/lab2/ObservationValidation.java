package pl.uczelnia.lab2;

import java.util.Collection;

public final class ObservationValidation {
    private ObservationValidation() { }

    public static double parseRate(String text) {
        String normalized = text == null ? "" : text.strip().replace(',', '.');
        if (!normalized.matches("[+-]?[0-9]+(?:\\.[0-9]+)?")) {
            throw new IllegalArgumentException("Stopa: wymagana liczba, np. 5,7 lub 5.7.");
        }
        double value = Double.parseDouble(normalized);
        if (!Double.isFinite(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException("Stopa musi należeć do zakresu 0–100.");
        }
        return value;
    }

    public static String validate(int year, String region, double rate,
                                  Collection<Observation> observations,
                                  Observation edited) {
        if (year < 1990 || year > 2035) {
            throw new IllegalArgumentException("Rok musi należeć do zakresu 1990–2035.");
        }
        String normalized = region == null ? "" : region.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Region jest wymagany.");
        }
        if (!Double.isFinite(rate) || rate < 0 || rate > 100) {
            throw new IllegalArgumentException("Stopa musi należeć do zakresu 0–100.");
        }
        boolean duplicate = observations.stream().anyMatch(item -> item != edited
                && item.getYear() == year
                && item.getRegion().strip().equalsIgnoreCase(normalized));
        if (duplicate) {
            throw new IllegalArgumentException("Istnieje już obserwacja tego regionu i roku.");
        }
        return normalized;
    }
}
