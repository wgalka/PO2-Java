package pl.uczelnia.lab2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class Main extends Application {
    private final ObservationModel model = new ObservationModel();
    private final ObservableList<Observation> observations = model.getObservations();
    private final ObservableList<String> regionOptions = FXCollections.observableArrayList();
    private final Preferences preferences = Preferences.userNodeForPackage(Main.class);

    private FilteredList<Observation> filtered;
    private TableView<Observation> table;
    private LineChart<Number, Number> chart;
    private MetricCard countCard;
    private MetricCard meanCard;
    private MetricCard regionsCard;
    private Label status;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        observations.setAll(sampleData());
        refreshRegionOptions();
        observations.addListener((ListChangeListener<Observation>) change -> refreshRegionOptions());
        filtered = new FilteredList<>(observations, observation -> true);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(new VBox(createMenuBar(stage), createHeader()));
        root.setCenter(createViews());
        status = new Label("Gotowe · dane demonstracyjne");
        status.getStyleClass().add("status-bar");
        root.setBottom(status);

        scene = new Scene(root, 1080, 720);
        applyTheme(preferences.getBoolean("dark", false));
        filtered.addListener((ListChangeListener<Observation>) change -> refreshDerivedViews());
        refreshDerivedViews();

        stage.setTitle("Analiza regionalnych wskaźników rynku pracy");
        stage.setMinWidth(560);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        Menu file = new Menu("Plik");
        MenuItem restore = new MenuItem("Przywróć dane demonstracyjne");
        restore.setOnAction(event -> {
            observations.setAll(sampleData());
            status.setText("Przywrócono dane demonstracyjne");
        });
        MenuItem exit = new MenuItem("Zakończ");
        exit.setOnAction(event -> stage.close());
        file.getItems().addAll(restore, new SeparatorMenuItem(), exit);

        Menu view = new Menu("Widok");
        Menu theme = new Menu("Motyw");
        ToggleGroup themes = new ToggleGroup();
        RadioMenuItem light = new RadioMenuItem("Jasny");
        RadioMenuItem dark = new RadioMenuItem("Ciemny");
        light.setToggleGroup(themes);
        dark.setToggleGroup(themes);
        light.setSelected(!preferences.getBoolean("dark", false));
        dark.setSelected(preferences.getBoolean("dark", false));
        light.setOnAction(event -> applyTheme(false));
        dark.setOnAction(event -> applyTheme(true));
        theme.getItems().addAll(light, dark);
        view.getItems().add(theme);

        Menu help = new Menu("Pomoc");
        MenuItem about = new MenuItem("O aplikacji");
        about.setOnAction(event -> new Alert(Alert.AlertType.INFORMATION,
                "Przykładowy pulpit do analizy regionalnych danych ekonomicznych.").showAndWait());
        help.getItems().add(about);
        return new MenuBar(file, view, help);
    }

    private VBox createHeader() {
        Label title = new Label("Wskaźniki rynku pracy");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Dane demonstracyjne · stopa bezrobocia według regionu i roku");
        subtitle.getStyleClass().add("muted");
        VBox header = new VBox(3, title, subtitle);
        header.setPadding(new Insets(14, 20, 12, 20));
        return header;
    }

    private TabPane createViews() {
        Tab dataTab = new Tab("Dane", createDataView());
        dataTab.setClosable(false);
        Tab chartTab = new Tab("Wykres", createChartView());
        chartTab.setClosable(false);
        TabPane tabs = new TabPane(dataTab, chartTab);
        tabs.getStyleClass().add("main-tabs");
        return tabs;
    }

    private BorderPane createDataView() {
        countCard = new MetricCard("Obserwacje w analizie", "—");
        meanCard = new MetricCard("Średnia stopa", "—");
        regionsCard = new MetricCard("Liczba regionów", "—");
        FlowPane metrics = new FlowPane(10, 10, countCard, meanCard, regionsCard);
        metrics.setPadding(new Insets(16, 16, 10, 16));

        TextField filter = new TextField();
        filter.setPromptText("Filtruj po nazwie regionu");
        filter.setPrefColumnCount(20);
        filter.textProperty().addListener((observable, before, after) -> {
            String query = after.strip().toLowerCase(java.util.Locale.ROOT);
            filtered.setPredicate(observation -> observation.getRegion()
                    .toLowerCase(java.util.Locale.ROOT).contains(query));
        });
        Button clearFilter = new Button("Wyczyść filtr");
        clearFilter.setOnAction(event -> filter.clear());
        Button delete = new Button("Usuń zaznaczoną");
        delete.setOnAction(event -> {
            Observation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Zaznacz obserwację w tabeli");
            } else {
                observations.remove(selected);
                status.setText("Usunięto obserwację · widok przeliczony");
            }
        });
        FlowPane actions = new FlowPane(8, 8, filter, clearFilter, delete);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(0, 16, 12, 16));

        table = createTable();
        BorderPane page = new BorderPane();
        page.setTop(new VBox(metrics, actions));
        page.setCenter(table);
        BorderPane.setMargin(table, new Insets(0, 16, 16, 16));
        return page;
    }

    private TableView<Observation> createTable() {
        TableView<Observation> result = new TableView<>();
        SortedList<Observation> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(result.comparatorProperty());
        result.setItems(sorted);
        result.setEditable(true);
        result.setPlaceholder(new Label("Brak obserwacji dla wybranego filtra"));

        TableColumn<Observation, Number> year = new TableColumn<>("Rok");
        year.setCellValueFactory(cell -> cell.getValue().yearProperty());
        year.setPrefWidth(100);

        TableColumn<Observation, String> region = new TableColumn<>("Region");
        region.setCellValueFactory(cell -> cell.getValue().regionProperty());
        region.setCellFactory(ComboBoxTableCell.forTableColumn(regionOptions));
        region.setOnEditCommit(event -> {
            Observation item = event.getRowValue();
            try {
                String value = ObservationValidation.validate(item.getYear(),
                        event.getNewValue(), item.getRate(), observations, item);
                item.setRegion(value);
                status.setText("Zmieniono region · podsumowanie i wykres zaktualizowane");
            } catch (IllegalArgumentException error) {
                new Alert(Alert.AlertType.ERROR, error.getMessage()).showAndWait();
                result.refresh();
            }
        });
        region.setPrefWidth(240);

        TableColumn<Observation, Number> rate = new TableColumn<>("Stopa bezrobocia (%)");
        rate.setCellValueFactory(cell -> cell.getValue().rateProperty());
        rate.setCellFactory(column -> new RateEditingCell());
        rate.setOnEditCommit(event -> {
            Number entered = event.getNewValue();
            double value = entered == null ? Double.NaN : entered.doubleValue();
            if (!Double.isFinite(value) || value < 0 || value > 100) {
                new Alert(Alert.AlertType.ERROR,
                        "Wartość musi być liczbą z zakresu 0–100.").showAndWait();
                result.refresh();
                return;
            }
            event.getRowValue().setRate(value);
            status.setText("Zmieniono wartość obserwacji · podsumowanie i wykres zaktualizowane");
        });
        rate.setPrefWidth(210);

        TableColumn<Observation, Boolean> included = new TableColumn<>("W analizie");
        included.setCellValueFactory(cell -> cell.getValue().includedInAnalysisProperty());
        included.setCellFactory(CheckBoxTableCell.forTableColumn(included));
        included.setEditable(true);
        included.setPrefWidth(110);

        result.getColumns().setAll(year, region, rate, included);
        return result;
    }

    private final class RateEditingCell extends TableCell<Observation, Number> {
        private final TextField editor = new TextField();

        RateEditingCell() {
            editor.setOnAction(event -> {
                try {
                    commitEdit(ObservationValidation.parseRate(editor.getText()));
                } catch (IllegalArgumentException error) {
                    new Alert(Alert.AlertType.ERROR, error.getMessage()).showAndWait();
                    editor.requestFocus();
                    editor.selectAll();
                }
                event.consume();
            });
            editor.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    cancelEdit();
                    event.consume();
                }
            });
        }

        @Override public void startEdit() {
            if (isEmpty() || !getTableView().isEditable()
                    || !getTableColumn().isEditable() || !isEditable()) return;
            super.startEdit();
            editor.setText(getItem().toString().replace('.', ','));
            setText(null);
            setGraphic(editor);
            editor.requestFocus();
            editor.selectAll();
        }

        @Override public void cancelEdit() {
            super.cancelEdit();
            showValue();
        }

        @Override protected void updateItem(Number value, boolean empty) {
            super.updateItem(value, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                setText(null);
                setGraphic(editor);
            } else {
                showValue();
            }
        }

        private void showValue() {
            setText(isEmpty() || getItem() == null ? null
                    : getItem().toString().replace('.', ','));
            setGraphic(null);
        }
    }

    private void refreshRegionOptions() {
        regionOptions.setAll(observations.stream()
                .map(Observation::getRegion)
                .filter(region -> region != null && !region.isBlank())
                .distinct()
                .sorted()
                .toList());
    }

    private BorderPane createChartView() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Rok");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Stopa bezrobocia (%)");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Stopa bezrobocia w regionach");
        chart.setAnimated(true);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);
        BorderPane page = new BorderPane(chart);
        page.setPadding(new Insets(16));
        return page;
    }

    private void refreshDerivedViews() {
        if (countCard == null || chart == null) {
            return;
        }
        List<Observation> visible = filtered.stream()
                .filter(Observation::isIncludedInAnalysis)
                .toList();
        countCard.setValue(Integer.toString(visible.size()));
        double mean = visible.stream().mapToDouble(Observation::getRate).average().orElse(Double.NaN);
        meanCard.setValue(Double.isNaN(mean) ? "—" : String.format(java.util.Locale.forLanguageTag("pl-PL"), "%.2f%%", mean));
        regionsCard.setValue(Long.toString(visible.stream().map(Observation::getRegion).distinct().count()));

        Map<String, List<Observation>> byRegion = new TreeMap<>();
        for (Observation observation : visible) {
            byRegion.computeIfAbsent(observation.getRegion(), ignored -> new ArrayList<>()).add(observation);
        }
        chart.getData().clear();
        for (Map.Entry<String, List<Observation>> entry : byRegion.entrySet()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            entry.getValue().stream()
                    .sorted(java.util.Comparator.comparingInt(Observation::getYear))
                    .forEach(observation -> series.getData().add(
                            new XYChart.Data<>(observation.getYear(), observation.getRate())));
            chart.getData().add(series);
        }
    }

    private void applyTheme(boolean dark) {
        String base = resourceUrl("/css/base.css");
        String theme = resourceUrl(dark ? "/css/dark.css" : "/css/light.css");
        scene.getStylesheets().setAll(base, theme);
        preferences.putBoolean("dark", dark);
    }

    private String resourceUrl(String path) {
        return java.util.Objects.requireNonNull(getClass().getResource(path), path).toExternalForm();
    }

    private static List<Observation> sampleData() {
        return List.of(
                new Observation(2021, "Podkarpackie", 8.2),
                new Observation(2022, "Podkarpackie", 7.3),
                new Observation(2023, "Podkarpackie", 6.8),
                new Observation(2024, "Podkarpackie", 6.1),
                new Observation(2025, "Podkarpackie", 5.7),
                new Observation(2021, "Małopolskie", 5.1),
                new Observation(2022, "Małopolskie", 4.8),
                new Observation(2023, "Małopolskie", 4.6),
                new Observation(2024, "Małopolskie", 4.4),
                new Observation(2025, "Małopolskie", 4.2));
    }

    @Override
    public void stop() {
        // Zasoby uruchamiane w tle należy zamykać w tym miejscu.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
