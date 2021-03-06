package controller;

import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.RangeSlider;
import service.ClinicianDataService;
import service.PatientDataService;
import service.TextWatcher;
import utility.CachedThreadPool;
import utility.GlobalEnums;
import utility.GlobalEnums.BirthGender;
import utility.GlobalEnums.FilterOption;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;
import utility.GlobalEnums.UndoableScreen;
import utility.TouchComboBoxSkin;
import utility.undoRedo.StatesHistoryScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class GUIClinicianSearchPatients extends UndoableController implements IWindowObserver {

	@FXML
	private GridPane pane;

    @FXML
    private TableView<Patient> patientDataTable;

    @FXML
    private TableColumn<Patient, String> columnName;

    @FXML
    private TableColumn<Patient, String> columnStatus;

    @FXML
    private TableColumn<Patient, String> columnAge;

    @FXML
    private TableColumn<Patient, String> columnBirthGender;

    @FXML
    private TableColumn<Patient, String> columnRegion;

    @FXML
    private TextField searchEntry;

    @FXML
    private TextField valueX;

    @FXML
    private Label displayY;

    @FXML
    private Text ageLabel;

    @FXML
    private CheckBox isDonorCheckbox;

    @FXML
    private CheckBox isRecieverCheckbox;

    @FXML
    private GridPane sliderGrid;

    @FXML
    private ComboBox<String> recievingFilter;

    @FXML
    private ComboBox<String> donationFilter;

    @FXML
    private ComboBox<String> birthGenderFilter;

    @FXML
    private ComboBox<String> regionFilter;

    private ObservableList<Patient> masterData = FXCollections.observableArrayList();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private RangeSlider rangeSlider;

    private Map<FilterOption, String> filter = new HashMap<>();

    private int numResults = 30;

    private int count = 0;

    private PatientDataService patientDataService = new PatientDataService();

    private ClinicianDataService clinicianDataService = new ClinicianDataService();


    /**
     * Initialises the data within the table to all patients
     */
    public void loadController() {
        displayY.setText("Display all " + count + " profiles");
        setupAgeSliderListeners();
        populateDropdowns();
        setupFilterOptions();
        setupTableColumnsAndData();
        TextWatcher watcher = new TextWatcher();
        searchEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            submitToTextWatcher(oldValue, newValue, watcher, "search");
        });
        valueX.textProperty().addListener((observable, oldValue, newValue) -> {
            submitToTextWatcher(oldValue, newValue, watcher, "updateSearch");
        });
        setupDoubleClickToPatientEdit();
        setupRowHoverOverText();

        setupUndoRedo();
        updateProfileCount();
        if (screenControl.isTouch()) {
            setupComboBoxSkins();
        }
    }

    private void submitToTextWatcher(String oldValue, String newValue, TextWatcher watcher, String methodName) {
        if (!newValue.equals(oldValue)) {
            watcher.onTextChange();
        }
        try {
            watcher.afterTextChange(GUIClinicianSearchPatients.class.getMethod(methodName), this);

        } catch (NoSuchMethodException e) {
            userActions.log(SEVERE, "No method exists for search", "Attempted to search");
        }
    }

    /**
     * Sets the ComboBox skins for ComboBoxes on this screen
     */
    private void setupComboBoxSkins() {
        new TouchComboBoxSkin(birthGenderFilter, (Pane) screenControl.getTouchParent(pane));
        new TouchComboBoxSkin(donationFilter, (Pane) screenControl.getTouchParent(pane));
        new TouchComboBoxSkin(recievingFilter, (Pane) screenControl.getTouchParent(pane));
        new TouchComboBoxSkin(regionFilter, (Pane) screenControl.getTouchParent(pane));
    }


    /**
     * Sets up undo redo for this screen
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>() {{
            add(birthGenderFilter);
            add(searchEntry);
            add(recievingFilter);
            add(isRecieverCheckbox);
            add(isDonorCheckbox);
            add(regionFilter);
            add(donationFilter);
            add(valueX);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, UndoableScreen.CLINICIANSEARCHPATIENTS, target);
    }


    /**
     * Sets up double-click functionality for each row to open a patient profile update. Opens the selected
     * patient's profile view screen in a new window.
     */
    private void setupDoubleClickToPatientEdit() {
        // Add double-click event to rows
        patientDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && patientDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                Patient selected = patientDataTable.getSelectionModel()
                        .getSelectedItem();
                patientDataService.save(patientDataService.getPatientByNhi(selected.getNhiNumber())); //save to local
                Parent parent = screenControl.getTouchParent(pane);
                GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selected, parent);
                controller.setTarget(selected);
            }
        });
    }


    /**
     * Called when the profile window of a patient opened by this controller is closed
     */
    public void windowClosed() {
        search();
    }


    /**
     * Sets the table columns to pull the correct data from the patient objects
     *
     * @return a filtered list of patients
     */
    private void setupTableColumnsAndData() {
        // initialize columns
        columnName.setCellValueFactory(d -> d.getValue()
                .getNameConcatenated() != null ? new SimpleStringProperty(d.getValue()
                .getNameConcatenated()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue()
                .getAge())));
        columnStatus.setCellValueFactory(d -> {
            Patient patient = d.getValue();
            if (patient.getDonations()
                    .keySet()
                    .size() > 0) {
                return new SimpleStringProperty(patient.getRequiredOrgans()
                        .size() > 0 ? "Donating & Receiving" : "Donating");
            }
            else if (patient.getRequiredOrgans()
                    .keySet()
                    .size() > 0 && patient.getDeathDate() == null) {
                return new SimpleStringProperty("Receiving");
            }
            return new SimpleStringProperty("--");
        });
        columnBirthGender.setCellValueFactory(d -> d.getValue()
                .getBirthGender() != null ? new SimpleStringProperty(d.getValue()
                .getBirthGender()
                .toString()) : new SimpleStringProperty(""));
        columnRegion.setCellValueFactory(d -> d.getValue()
                .getRegion() != null ? new SimpleStringProperty(d.getValue()
                .getRegion()
                .toString()) : new SimpleStringProperty(""));

        // wrap ObservableList in a FilteredList
        FilteredList<Patient> filteredData = new FilteredList<>(masterData, d -> true);
        filteredData.setPredicate(patient -> true);
        // wrap the FilteredList in a SortedList.
        SortedList<Patient> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(patientDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        patientDataTable.setItems(sortedData);
    }


    @SuppressWarnings("WeakerAccess")
    public void search() {
        List<Patient> results = clinicianDataService.searchPatients(searchEntry.getText(), filter, numResults);
        masterData.clear();
        masterData.addAll(results);
        updateProfileCount();
    }


    private void updateProfileCount() {
        CachedThreadPool cachedThreadPool = CachedThreadPool.getCachedThreadPool();
        ExecutorService service = cachedThreadPool.getThreadService();
        Future task = service.submit(() -> {
            count = clinicianDataService.getPatientCount();
        });
        try {
            task.get();
            if (count > 100) {
                displayY.setText("Display 100 profiles");
            }
            else {
                displayY.setText("Display all " + count + " profiles");
            }
        }
        catch (InterruptedException | ExecutionException e) {
            systemLogger.log(Level.WARNING, "Error receiving profile count");
        }
    }


    @SuppressWarnings("weakeraccess")
    @FXML
    public void updateSearch() {
        masterData.clear();
        String search = searchEntry.getText();
        String numResultsString = valueX.getText();
        if (!numResultsString.equals("")) {
            try {
                numResults = Integer.parseInt(numResultsString);
            }
            catch (NumberFormatException e) {
                new Alert((Alert.AlertType.ERROR),
                        valueX.getText() + " is not a valid number. \nPlease enter a valid number for the number of search results.").show();
            }
        }
        else {
            numResults = 30;
        }
        if (numResults > 0) {
            search();
        }
        //displayY.setText( "Display all " + searcher.getDefaultResults(new UserTypes[]{UserTypes.PATIENT}, null).size() + " profiles" );
    }


    /**
     * Displays only the first X profiles to the search patients table if more than X results from search
     */
    @FXML
    private void displayAllResults() {
        if (count > 100) {
            numResults = 100;
        }
        else {
            numResults = count;
        }
        search();
    }


    /**
     * Gets the number of profiles that have been returned from a patient search by clinician
     *
     * @return An integer value representing the total number of profiles returned from the search
     */
    private int getProfileCount() {
        return count;
    }


    /**
     * Adds custom hover-over text to each row in the table
     */
    private void setupRowHoverOverText() {
        patientDataTable.setRowFactory(tv -> new TableRow<Patient>() {
            private Tooltip tooltip = new Tooltip();


            @Override
            public void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (patient == null) {
                    setTooltip(null);
                }
                else if (patient.getDonations()
                        .isEmpty()) {

                    tooltip.setText(patient.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                }
                else {
                    StringBuilder tooltipText = new StringBuilder(patient.getNameConcatenated() + ". Donations: ");
                    for (Organ organ : patient.getDonations()
                            .keySet()) {
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
     * Adds listener to the age label to update when slider is moved
     */
    private void setupAgeSliderListeners() {
        rangeSlider = new RangeSlider();
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setPadding(new Insets(10, 150, 0, 50));
        rangeSlider.setMaxWidth(10000);
        rangeSlider.setMax(100);
        rangeSlider.setLowValue(0);
        rangeSlider.setHighValue(100);
        rangeSlider.setShowTickMarks(true);

        sliderGrid.add(rangeSlider, 0, 4, 3, 1);

        rangeSlider.highValueProperty()
                .addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s",
                        ((int) rangeSlider.getLowValue()),
                        String.valueOf(newValue.intValue())))));

        rangeSlider.lowValueProperty()
                .addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s",
                        String.valueOf(newValue.intValue()),
                        (int) rangeSlider.getHighValue()))));
    }


    /**
     * Resets the filter selections
     */
    @FXML
    public void clearFilterOptions() {
        valueX.setText("30");
        numResults = 30;
        recievingFilter.getSelectionModel()
                .select(GlobalEnums.NONE_ID);
        donationFilter.getSelectionModel()
                .select(GlobalEnums.NONE_ID);
        regionFilter.getSelectionModel()
                .select(GlobalEnums.NONE_ID);
        recievingFilter.getSelectionModel()
                .select(GlobalEnums.NONE_ID);
        birthGenderFilter.getSelectionModel()
                .select(GlobalEnums.NONE_ID);
        rangeSlider.setLowValue(0);
        rangeSlider.setHighValue(100);
        searchEntry.clear();
        isRecieverCheckbox.setSelected(false);
        isDonorCheckbox.setSelected(false);
        search();
    }


    /**
     * Populate dropdowns with enum values
     */
    private void populateDropdowns() {
        regionFilter.getItems()
                .add(GlobalEnums.NONE_ID); //for empty selection
        for (Region region : Region.values()) { //add values to region choice box
            regionFilter.getItems()
                    .add(StringUtils.capitalize(region.getValue()));
        }
        donationFilter.getItems()
                .add(GlobalEnums.NONE_ID);
        for (Organ organ : Organ.values()) {
            donationFilter.getItems()
                    .add(StringUtils.capitalize(organ.getValue()));
        }
        recievingFilter.getItems()
                .add(GlobalEnums.NONE_ID);
        for (Organ organ : Organ.values()) {
            recievingFilter.getItems()
                    .add(StringUtils.capitalize(organ.getValue()));
        }
        birthGenderFilter.getItems()
                .add(GlobalEnums.NONE_ID);
        for (BirthGender gender : BirthGender.values()) {
            birthGenderFilter.getItems()
                    .add(StringUtils.capitalize(gender.getValue()));
        }
    }


    /**
     * Add listeners to the filter options and set up default filter (not selected)
     */
    private void setupFilterOptions() {
        filter.put(FilterOption.REGION, GlobalEnums.NONE_ID);
        filter.put(FilterOption.DONATIONS, GlobalEnums.NONE_ID);
        filter.put(FilterOption.REQUESTEDDONATIONS, GlobalEnums.NONE_ID);
        filter.put(FilterOption.AGEUPPER, "100");
        filter.put(FilterOption.AGELOWER, "0");
        filter.put(FilterOption.BIRTHGENDER, GlobalEnums.NONE_ID);
        filter.put(FilterOption.DONOR, String.valueOf(isDonorCheckbox.isSelected()));
        filter.put(FilterOption.RECIEVER, String.valueOf(isRecieverCheckbox.isSelected()));

        //3.
        regionFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.REGION, filter.get(FilterOption.REGION), newValue);
                    search();
                }));

        //4.
        donationFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.DONATIONS, filter.get(FilterOption.DONATIONS), newValue);
                    search();
                }));

        //5.
        recievingFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.REQUESTEDDONATIONS, filter.get(FilterOption.REQUESTEDDONATIONS), newValue);
                    search();
                }));

        //6.
        birthGenderFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.BIRTHGENDER, filter.get(FilterOption.BIRTHGENDER), newValue);
                    search();
                }));

        isDonorCheckbox.selectedProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.DONOR, filter.get(FilterOption.DONOR), newValue.toString());
                    search();
                }));

        isRecieverCheckbox.selectedProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.replace(FilterOption.RECIEVER, filter.get(FilterOption.RECIEVER), newValue.toString());
                    search();
                }));

        rangeSlider.onMouseReleasedProperty()
                .addListener((observable, oldvalue, newvalue) -> {
                    filter.replace(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
                    filter.replace(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
                    search();
                });

        rangeSlider.setOnMouseReleased(event -> {
            filter.replace(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
            filter.replace(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
            search();
        });
    }


    /**
     * View patients from table on the map
     * Sets the patients list in the JavaScript to custom set
     * Opens the map and loads
     */
    @FXML
    public void viewOnMap() {
        List<Patient> patients = new ArrayList<>(masterData);

        if (screenControl.getMapOpen()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to repopulate the map?", ButtonType.OK, ButtonType.NO);
            alert.show();

            alert.getDialogPane()
                    .lookupButton(ButtonType.OK)
                    .addEventFilter(ActionEvent.ACTION, event -> {
                        populateMap(patients);
                    });
        }
        else {
            statesHistoryScreen.getUndoableWrapper()
                    .getGuiHome()
                    .openMap();
            populateMap(patients);
        }
    }


    /**
     * Populates the map with the provided collection of patients
     *
     * @param patients the patients to populate the map with
     */
    private void populateMap(Collection<Patient> patients) {
        screenControl.setIsCustomSetMap(true);
        screenControl.getMapController()
                .setPatients(patients);
        screenControl.setMapOpen(true);
    }
}
