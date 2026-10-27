package pl.uczelnia.lab3;

public record Indicator(
        long id,
        String code,
        String name,
        String category,
        String unit
) {
    public Indicator {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Kod wskaźnika nie może być pusty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa wskaźnika nie może być pusta");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Kategoria wskaźnika nie może być pusta");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Jednostka miary nie może być pusta");
        }
    }
}
