package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.AdministratorDataService;
import service.ClinicianDataService;
import service.PatientDataService;
import service.TextWatcher;
import service.interfaces.IAdministratorDataService;
import service.interfaces.IClinicianDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.StatesHistoryScreen;

import java.util.ArrayList;
import java.util.List;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorSearchUsers extends UndoableController implements IWindowObserver {

    @FXML
    private TableView<User> userDataTable;

    @FXML
    private TableColumn<User, String> columnName;

    @FXML
    private TableColumn<User, String> columnID;

    @FXML
    private TextField searchEntry;

    private ObservableList<User> masterData = FXCollections.observableArrayList();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

    private IAdministratorDataService administratorDataService = new AdministratorDataService();

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    private IPatientDataService patientDataService = new PatientDataService();

    /**
     * Initialises the data within the table to all users
     */
    public void load() {
        setupTableColumnsAndData();
        TextWatcher watcher = new TextWatcher();
        searchEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                watcher.onTextChange(); //reset
            }
            try {
                watcher.afterTextChange(GUIAdministratorSearchUsers.class.getMethod("search"), this); //start timer

            } catch (NoSuchMethodException e) {
                userActions.log(SEVERE, "No method exists for search", "Attempted to search");
            }
        });
        setupDoubleClickToUserEdit();
        setupRowHoverOverText();
        setupUndoRedo();
        search();
    }

    private void search() {
        List<User> results = administratorDataService.searchUsers(searchEntry.getText());
        if (results != null) {
            masterData.clear();
            masterData.addAll(results);
        }
    }

    /**
     * Sets up undo redo for this screen
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>() {{
            add(searchEntry);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.ADMINISTRATORSEARCHUSERS, target);
    }

    /**
     * Sets up double-click functionality for each row to open a user profile update. Opens the selected
     * users's profile view screen in a new window.
     */
    private void setupDoubleClickToUserEdit() {
        // Add double-click event to rows
        userDataTable.setOnMouseClicked(click -> {
        	User selected = userDataTable.getSelectionModel().getSelectedItem();
            if (click.getClickCount() == 2 && selected != null && selected != userControl.getLoggedInUser()) {
                GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selected);
                controller.setTarget(selected);
                //Save user to local db
                if (selected instanceof Patient) {
                    Patient p = patientDataService.getPatientByNhi(((Patient) selected).getNhiNumber());
                    patientDataService.save(p);
                } else if (selected instanceof Clinician) {
                    Clinician c = clinicianDataService.getClinician(((Clinician) selected).getStaffID());
                    clinicianDataService.save(c);
                } else {
                    Administrator a = administratorDataService.getAdministratorByUsername(((Administrator) selected).getUsername());
                    administratorDataService.save(a);
                }
            }
        });
    }

    /**
     * Called when the created window from opening a profile is closed
     */
    public void windowClosed() {
        search();
    }

    /**
     * Sets the table columns to pull the correct data from the user objects
     */
    private void setupTableColumnsAndData() {
        // initialize columns
        columnName.setCellValueFactory(d -> d.getValue()
                .getNameConcatenated() != null ? new SimpleStringProperty(d.getValue()
                .getNameConcatenated()) : new SimpleStringProperty(""));
        columnID.setCellValueFactory(d -> {
            String ident;
            User user = d.getValue();
            if (user instanceof Patient) {
                ident = ((Patient) user).getNhiNumber();
            } else if (user instanceof Clinician) {
                ident = String.valueOf(((Clinician) user).getStaffID());
            } else {
                ident = ((Administrator) user).getUsername();
            }
            return new SimpleStringProperty(ident);
        });

        // wrap ObservableList in a FilteredList
        FilteredList<User> filteredData = new FilteredList<>(masterData, d -> true);

        // wrap the FilteredList in a SortedList.
        SortedList<User> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(userDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        userDataTable.setItems(sortedData);
    }

    /**
     * Adds custom hover-over text to each row in the table
     */
    private void setupRowHoverOverText() {
        userDataTable.setRowFactory(tv -> new TableRow<User>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (!(user instanceof Patient)) {
                    setTooltip(null);
                } else if (((Patient) user).getDonations().isEmpty()) {
                    Patient patient = (Patient) user;
                    tooltip.setText(patient.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                } else {
                    Patient patient = (Patient) user;
                    StringBuilder tooltipText = new StringBuilder(patient.getNameConcatenated() + ". Donations: ");
                    for (GlobalEnums.Organ organ : patient.getDonations().keySet()) {
                        tooltipText.append(organ)
                                .append(", ");
                    }
                    tooltipText = new StringBuilder(tooltipText.substring(0, tooltipText.length() - 2));
                    tooltip.setText(tooltipText.toString());
                    setTooltip(tooltip);
                }
            }
        });
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
       userDataTable.refresh();
    }
}
