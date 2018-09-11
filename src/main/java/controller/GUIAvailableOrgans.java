package controller;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.CachedThreadPool;
import utility.ExpiryObservable;
import utility.GlobalEnums.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import model.Patient;
import utility.ProgressBarCustomTableCell;
import utility.ProgressTask;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIAvailableOrgans extends UndoableController implements IWindowObserver {

    private final float NUM_ROWS_PER_PAGE = 10;

    @FXML
    private TableView<PatientOrgan> availableOrgansTableView;

    @FXML
    private TableColumn<PatientOrgan, String> patientCol;

    @FXML
    private TableColumn<PatientOrgan, String> organCol;

    @FXML
    private TableColumn<PatientOrgan, String> locationCol;

    @FXML
    private TableColumn<PatientOrgan, String> deathCol;

    @FXML
    private TableColumn<PatientOrgan, String> expiryCol;

    @FXML
    private TableColumn<PatientOrgan, ProgressTask> organExpiryProgressCol;

    @FXML
    private Button potentialMatchesBtn;

    private ObservableList<PatientOrgan> masterData = FXCollections.observableArrayList();

    private ObservableList<PatientOrgan> filterData = FXCollections.observableArrayList();

    private SortedList<PatientOrgan> sortedData;

    @FXML
    private Pagination pagination;

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Adds organ expiry observable
     */
    public GUIAvailableOrgans() {
        ExpiryObservable.getInstance().addObserver((o, arg) -> {
            filterData.remove(arg);
            masterData.remove(arg);
            onSort(); //grab any new records to replace the missing
            Platform.runLater(() -> {
                pagination.setPageCount(getPageCount()); //re-update the page count
            });
        });
    }

    public void load() {
        for (PatientOrgan po : masterData) {
            if (po.getProgressTask() != null) {
                po.getProgressTask().setInterrupted();
            }
        }
        masterData.clear();
        CachedThreadPool.getCachedThreadPool().getThreadService().shutdownNow();
        List<Patient> deadPatients = patientDataService.getDeadPatients();
        for (Patient patient : deadPatients) {
            if (patient.getDeathDate() != null) {
                for (Organ organ : patient.getDonations()) {
                    PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                    if (!masterData.contains(patientOrgan)) {
                        if (patientOrgan.timeRemaining() < 0) {
                            masterData.add(patientOrgan);
                        }
                    }
                }
            }
        }
        populateTable();
    }

    /**
     * Called whenever the onSort TableView event is fired.
     * Overrides the sorting comparator when appropriate (i.e when the table is being sorted based on the expiry col or
     * progress bar col. Whenever one of these columns are being sorted on, the items in the table are sorted based on
     * the timeRemaining attribute.
     */
    @FXML
    public void onSort() {
        //Create ascending and descending comparators
        Comparator<PatientOrgan> comparatorAscending = (o1, o2) -> Long.compare(o2.timeRemaining(), o1.timeRemaining());
        Comparator<PatientOrgan> comparatorDescending = Comparator.comparingLong(PatientOrgan::timeRemaining);
        ObjectProperty<Comparator<? super PatientOrgan>> objectPropertyAsc = new SimpleObjectProperty<>(comparatorAscending);
        ObjectProperty<Comparator<? super PatientOrgan>> objectPropertyDesc = new SimpleObjectProperty<>(comparatorDescending);
        sortedData.comparatorProperty().unbind(); //Clear the current comparator property
        if (availableOrgansTableView.getSortOrder().size() == 0) { //Called after the third consecutive click on a table column header
            sortedData.comparatorProperty().bind(objectPropertyAsc);
            availableOrgansTableView.setSortPolicy(param -> true);
        } else {
            boolean sortingByExpiry = false;
            boolean isAscending = true;
            ObservableList<TableColumn<PatientOrgan, ?>> sortPolicies = availableOrgansTableView.getSortOrder();
            //Search the sort policies to see if any of the tablecolumns being sorted is either the progress bar or expiry time
            for (TableColumn<PatientOrgan, ?> tableColumn : sortPolicies) {
                if (tableColumn.getId().equals("expiryCol") || tableColumn.getId().equals("organExpiryProgressCol")) {
                    sortingByExpiry = true;
                    //Get the sort order of the table column
                    isAscending = tableColumn.getSortType() == TableColumn.SortType.ASCENDING;
                }
            }
            if (sortingByExpiry) {
                if (!isAscending) {
                    sortedData.comparatorProperty().bind(objectPropertyDesc);
                } else {
                    sortedData.comparatorProperty().bind(objectPropertyAsc);
                }
            } else {
                sortedData.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());
            }
            availableOrgansTableView.setSortPolicy(param -> true);
        }
       updateTable(pagination.getCurrentPageIndex());
    }


    /**
     * Populates waiting list table with all patients waiting to receive an organ
     */
    private void populateTable() {
        sortedData = new SortedList<>(masterData);
        onSort();
        // initialize columns
        patientCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getNhiNumber()));
        organCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getOrgan()
                .toString()));

        locationCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getDeathLocationConcat()));
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getDeathDate()
                .toString()));

        //expiry
        expiryCol.setCellValueFactory(r -> {
            if (r.getValue().getProgressTask() == null) {
                return null;
            }
            return r.getValue().getProgressTask().messageProperty();
        });
        organExpiryProgressCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getProgressTask()));
        organExpiryProgressCol.setCellFactory(cb -> ProgressBarCustomTableCell.getCell(organExpiryProgressCol));

        // add sorted (and filtered) data to the table.
        availableOrgansTableView.setItems(filterData);
        availableOrgansTableView.setVisible(true);
        availableOrgansTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                potentialMatchesBtn.setDisable(false);
            } else {
                potentialMatchesBtn.setDisable(true);
            }
        });
        setUpDoubleClickToPatientEdit();
        pagination.setPageCount(getPageCount());
        pagination.currentPageIndexProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                updateTable(newValue.intValue());
            }
        }));
    }


    /**
     * Updates the page items on the current page
     * @param index - index of the page number we are looking at
     */
    private void updateTable(int index) {
        int endIndex;
        int numberToDisplay;
        //kill all tasks on previous page
        filterData.forEach(x -> {
            if (x.getProgressTask() != null) {
                x.getProgressTask().setInterrupted();
            }
        });
        if ((int) NUM_ROWS_PER_PAGE * (index + 1) <= masterData.size()) {
            endIndex = (int) NUM_ROWS_PER_PAGE * (index + 1);
            numberToDisplay = (int)NUM_ROWS_PER_PAGE;
        } else {
            endIndex = sortedData.size();
            numberToDisplay = (int) (masterData.size() % NUM_ROWS_PER_PAGE); //remaining items
        }
        filterData.clear();
        filterData.addAll(FXCollections.observableArrayList(sortedData.subList(endIndex - numberToDisplay, endIndex)));
        //start the progress tasks on current page
        filterData.forEach(PatientOrgan::startTask);
    }

    /**
     * Gets the page count to show
     * @return - page count for the pagination
     */
    private int getPageCount(){
        return (int) Math.ceil((float)masterData.size()/NUM_ROWS_PER_PAGE);
    }


    /**
     * Sets up double-click functionality for each row to open a patient profile update. Opens the selected
     * patient's profile view screen in a new window.
     */
    private void setUpDoubleClickToPatientEdit() {
        availableOrgansTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && availableOrgansTableView.getSelectionModel()
                    .getSelectedItem() != null) {
                Patient selected = availableOrgansTableView.getSelectionModel()
                        .getSelectedItem()
                        .getPatient();
                GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selected);
                controller.setTarget(selected);
                patientDataService.save(patientDataService.getPatientByNhi(selected.getNhiNumber())); //save to local
            }
        });
    }


    /**
     * Opens the potential matches table for the selected organ
     */
    @FXML
    public void viewPotentialMatches() {
        PatientOrgan selected = availableOrgansTableView.getSelectionModel()
                .getSelectedItem();
        if (selected == null) {
            userActions.log(Level.WARNING, "Please select a organ to match", "Attempted to view available matches without selecting an organ");
        } else if (selected.getPatient().getBloodGroup() == null) {
            userActions.log(Level.WARNING, "Selected donor does not have a blood group set. Please set a blood group.", "Attempted to view available matches for a donor without a blood group");
        } else {
            GUIClinicianPotentialMatches controller = (GUIClinicianPotentialMatches) screenControl.show("/scene/clinicianPotentialMatches.fxml", false, null, selected.getPatient());
            controller.setTarget(selected.getPatient(), selected.getOrgan());
        }
    }


    /**
     * Refreshes the table when a profile opened by this controller
     */
    public void windowClosed() {
        load();
        availableOrgansTableView.refresh();
    }
}
