package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import utility.GlobalEnums;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorSearchUsers extends UndoableController implements Initializable {

    @FXML
    private TableView<User> userDataTable;

    @FXML
    private TableColumn<User, String> columnName;

    @FXML
    private TableColumn<User, String> columnID;

    private final int NUMRESULTS = 30;

    @FXML
    private TextField searchEntry;

    private ObservableList<User> masterData = FXCollections.observableArrayList();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Searcher searcher = Searcher.getSearcher();

    /**
     * Initialises the data within the table to all users
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        FilteredList<User> filteredData = setupTableColumnsAndData();
        setupSearchingListener(filteredData);
        setupDoubleClickToUserEdit();
        setupRowHoverOverText();
        setupUndoRedo();
    }

    /**
     * Sets up undo redo for this screen
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>() {{
            add(searchEntry);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANSEARCHPATIENTS);
    }

    /**
     * Sets up double-click functionality for each row to open a user profile update. Opens the selected
     * users's profile view screen in a new window.
     */
    private void setupDoubleClickToUserEdit() {
        // Add double-click event to rows
        userDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && userDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    UserControl userControl = new UserControl();
                    User selected = userDataTable.getSelectionModel().getSelectedItem();
                    userControl.setTargetUser(selected);
                    FXMLLoader fxmlLoader;
                    if (selected instanceof Patient) {
                        fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    } else if (selected instanceof Clinician) {
                        fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianProfile.fxml"));
                    } else {
                        fxmlLoader = new FXMLLoader(getClass().getResource("/scene/administratorProfile.fxml"));
                    }
                    UndoableStage popUpStage = new UndoableStage();
                    screenControl.addStage(popUpStage.getUUID(), popUpStage);
                    screenControl.show(popUpStage.getUUID(), fxmlLoader.load());

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));
                }
                catch (IOException e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open user profile scene from search users table",
                            "attempted to open user edit window from search users table");
                    new Alert(Alert.AlertType.ERROR, "Unable to open user edit window", ButtonType.OK).show();
                }
            }
        });
    }

    /**
     * Sets the table columns to pull the correct data from the user objects
     *
     * @return a filtered list of users
     */
    private FilteredList<User> setupTableColumnsAndData() {
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
        FilteredList<User> filteredData = new FilteredList<>(masterData, new Predicate<User>() {
            @Override
            public boolean test(User d) {
                return true;
            }
        });

        // 2. Set the filter Predicate whenever the filter changes.
        searchEntry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            masterData.clear();
            UserTypes[] types = new UserTypes[] {UserTypes.PATIENT, UserTypes.CLINICIAN, UserTypes.ADMIN};
            List<User> results = new ArrayList<User>();
            if (newValue == null || newValue.isEmpty()) {
            	results = searcher.getDefaultResults(types);
            } else {
            	results = searcher.search(newValue,types , NUMRESULTS);
            }      
            masterData.addAll(results);
            filteredData.setPredicate(patient -> true);
        });

        // wrap the FilteredList in a SortedList.
        SortedList<User> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(userDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        userDataTable.setItems(sortedData);
        return filteredData;
    }

    /**
     * Sets the search textfield to listen for any changes and search for the entry on change
     *
     * @param filteredData the users to be filtered/searched through
     */
    private void setupSearchingListener(FilteredList<User> filteredData) {
        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> filteredData.setPredicate(user -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    List<User> results = searcher.search(newValue, new UserTypes[] {UserTypes.PATIENT, UserTypes.CLINICIAN, UserTypes.ADMIN}, NUMRESULTS);
                    return results.contains(user);
                }));
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
                if (user == null || !(user instanceof Patient)) {
                    setTooltip(null);
                }
                else if (((Patient)user).getDonations().isEmpty()) {
                    Patient patient = (Patient) user;
                    tooltip.setText(patient.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                }
                else {
                    Patient patient = (Patient) user;
                    StringBuilder tooltipText = new StringBuilder(patient.getNameConcatenated() + ". Donations: ");
                    for (GlobalEnums.Organ organ : patient.getDonations()) {
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
     * Adds all db data via constructor
     */
    public GUIAdministratorSearchUsers() {
        masterData.addAll(searcher.getDefaultResults(new UserTypes[] {UserTypes.ADMIN, UserTypes.CLINICIAN, UserTypes.PATIENT}));
    }

    public void goToAdministratorHome() {
        try {
            screenControl.show(userDataTable, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator home").show();
            userActions.log(SEVERE, "Failed to load administrator home", "Attempted to load administrator home");
        }
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        userDataTable.refresh();
    }
}
