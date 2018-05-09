package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Patient;
import model.Procedure;
import utility.GlobalEnums.Organ;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public class GUIPatientProcedures {

    @FXML
    public TableView<Procedure> previousProceduresView;

    @FXML
    public TableView<Procedure> pendingProceduresView;

    @FXML
    public TableColumn<Procedure, String> previousProcedureCol;

    @FXML
    public TableColumn<Procedure, String> previousDescriptionCol;

    @FXML
    public TableColumn<Procedure, LocalDate> previousDateCol;

    @FXML
    public TableColumn<Procedure, Set<Organ>> previousAffectedCol;

    @FXML
    public TableColumn<Procedure, String> pendingProcedureCol;

    @FXML
    public TableColumn<Procedure, String> pendingDescriptionCol;

    @FXML
    public TableColumn<Procedure, LocalDate> pendingDateCol;

    @FXML
    public TableColumn<Procedure, Set<Organ>> pendingAffectedCol;

    private Patient patient;

    public void initialize() {
//        patient = ???
        ObservableList<Procedure> previousProcedures = FXCollections.observableArrayList();
        ObservableList<Procedure> pendingProcedures = FXCollections.observableArrayList();
        for (Procedure procedure : patient.getProcedures()) {
            if (procedure.getDate().isBefore(LocalDate.now())) {
                previousProcedures.add(procedure);
            } else {
                pendingProcedures.add(procedure);
            }
        }
        previousProceduresView.setItems(previousProcedures);
        pendingProceduresView.setItems(pendingProcedures);
        previousProcedureCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSummary()));
        previousDescriptionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        previousDateCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        previousAffectedCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAffectedDonations()));
        pendingProcedureCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSummary()));
        pendingDescriptionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        pendingDateCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDate()));
        pendingAffectedCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getAffectedDonations()));
    }

}
