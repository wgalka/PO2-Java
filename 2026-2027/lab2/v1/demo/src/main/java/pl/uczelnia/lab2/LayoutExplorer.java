package pl.uczelnia.lab2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Objects;
import java.util.prefs.Preferences;

/** Każda metoda example tworzy oddzielne kontrolki — węzeł ma jednego rodzica. */
public class LayoutExplorer extends Application {
    private static final String[] NAMES = {"HBox", "VBox", "FlowPane", "TilePane",
            "BorderPane", "GridPane", "StackPane", "AnchorPane", "ScrollPane / Accordion", "Zdarzenia"};
    private final Slider width = new Slider(320, 1000, 720);
    private final Slider spacing = new Slider(0, 30, 10);
    private final Slider padding = new Slider(0, 40, 16);
    private final CheckBox grow = new CheckBox("Pozwól rosnąć (HBox / VBox / GridPane)");
    private final ComboBox<Pos> alignment = new ComboBox<>();
    private final Label description = new Label();
    private final StackPane display = new StackPane();
    private Region sample;

    @Override public void start(Stage stage) {
        if (getParameters().getRaw().contains("--verify")) {
            try { verify(); }
            catch (Throwable ex) { ex.printStackTrace(); System.exit(1); }
            finally { Platform.exit(); }
            return;
        }
        alignment.getItems().setAll(Pos.TOP_LEFT, Pos.CENTER, Pos.BOTTOM_RIGHT);
        alignment.setValue(Pos.TOP_LEFT);
        grow.setSelected(true);
        ComboBox<String> choice = new ComboBox<>();
        choice.getItems().addAll(NAMES);
        choice.setValue(NAMES[0]);
        CheckBox dark = new CheckBox("Ciemny motyw");
        Preferences prefs = Preferences.userNodeForPackage(LayoutExplorer.class);
        dark.setSelected(prefs.getBoolean("dark", false));
        CheckBox largeText = new CheckBox("Tekst +25%");
        FlowPane toolbar = new FlowPane(12, 10, choice, alignment, grow, dark, largeText);
        VBox settings = new VBox(8, sliderRow("Szerokość przykładu", width),
                sliderRow("Spacing / gap", spacing), sliderRow("Padding", padding));
        description.setWrapText(true);
        description.getStyleClass().add("note");
        display.setAlignment(Pos.TOP_LEFT);
        ScrollPane exampleScroll = new ScrollPane(display);
        exampleScroll.setFitToHeight(true);
        exampleScroll.setPrefViewportHeight(340);
        Label size = new Label();
        size.textProperty().bind(Bindings.createStringBinding(
                () -> "Szerokość próbki: " + Math.round(width.getValue())
                        + " jednostek logicznych. Przewijaj pole, jeśli próbka jest szersza od okna.",
                width.valueProperty()));
        size.setWrapText(true);
        Label title = new Label("JavaFX — laboratorium układów");
        title.getStyleClass().add("heading");
        VBox page = new VBox(title, toolbar, settings, description, exampleScroll, size);
        page.getStyleClass().add("page");
        VBox.setVgrow(exampleScroll, Priority.ALWAYS);
        ScrollPane outer = new ScrollPane(page);
        outer.setFitToWidth(true);
        Scene scene = new Scene(outer, 1020, 740);
        applyTheme(scene, dark.isSelected());
        dark.selectedProperty().addListener((o, before, after) -> {
            applyTheme(scene, after);
            prefs.putBoolean("dark", after);
        });
        largeText.selectedProperty().addListener((o, before, after) ->
                outer.setStyle("-fx-font-size: " + (after ? "17.5px;" : "14px;")));
        choice.valueProperty().addListener((o, before, after) -> show(after));
        show(choice.getValue());
        stage.setTitle("Lab 2 — JavaFX Layout Explorer");
        stage.setScene(scene);
        stage.show();
    }

    private HBox sliderRow(String caption, Slider slider) {
        Label value = new Label();
        value.textProperty().bind(slider.valueProperty().asString("%.0f"));
        slider.setMinWidth(60);
        HBox.setHgrow(slider, Priority.ALWAYS);
        HBox row = new HBox(10, new Label(caption), slider, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void show(String name) {
        sample = example(name);
        sample.getStyleClass().add("sample");
        sample.setMinSize(0, 300);
        sample.setPrefHeight(300);
        sample.setMaxHeight(300);
        sample.prefWidthProperty().bind(width.valueProperty());
        sample.maxWidthProperty().bind(width.valueProperty());
        sample.minWidthProperty().bind(width.valueProperty());
        sample.paddingProperty().bind(Bindings.createObjectBinding(
                () -> new Insets(padding.getValue()), padding.valueProperty()));
        display.getChildren().setAll(sample);
    }

    private Region example(String name) {
        return switch (name) {
            case "HBox" -> hbox();
            case "VBox" -> vbox();
            case "FlowPane" -> flow();
            case "TilePane" -> tiles();
            case "BorderPane" -> border();
            case "GridPane" -> grid();
            case "StackPane" -> stack();
            case "AnchorPane" -> anchor();
            case "ScrollPane / Accordion" -> accordion();
            case "Zdarzenia" -> events();
            default -> throw new IllegalArgumentException(name);
        };
    }

    private HBox hbox() {
        description.setText("HBox: jeden wiersz, bez zawijania. Włącz wzrost: pole przejmuje wolną szerokość. Wyłącz i porównaj alignment.");
        TextField field = new TextField("Inflacja — dane demo");
        field.setPrefColumnCount(8);
        HBox box = new HBox(new Label("Zbiór"), field, new Button("Wybierz"));
        box.spacingProperty().bind(spacing.valueProperty());
        box.alignmentProperty().bind(alignment.valueProperty());
        // ALWAYS przydziela wolne miejsce, lecz maxWidth może ograniczyć wzrost.
        field.maxWidthProperty().bind(Bindings.when(grow.selectedProperty())
                .then(Double.MAX_VALUE).otherwise(Region.USE_PREF_SIZE));
        HBox.setHgrow(field, Priority.ALWAYS);
        return box;
    }

    private VBox vbox() {
        description.setText("VBox: kolumna. TextArea może przejąć wolną wysokość; samo vgrow nie przełamuje maxHeight.");
        TextArea notes = new TextArea("Opis jednostek, okresu i źródła danych.");
        notes.setPrefRowCount(2); notes.setWrapText(true);
        notes.maxHeightProperty().bind(Bindings.when(grow.selectedProperty())
                .then(Double.MAX_VALUE).otherwise(Region.USE_PREF_SIZE));
        VBox.setVgrow(notes, Priority.ALWAYS);
        VBox box = new VBox(new Label("Notatki do analizy"), notes, new Button("Zapisz opis"));
        box.spacingProperty().bind(spacing.valueProperty());
        box.alignmentProperty().bind(alignment.valueProperty());
        return box;
    }

    private FlowPane flow() {
        description.setText("FlowPane: różne szerokości elementów, zawijanie zależy od aktualnej szerokości kontenera.");
        FlowPane flow = new FlowPane();
        flow.hgapProperty().bind(spacing.valueProperty());
        flow.vgapProperty().bind(spacing.valueProperty());
        flow.alignmentProperty().bind(alignment.valueProperty());
        for (String text : new String[]{"PKB", "Stopa bezrobocia", "Inflacja", "Wynagrodzenie", "Eksport", "Import"})
            flow.getChildren().add(new Button(text));
        return flow;
    }

    private TilePane tiles() {
        description.setText("TilePane: jednakowe pola dla kafelków; liczba kolumn zmienia się z szerokością. To inna reguła niż FlowPane.");
        TilePane pane = new TilePane();
        pane.hgapProperty().bind(spacing.valueProperty());
        pane.vgapProperty().bind(spacing.valueProperty());
        pane.alignmentProperty().bind(alignment.valueProperty());
        for (String text : new String[]{"PKB", "Stopa bezrobocia", "Inflacja", "Eksport"}) {
            Button button = new Button(text);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pane.getChildren().add(button);
        }
        return pane;
    }

    private BorderPane border() {
        description.setText("BorderPane: top i bottom przez całą szerokość, boki przy środku. CENTER dostaje pozostałe miejsce; kontrolka musi pozwalać na wzrost.");
        BorderPane pane = new BorderPane();
        pane.setTop(tile("TOP — narzędzia")); pane.setBottom(tile("BOTTOM — status"));
        pane.setLeft(tile("LEFT")); pane.setRight(tile("RIGHT")); pane.setCenter(tile("CENTER — dane"));
        return pane;
    }

    private GridPane grid() {
        description.setText("GridPane: kolumna etykiet i rosnąca kolumna pól. Ostatni przycisk zajmuje dwie kolumny (columnSpan=2).");
        GridPane grid = new GridPane();
        grid.hgapProperty().bind(spacing.valueProperty()); grid.vgapProperty().bind(spacing.valueProperty());
        grid.alignmentProperty().bind(alignment.valueProperty());
        ColumnConstraints labels = new ColumnConstraints();
        ColumnConstraints fields = new ColumnConstraints(); fields.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labels, fields);
        String[] names = {"Zbiór", "Okres", "Jednostka"};
        for (int i = 0; i < names.length; i++) {
            TextField field = new TextField(); field.setPrefColumnCount(8);
            field.maxWidthProperty().bind(Bindings.when(grow.selectedProperty())
                    .then(Double.MAX_VALUE).otherwise(Region.USE_PREF_SIZE));
            grid.addRow(i, new Label(names[i]), field);
        }
        Button save = new Button("Zatwierdź parametry"); save.setMaxWidth(Double.MAX_VALUE);
        grid.add(save, 0, 3, 2, 1);
        return grid;
    }

    private VBox stack() {
        description.setText("StackPane: ostatni element jest na wierzchu. Nakładka może blokować kontrolki pod spodem; nie jest odpowiednikiem CardLayout.");
        TextField field = new TextField("Spróbuj edytować z nakładką i bez niej");
        VBox overlay = new VBox(8, new ProgressIndicator(), new Label("Trwa demonstracyjne ładowanie"));
        overlay.setAlignment(Pos.CENTER); overlay.getStyleClass().add("overlay");
        CheckBox visible = new CheckBox("Pokaż nakładkę"); visible.setSelected(true);
        overlay.visibleProperty().bind(visible.selectedProperty());
        overlay.managedProperty().bind(visible.selectedProperty());
        StackPane layers = new StackPane(field, overlay); VBox.setVgrow(layers, Priority.ALWAYS);
        return new VBox(10, visible, layers);
    }

    private AnchorPane anchor() {
        description.setText("AnchorPane: pole ma kotwice left i right, więc się rozciąga; przycisk pozostaje przy prawej i dolnej krawędzi.");
        TextField field = new TextField("Nazwa zbioru");
        Button button = new Button("Zapisz");
        AnchorPane.setLeftAnchor(field, 12.0); AnchorPane.setRightAnchor(field, 12.0);
        AnchorPane.setTopAnchor(field, 12.0);
        AnchorPane.setRightAnchor(button, 12.0); AnchorPane.setBottomAnchor(button, 12.0);
        return new AnchorPane(field, button);
    }

    private Accordion accordion() {
        description.setText("Accordion rozwija najwyżej jedną sekcję. ScrollPane ma fitToWidth=true: formularz dopasowuje się do viewportu, a wysokość przewijamy.");
        VBox fields = new VBox(10);
        for (int i = 1; i <= 16; i++) fields.getChildren().add(new TextField("Zmienna " + i));
        fields.setPadding(new Insets(10));
        ScrollPane scroll = new ScrollPane(fields); scroll.setFitToWidth(true);
        TitledPane variables = new TitledPane("Zmienne", scroll);
        TitledPane settings = new TitledPane("Ustawienia", new VBox(10,
                new CheckBox("Pokaż legendę"), new CheckBox("Pomiń braki danych")));
        variables.setAnimated(false); settings.setAnimated(false);
        Accordion accordion = new Accordion(variables, settings);
        accordion.setExpandedPane(variables);
        return accordion;
    }

    private VBox events() {
        description.setText("Kliknij przycisk: filtr rodzica → handler przycisku → handler rodzica. consume() w filtrze zatrzymuje propagację tego zdarzenia.");
        CheckBox consume = new CheckBox("Zatrzymaj zdarzenie w filtrze");
        TextArea log = new TextArea(); log.setEditable(false); log.setPrefRowCount(5);
        Button button = new Button("Wykonaj akcję");
        VBox box = new VBox(10, consume, button, log);
        VBox.setVgrow(log, Priority.ALWAYS);
        box.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            if (e.getTarget() == button) {
                log.appendText("1. Filtr rodzica\n");
                if (consume.isSelected()) { e.consume(); log.appendText("   consume()\n"); }
            }
        });
        button.setOnAction(e -> log.appendText("2. Handler przycisku\n"));
        box.addEventHandler(javafx.event.ActionEvent.ACTION, e -> {
            if (e.getTarget() == button) log.appendText("3. Handler rodzica\n\n");
        });
        return box;
    }

    private Label tile(String text) {
        Label label = new Label(text); label.setWrapText(true); label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); label.getStyleClass().add("tile");
        return label;
    }

    private static void applyTheme(Scene scene, boolean dark) {
        scene.getStylesheets().setAll(resource("base.css"), resource(dark ? "dark.css" : "light.css"));
    }
    private static String resource(String name) {
        return Objects.requireNonNull(LayoutExplorer.class.getResource("/css/" + name), name).toExternalForm();
    }

    /** Kontrola layoutów bez pokazywania okna. Nie zastępuje testów sprzętowego DPI. */
    private void verify() {
        alignment.setValue(Pos.TOP_LEFT); grow.setSelected(true);
        int count = 0;
        for (boolean dark : new boolean[]{false, true}) {
            for (String name : NAMES) {
                Region region = example(name);
                region.setMinSize(0, 0); region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                StackPane root = new StackPane(region);
                Scene scene = new Scene(root); applyTheme(scene, dark);
                for (int w : new int[]{360, 900}) {
                    root.resize(w, 340); root.applyCss(); root.layout();
                    if (!Double.isFinite(region.getWidth()) || region.getWidth() <= 0)
                        throw new AssertionError("Niepoprawny rozmiar: " + name);
                    count++;
                }
            }
        }
        FlowPane flow = flow();
        StackPane root = new StackPane(flow); Scene scene = new Scene(root); applyTheme(scene, false);
        root.resize(360, 340); root.applyCss(); root.layout();
        double narrowY = flow.getChildren().getLast().getLayoutY();
        root.resize(1000, 340); root.layout();
        if (narrowY <= flow.getChildren().getLast().getLayoutY())
            throw new AssertionError("FlowPane nie zawija elementów");
        System.out.println("OK: " + count + " wariantów layout/motyw/szerokość oraz zawijanie FlowPane.");
    }

    public static void main(String[] args) { launch(args); }
}
