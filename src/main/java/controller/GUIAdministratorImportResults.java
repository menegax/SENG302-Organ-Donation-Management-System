package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUIAdministratorImportResults {
    @FXML
    private GridPane adminImportResultsPane;

    public void initialize() {

    }

    /**
     * Closes the import results window
     */
    public void close() {
        ((Stage) adminImportResultsPane.getScene().getWindow()).close();
    }
}
