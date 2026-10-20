package pl.uczelnia.lab5;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IndicatorDataService {
    private static final IndicatorDataService INSTANCE = new IndicatorDataService();

    private final List<EconomicIndicator> storage = new CopyOnWriteArrayList<>();

    private IndicatorDataService() {
        storage.add(new EconomicIndicator("UNEMP_RATE", "Stopa bezrobocia rejestrowanego", "Rynek pracy", 5.0, "%"));
        storage.add(new EconomicIndicator("CPI_INFLATION", "Wskaźnik cen towarów i usług konsumpcyjnych (CPI)", "Ceny", 4.2, "% r/r"));
        storage.add(new EconomicIndicator("GDP_REAL_GROWTH", "Dynamika realnego PKB", "Rachunki narodowe", 3.2, "% r/r"));
        storage.add(new EconomicIndicator("RETAIL_SALES", "Dynamika sprzedaży detalicznej", "Handel", 2.1, "% r/r"));
    }

    public static IndicatorDataService getInstance() {
        return INSTANCE;
    }

    public List<EconomicIndicator> findAll(String categoryFilter) {
        if (categoryFilter == null || categoryFilter.isBlank() || "Wszystkie".equalsIgnoreCase(categoryFilter)) {
            return List.copyOf(storage);
        }
        return storage.stream()
                .filter(i -> i.category().equalsIgnoreCase(categoryFilter.trim()))
                .toList();
    }

    public synchronized void add(EconomicIndicator indicator) {
        boolean exists = storage.stream().anyMatch(i -> i.code().equalsIgnoreCase(indicator.code()));
        if (exists) {
            throw new IllegalArgumentException("Wskaźnik o kodzie " + indicator.code() + " już istnieje.");
        }
        storage.add(indicator);
    }

    public synchronized boolean delete(String code) {
        return storage.removeIf(i -> i.code().equalsIgnoreCase(code));
    }
}
