package pl.uczelnia.lab5;

public record EconomicIndicator(
        String code,
        String name,
        String category,
        double latestValue,
        String unit
) {
    public EconomicIndicator {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Kod wskaźnika jest wymagany");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa wskaźnika jest wymagana");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Kategoria jest wymagana");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Jednostka miary jest wymagana");
        }
    }
}
