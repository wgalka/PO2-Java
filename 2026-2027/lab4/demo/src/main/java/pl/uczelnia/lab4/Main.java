package pl.uczelnia.lab4;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.prefs.Preferences;

public class Main {
    private static final String PREF_DARK = "dark_theme_lab4";
    private static final Preferences PREFS = Preferences.userNodeForPackage(Main.class);

    public static void main(String[] args) {
        if (args.length > 0 && "--verify".equals(args[0])) {
            runVerifySmokeTest();
            return;
        }

        boolean dark = PREFS.getBoolean(PREF_DARK, false);
        if (dark) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                java.nio.file.Files.createDirectories(java.nio.file.Path.of("data"));
                SessionFactory factory = new Configuration().configure().buildSessionFactory();
                DatasetRepository repo = new DatasetRepository(factory);
                seedDemoData(repo);

                JFrame frame = new JFrame("Lab 4 — Mapowanie obiektowo-relacyjne (Hibernate / JPA)");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setMinimumSize(new Dimension(680, 480));

                buildGui(frame, repo);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Błąd inicjalizacji Hibernate: " + ex.getMessage(),
                        "Błąd ORM", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    private static void buildGui(JFrame frame, DatasetRepository repo) {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton refreshBtn = new JButton("Odśwież (DTO)");
        JButton themeBtn = new JButton("Przełącz motyw");
        JButton rollbackBtn = new JButton("Test wycofania transakcji");

        toolbar.add(refreshBtn);
        toolbar.add(rollbackBtn);
        toolbar.add(themeBtn);
        root.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "Kod", "Tytuł zbioru", "Liczba obserwacji", "Liczba raportów"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        JTextArea detailsArea = new JTextArea(6, 40);
        detailsArea.setEditable(false);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Szczegóły wybranego zbioru (Fetch LAZY w sesji)"));
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        root.add(detailsPanel, BorderLayout.SOUTH);

        Runnable reload = () -> {
            model.setRowCount(0);
            List<DatasetSummaryDto> list = repo.findAllSummaries();
            for (DatasetSummaryDto d : list) {
                model.addRow(new Object[]{d.id(), d.code(), d.title(), d.observationCount(), d.reportCount()});
            }
        };

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) table.getValueAt(row, 0);
                repo.findByIdWithDetails(id).ifPresentOrElse(ds -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Kod: ").append(ds.getCode()).append(" | Źródło: ").append(ds.getSourceOrg())
                            .append(" | Częstotliwość: ").append(ds.getFrequency()).append("\nObserwacje:\n");
                    for (Observation o : ds.getObservations()) {
                        sb.append("  - [").append(o.getPeriod()).append(" / ").append(o.getRegion())
                                .append("]: ").append(o.getValueNumeric()).append("\n");
                    }
                    detailsArea.setText(sb.toString());
                }, () -> detailsArea.setText("Nie znaleziono obiektu."));
            }
        });

        refreshBtn.addActionListener(e -> reload.run());

        rollbackBtn.addActionListener(e -> {
            try {
                EconomicDataset failDs = new EconomicDataset("TEMP_FAIL", "Test Rollback", "GUS", "Roczna");
                failDs.addObservation(new Observation("2024", "PL", new BigDecimal("10.0")));
                failDs.addObservation(new Observation("2024", "PL", new BigDecimal("20.0"))); // Duplikat klucza biznesowego
                repo.save(failDs);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Wycofano transakcję po błędzie unikalności:\n" + ex.getMessage()
                                + "\nKontekst persystencji odrzucił zmiany.",
                        "Potwierdzenie Rollback", JOptionPane.INFORMATION_MESSAGE);
                reload.run();
            }
        });

        themeBtn.addActionListener(e -> {
            boolean dark = PREFS.getBoolean(PREF_DARK, false);
            boolean newDark = !dark;
            if (newDark) FlatDarkLaf.setup(); else FlatLightLaf.setup();
            FlatLaf.updateUI();
            PREFS.putBoolean(PREF_DARK, newDark);
        });

        frame.setContentPane(root);
        reload.run();
    }

    private static void seedDemoData(DatasetRepository repo) {
        if (repo.findAllSummaries().isEmpty()) {
            EconomicDataset ds1 = new EconomicDataset("PL_UNEMP", "Stopa bezrobocia rejestrowanego w Polsce", "GUS", "Kwartalna");
            ds1.addObservation(new Observation("2024-Q1", "Polska", new BigDecimal("5.4")));
            ds1.addObservation(new Observation("2024-Q2", "Polska", new BigDecimal("5.0")));
            ds1.addObservation(new Observation("2024-Q3", "Polska", new BigDecimal("4.9")));
            ds1.addReport(new AnalysisReport("Analiza trendu", 0.88, "Ujemna tendencja wskaźnika w I połowie roku"));
            repo.save(ds1);

            EconomicDataset ds2 = new EconomicDataset("PL_CPI", "Wskaźnik cen towarów i usług konsumpcyjnych (CPI)", "GUS", "Kwartalna");
            ds2.addObservation(new Observation("2024-Q1", "Polska", new BigDecimal("2.8")));
            ds2.addObservation(new Observation("2024-Q2", "Polska", new BigDecimal("2.6")));
            ds2.addObservation(new Observation("2024-Q3", "Polska", new BigDecimal("4.3")));
            ds2.addReport(new AnalysisReport("Model OLS", 0.74, "Dopasowanie regresji liniowej cen"));
            repo.save(ds2);
        }
    }

    public static void runVerifySmokeTest() {
        System.out.println("Uruchamianie weryfikacji Lab 4 (Hibernate / JPA)...");
        try {
            Configuration cfg = new Configuration();
            cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:verify_db;DB_CLOSE_DELAY=-1");
            cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            cfg.setProperty("hibernate.show_sql", "false");
            cfg.addAnnotatedClass(EconomicDataset.class);
            cfg.addAnnotatedClass(Observation.class);
            cfg.addAnnotatedClass(AnalysisReport.class);

            try (SessionFactory factory = cfg.buildSessionFactory()) {
                DatasetRepository repo = new DatasetRepository(factory);

                EconomicDataset ds = new EconomicDataset("TEST_DS", "Zbiór testowy", "TestOrg", "Miesięczna");
                ds.addObservation(new Observation("2025-01", "Region1", new BigDecimal("100.5")));
                ds.addReport(new AnalysisReport("Model testowy", 0.95, "Bardzo dobre dopasowanie"));
                repo.save(ds);

                List<DatasetSummaryDto> summaries = repo.findAllSummaries();
                if (summaries.size() != 1) {
                    throw new IllegalStateException("Oczekiwano 1 zbioru w podsumowaniu, otrzymano: " + summaries.size());
                }
                DatasetSummaryDto sum = summaries.get(0);
                if (sum.observationCount() != 1 || sum.reportCount() != 1) {
                    throw new IllegalStateException("Błędna liczba powiązanych encji w DTO: " + sum);
                }

                // Test rollback
                boolean rollbackCaught = false;
                try {
                    EconomicDataset bad = new EconomicDataset("TEST_DS", "Duplikat kodu", "Test", "Roczna");
                    repo.save(bad); // Naruszenie unique(code)
                } catch (Exception ex) {
                    rollbackCaught = true;
                }
                if (!rollbackCaught) {
                    throw new IllegalStateException("Oczekiwano błędu unikalności kodu zbioru!");
                }

                System.out.println("Weryfikacja zakończona sukcesem (SessionFactory, Encje, Relacje, DTO, Transakcje OK).");
            }
        } catch (Exception e) {
            System.err.println("Weryfikacja zakończona błędem: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
