package pl.uczelnia.lab2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Observation {
    private final IntegerProperty year = new SimpleIntegerProperty(this, "year");
    private final StringProperty region = new SimpleStringProperty(this, "region");
    private final DoubleProperty rate = new SimpleDoubleProperty(this, "rate");
    private final BooleanProperty includedInAnalysis =
            new SimpleBooleanProperty(this, "includedInAnalysis", true);

    public Observation(int year, String region, double rate) {
        setYear(year);
        setRegion(region);
        setRate(rate);
    }

    public IntegerProperty yearProperty() { return year; }
    public StringProperty regionProperty() { return region; }
    public DoubleProperty rateProperty() { return rate; }
    public BooleanProperty includedInAnalysisProperty() { return includedInAnalysis; }

    public int getYear() { return year.get(); }
    public void setYear(int value) { year.set(value); }
    public String getRegion() { return region.get(); }
    public void setRegion(String value) { region.set(value); }
    public double getRate() { return rate.get(); }
    public void setRate(double value) { rate.set(value); }
    public boolean isIncludedInAnalysis() { return includedInAnalysis.get(); }
    public void setIncludedInAnalysis(boolean value) { includedInAnalysis.set(value); }
}
