package pl.uczelnia.lab2;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservationModel {
    private final ObservableList<Observation> observations =
            FXCollections.observableArrayList(observation -> new Observable[]{
                    observation.yearProperty(), observation.regionProperty(), observation.rateProperty(),
                    observation.includedInAnalysisProperty()
            });

    public ObservableList<Observation> getObservations() {
        return observations;
    }
}
