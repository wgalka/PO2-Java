package pl.uczelnia.lab3;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.prefs.Preferences;

public class Main {
    private static final String PREF_DARK = "dark_theme";
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
                Connection conn = Database.open();
                Database.initSchema(conn);
                seedDemoData(conn);

                JFrame frame = new JFrame("Lab 3 — Analiza wskaźników makroekonomicznych (JDBC)");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setMinimumSize(new Dimension(640, 480));

                buildGui(frame, conn);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Błąd startu aplikacji: " + ex.getMessage(),
                        "Błąd bazy danych",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    private static void buildGui(JFrame frame, Connection conn) {
        IndicatorDao indicatorDao = new IndicatorDao();
        ObservationDao observationDao = new ObservationDao();
        EconomicDataService service = new EconomicDataService(indicatorDao, observationDao);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Pasek narzędziowy / filtrów
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JComboBox<String> indicatorCombo = new JComboBox<>();
        indicatorCombo.addItem("Wszystkie wskaźniki");
        refreshIndicators(conn, indicatorDao, indicatorCombo);

        JButton refreshBtn = new JButton("Odśwież");
        JButton toggleThemeBtn = new JButton("Przełącz motyw");
        JButton testRollbackBtn = new JButton("Test wycofania transakcji");

        toolbar.add(new JLabel("Filtr wskaźnika:"));
        toolbar.add(indicatorCombo);
        toolbar.add(refreshBtn);
        toolbar.add(testRollbackBtn);
        toolbar.add(toggleThemeBtn);
        root.add(toolbar, BorderLayout.NORTH);

        // Tabela danych
        String[] columnNames = {"ID", "Wskaźnik", "Okres", "Region", "Wartość"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Formularz wprowadzania danych
        JPanel formPanel = new JPanel(new GridLayout(2, 5, 6, 6));
        JTextField codeField = new JTextField("INFLATION");
        JTextField periodField = new JTextField("2025-Q1");
        JTextField regionField = new JTextField("Polska");
        JTextField valueField = new JTextField("4.2");
        JButton addBtn = new JButton("Dodaj obserwację");

        formPanel.add(new JLabel("Kod wskaźnika:"));
        formPanel.add(new JLabel("Okres:"));
        formPanel.add(new JLabel("Region:"));
        formPanel.add(new JLabel("Wartość:"));
        formPanel.add(new JLabel(""));

        formPanel.add(codeField);
        formPanel.add(periodField);
        formPanel.add(regionField);
        formPanel.add(valueField);
        formPanel.add(addBtn);

        root.add(formPanel, BorderLayout.SOUTH);
        frame.setContentPane(root);

        // Logika zdarzeń
        Runnable reloadData = () -> {
            try {
                tableModel.setRowCount(0);
                String selected = (String) indicatorCombo.getSelectedItem();
                List<Observation> list;
                if (selected == null || "Wszystkie wskaźniki".equals(selected)) {
                    list = observationDao.findAllWithIndicators(conn);
                } else {
                    var ind = indicatorDao.findByCode(conn, selected);
                    list = ind.isPresent()
                            ? observationDao.findByIndicator(conn, ind.get().id())
                            : List.of();
                }
                for (Observation o : list) {
                    tableModel.addRow(new Object[]{
                            o.id(), o.indicatorCode(), o.period(), o.region(), o.value()
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Błąd odczytu danych: " + ex.getMessage(),
                        "Błąd SQL", JOptionPane.ERROR_MESSAGE);
            }
        };

        refreshBtn.addActionListener(e -> reloadData.run());
        indicatorCombo.addActionListener(e -> reloadData.run());

        addBtn.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String period = periodField.getText().trim();
                String region = regionField.getText().trim();
                BigDecimal val = new BigDecimal(valueField.getText().trim().replace(',', '.'));

                var indOpt = indicatorDao.findByCode(conn, code);
                long indId;
                if (indOpt.isEmpty()) {
                    indId = indicatorDao.insert(conn, new Indicator(0, code, code, "Ogólne", "%"));
                    refreshIndicators(conn, indicatorDao, indicatorCombo);
                } else {
                    indId = indOpt.get().id();
                }

                observationDao.insert(conn, new Observation(0, indId, code, period, region, val));
                reloadData.run();
                JOptionPane.showMessageDialog(frame, "Pomyślnie dodano obserwację.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Niepoprawny format liczby: " + valueField.getText(),
                        "Błąd walidacji", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(frame, "Błąd zapisu w bazie: " + sqle.getMessage(),
                        "Błąd integralności SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        testRollbackBtn.addActionListener(e -> {
            try {
                Indicator testInd = new Indicator(0, "TEST_ROLLBACK", "Wskaźnik testowy", "Test", "pkt");
                List<Observation> observationsWithDuplicate = List.of(
                        new Observation(0, 0, "TEST_ROLLBACK", "2025-Q1", "Region A", new BigDecimal("10.0")),
                        // Druga obserwacja o tym samym kluczu naruszy UNIQUE (indicator_id, period, region):
                        new Observation(0, 0, "TEST_ROLLBACK", "2025-Q1", "Region A", new BigDecimal("20.0"))
                );
                service.saveSeriesInTransaction(conn, testInd, observationsWithDuplicate);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Wycofano transakcję po napotkaniu błędu integralności:\n" + ex.getMessage()
                                + "\nŻadne dane z paczki nie zostały zapisane.",
                        "Potwierdzenie Rollback", JOptionPane.INFORMATION_MESSAGE);
                reloadData.run();
            }
        });

        toggleThemeBtn.addActionListener(e -> {
            boolean currentDark = PREFS.getBoolean(PREF_DARK, false);
            boolean newDark = !currentDark;
            if (newDark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            FlatLaf.updateUI();
            PREFS.putBoolean(PREF_DARK, newDark);
        });

        reloadData.run();
    }

    private static void refreshIndicators(Connection conn, IndicatorDao dao, JComboBox<String> combo) {
        try {
            combo.removeAllItems();
            combo.addItem("Wszystkie wskaźniki");
            for (Indicator ind : dao.findAll(conn)) {
                combo.addItem(ind.code());
            }
        } catch (SQLException ignored) {}
    }

    private static void seedDemoData(Connection conn) throws SQLException {
        IndicatorDao indDao = new IndicatorDao();
        ObservationDao obsDao = new ObservationDao();
        if (indDao.findAll(conn).isEmpty()) {
            long ind1 = indDao.insert(conn, new Indicator(0, "UNEMP_RATE", "Stopa bezrobocia rejestrowanego", "Rynek pracy", "%"));
            long ind2 = indDao.insert(conn, new Indicator(0, "INFLATION", "Wskaźnik cen towarów i usług (CPI)", "Ceny", "%"));

            obsDao.insert(conn, new Observation(0, ind1, "UNEMP_RATE", "2024-Q1", "Polska", new BigDecimal("5.4")));
            obsDao.insert(conn, new Observation(0, ind1, "UNEMP_RATE", "2024-Q2", "Polska", new BigDecimal("5.0")));
            obsDao.insert(conn, new Observation(0, ind1, "UNEMP_RATE", "2024-Q3", "Polska", new BigDecimal("4.9")));
            obsDao.insert(conn, new Observation(0, ind1, "UNEMP_RATE", "2024-Q4", "Polska", new BigDecimal("5.1")));

            obsDao.insert(conn, new Observation(0, ind2, "INFLATION", "2024-Q1", "Polska", new BigDecimal("2.8")));
            obsDao.insert(conn, new Observation(0, ind2, "INFLATION", "2024-Q2", "Polska", new BigDecimal("2.6")));
            obsDao.insert(conn, new Observation(0, ind2, "INFLATION", "2024-Q3", "Polska", new BigDecimal("4.3")));
            obsDao.insert(conn, new Observation(0, ind2, "INFLATION", "2024-Q4", "Polska", new BigDecimal("4.6")));
        }
    }

    public static void runVerifySmokeTest() {
        System.out.println("Uruchamianie weryfikacji Lab 3 (JDBC / SQLite)...");
        try (Connection conn = Database.open("jdbc:sqlite::memory:")) {
            Database.initSchema(conn);

            IndicatorDao indDao = new IndicatorDao();
            ObservationDao obsDao = new ObservationDao();
            EconomicDataService service = new EconomicDataService(indDao, obsDao);

            long indId = indDao.insert(conn, new Indicator(0, "GDP_GROWTH", "Dynamika PKB", "Rachunki", "% r/r"));
            assert indId > 0 : "Niepoprawne ID wskaźnika";

            obsDao.insert(conn, new Observation(0, indId, "GDP_GROWTH", "2024-Q1", "Polska", new BigDecimal("2.0")));
            obsDao.insert(conn, new Observation(0, indId, "GDP_GROWTH", "2024-Q2", "Polska", new BigDecimal("3.2")));

            var list = obsDao.findByIndicator(conn, indId);
            if (list.size() != 2) {
                throw new IllegalStateException("Oczekiwano 2 obserwacji, otrzymano: " + list.size());
            }

            var changes = service.computePeriodChanges(list);
            if (changes.size() != 1) {
                throw new IllegalStateException("Oczekiwano 1 zmiany okresu");
            }

            // Test transakcji z błędem
            boolean rollbackCaught = false;
            try {
                service.saveSeriesInTransaction(conn,
                        new Indicator(0, "BATCH_TEST", "Test", "Test", "%"),
                        List.of(
                                new Observation(0, 0, "BATCH_TEST", "2024", "Polska", new BigDecimal("1.0")),
                                new Observation(0, 0, "BATCH_TEST", "2024", "Polska", new BigDecimal("2.0")) // Duplikat
                        ));
            } catch (SQLException ex) {
                rollbackCaught = true;
            }

            if (!rollbackCaught) {
                throw new IllegalStateException("Oczekiwano błędu unikalności w transakcji!");
            }

            if (indDao.findByCode(conn, "BATCH_TEST").isPresent()) {
                throw new IllegalStateException("Rollback nie wycofał wskaźnika z transakcji!");
            }

            System.out.println("Weryfikacja zakończona sukcesem (Baza, DAO, Schemat, Transakcje, Obliczenia OK).");
        } catch (Exception e) {
            System.err.println("Weryfikacja zakończona błędem: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
