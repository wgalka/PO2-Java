package pl.uczelnia.lab2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MetricCard extends VBox {
    private final StringProperty title = new SimpleStringProperty(this, "title", "");
    private final StringProperty value = new SimpleStringProperty(this, "value", "");

    public MetricCard(String title, String value) {
        Label titleLabel = new Label();
        titleLabel.textProperty().bind(this.title);
        titleLabel.getStyleClass().add("metric-title");

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(this.value);
        valueLabel.getStyleClass().add("metric-value");
        valueLabel.setWrapText(true);

        getStyleClass().add("metric-card");
        setSpacing(5);
        setPadding(new Insets(12, 16, 12, 16));
        setMinWidth(160);
        setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(titleLabel, valueLabel);
        setTitle(title);
        setValue(value);
    }

    public StringProperty titleProperty() { return title; }
    public StringProperty valueProperty() { return value; }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public String getValue() { return value.get(); }
    public void setValue(String value) { this.value.set(value); }
}
