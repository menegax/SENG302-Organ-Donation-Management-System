package controller;

import static utility.UserActionHistory.userActions;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IPatientDataAccess;
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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.RangeSlider;
import model.User;
import utility.CachedThreadPool;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import utility.GlobalEnums.UserTypes;
import utility.Searcher;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

public class GUIClinicianSearchPatients extends UndoableController implements Initializable {

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

    private Searcher searcher = Searcher.getSearcher();

    private RangeSlider rangeSlider;

    private Map<FilterOption, String> filter = new HashMap<>();

    private int numResults = 30;

    IPatientDataAccess patientDataAccess;

    /**
     * Initialises the data within the table to all patients
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        DAOFactory factory = DAOFactory.getDAOFactory(FactoryType.MYSQL);
        patientDataAccess = factory.getPatientDataAccess();
    	displayY.setText( "Display all " + searcher.getDefaultResults(new UserTypes[]{UserTypes.PATIENT}, null).size() + " profiles" );
        setupAgeSliderListeners();
        populateDropdowns();
        setupFilterOptions();
        List<User> defaultUsers = searcher.getDefaultResults(new UserTypes[] {UserTypes.PATIENT}, filter);
        List<Patient> defaultPatients = new ArrayList<>();
        for (User user: defaultUsers) {
            defaultPatients.add((Patient)user);
        }
        masterData.addAll(defaultPatients);
        FilteredList<Patient> filteredData = setupTableColumnsAndData();
        setupSearchingListener(filteredData);
        setupNumResultsListener(filteredData);
        setupDoubleClickToPatientEdit();
        setupRowHoverOverText();
        searchEntry.setPromptText( "There are " + getProfileCount() + " profiles" );

        setupUndoRedo();
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
        statesHistoryScreen = new StatesHistoryScreen(controls, UndoableScreen.CLINICIANSEARCHPATIENTS);
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
                try {
                    UserControl userControl = new UserControl();
                    userControl.setTargetUser(patientDataTable.getSelectionModel().getSelectedItem());
                    DAOFactory daoFactory = DAOFactory.getDAOFactory(FactoryType.LOCAL);
                    daoFactory.getPatientDataAccess().savePatients(new ArrayList<Patient>(){{
                        add(patientDataTable.getSelectionModel().getSelectedItem());}});
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/home.fxml"));
                    UndoableStage popUpStage = new UndoableStage();
                    //Set initial popup dimensions
                    popUpStage.setWidth(1150);
                    popUpStage.setHeight(700);
                    screenControl.addStage(popUpStage.getUUID(), popUpStage);
                    screenControl.show(popUpStage.getUUID(), fxmlLoader.load());

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(() -> {
                        masterData.clear();
                        Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                                numResults, filter).forEach(x ->  masterData.add((Patient)x));
                    }));
                }
                catch (IOException e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open patient profile scene from search patients table",
                            "attempted to open patient edit window from search patients table");
                    new Alert(Alert.AlertType.ERROR, "Unable to open patient edit window", ButtonType.OK).show();
                }
            }
        });
    }

    /**
     * Sets the table columns to pull the correct data from the patient objects
     *
     * @return a filtered list of patients
     */
    private FilteredList<Patient> setupTableColumnsAndData() {
        // initialize columns
        columnName.setCellValueFactory(d -> d.getValue()
                .getNameConcatenated() != null ? new SimpleStringProperty(d.getValue()
                .getNameConcatenated()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue()
                .getAge())));
        columnStatus.setCellValueFactory(d -> {
            Patient patient = d.getValue();
            if (patient.getDonations().size() > 0) {
                if (patient.getRequiredOrgans().size() > 0) {
                    return new SimpleStringProperty("Donating & Receiving");
                } else {
                    return new SimpleStringProperty("Donating");
                }
            } else if (patient.getRequiredOrgans().size() > 0) {
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

        // 2. Set the filter Predicate whenever the filter changes.
        searchEntry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            masterData.clear();
            //List<User> results = searcher.search(newValue, new UserTypes[]{UserTypes.PATIENT}, numResults, filter);
            //for (User user : results) {
                //masterData.add((Patient) user);
            //}

            CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();
            ExecutorService service = pool.getThreadService();
            service.submit(() -> {
                List<Patient> results = patientDataAccess.searchPatient(searchEntry.getText(), null, numResults);
                filteredData.setPredicate(patient -> true);
                masterData.addAll(results);
            });
            Platform.runLater(() -> {
                patientDataTable.refresh();
            });

        });

        setupFilterOptions();

        // wrap the FilteredList in a SortedList.
        SortedList<Patient> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(patientDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        patientDataTable.setItems(sortedData);
        return filteredData;
    }

    /**
     * Sets the search textfield to listen for any changes and search for the entry on change
     *
     * @param filteredData the patients to be filtered/searched through
     */
    private void setupSearchingListener(FilteredList<Patient> filteredData) {
    	UserTypes[] types = new UserTypes[]{UserTypes.PATIENT};
    	masterData.clear();
    	List<Patient> tempPatients = new ArrayList<>();
    	List<User> users = searcher.getDefaultResults(types, filter);
    	for (User user: users) {
    		tempPatients.add((Patient)user);
    	}
    	//masterData.addAll(tempPatients);
        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> filteredData.setPredicate(patient -> {

                    // If filter text is empty, display all persons.
                    int numResults = 30;
                    String numResultsString = valueX.getText();
                    if (!numResultsString.equals("")) {
            	        try {
            	        	numResults = Integer.parseInt(numResultsString);
            	        } catch (NumberFormatException e) {
            	        	new Alert((Alert.AlertType.ERROR), valueX.getText() + " is not a valid number. \nPlease enter a valid number for the number of search results.").show();
            	        }
                    }
                    if (newValue.toLowerCase().equals( "male" ) || newValue.toLowerCase().equals("female")) {
                        return patient.getBirthGender().getValue().toLowerCase().equals( newValue.toLowerCase() ); // ------------------------------this is where it fails!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                    filteredData.setPredicate(patientCheck -> true);
                    //List<User> results = searcher.search(newValue, new UserTypes[] {UserTypes.PATIENT}, numResults, filter);
                    List<Patient> results = patientDataAccess.searchPatient(newValue, null, numResults);
                    return results.contains(patient);
                }));
    }


    /**
     * Sets the search textfield to listen for any changes and search for the entry on change
     *
     * @param filteredData the patients to be filtered/searched through
     */
    private void setupNumResultsListener(FilteredList<Patient> filteredData) {
        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> filteredData.setPredicate(patient -> {

                    int numResults = 30;
                    String search = searchEntry.getText();
                    String numResultsString = valueX.getText();
                    if (!numResultsString.equals("")) {
            	        try {
            	        	numResults = Integer.parseInt(numResultsString);
            	        } catch (NumberFormatException e) {
            	        	new Alert((Alert.AlertType.ERROR), valueX.getText() + " is not a valid number. \nPlease enter a valid number for the number of search results.").show();
            	        }
                    }
                    filteredData.setPredicate(patientCheck -> true);
                    if (numResults > 0) {
                    	//return searcher.search(search, new UserTypes[]{UserTypes.PATIENT}, numResults, filter)
                    			//.contains(patient);
                        return patientDataAccess.searchPatient(search, null, numResults).contains(patient);
                    }
                    return false;
                }));
    }

    @FXML
    private void updateSearch() {
    	masterData.clear();
        String search = searchEntry.getText();
        String numResultsString = valueX.getText();
        if (!numResultsString.equals("")) {
	        try {
	        	numResults = Integer.parseInt(numResultsString);
            } catch (NumberFormatException e) {
	        	new Alert((Alert.AlertType.ERROR), valueX.getText() + " is not a valid number. \nPlease enter a valid number for the number of search results.").show();
	        }
        } else {
            numResults = 30;
        }
        if (numResults > 0) {
            //for (User user : searcher.search(search, new UserTypes[]{UserTypes.PATIENT}, numResults, filter)) {
                //masterData.add((Patient) user);
            //}
            masterData.clear();
            masterData.addAll(patientDataAccess.searchPatient(search, null, numResults));
        	patientDataTable.setItems(masterData);
        }
        displayY.setText( "Display all " + searcher.getDefaultResults(new UserTypes[]{UserTypes.PATIENT}, null).size() + " profiles" );
    }

    /**
     * Displays only the first X profiles to the search patients table if more than X results from search
     */
    @FXML
    private void displayAllResults() {
    	masterData.clear();
    	for (User user : searcher.getDefaultResults(new UserTypes[]{UserTypes.PATIENT}, null)) {
    	    masterData.add((Patient)user);
        }
        patientDataTable.setItems(masterData);
    }

    /**
     * Gets the number of profiles that have been returned from a patient search by clinician
     * @return An integer value representing the total number of profiles returned from the search
     */
    private int getProfileCount() {
       return searcher.getDefaultResults(new UserTypes[]{UserTypes.PATIENT}, null).size();
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
                else if (patient.getDonations().isEmpty()) {

                    tooltip.setText(patient.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                }
                else {
                    StringBuilder tooltipText = new StringBuilder(patient.getNameConcatenated() + ". Donations: ");
                    for (Organ organ : patient.getDonations()) {
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

        rangeSlider.highValueProperty().addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s", ((int) rangeSlider.getLowValue()), String.valueOf(newValue.intValue())))));

        rangeSlider.lowValueProperty().addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s", String.valueOf(newValue.intValue()), (int) rangeSlider.getHighValue()))));
    }

    /**
     * Resets the filter selections
     */
    @FXML
    public void clearFilterOptions() {
        valueX.setText("30");
        updateSearch();
        recievingFilter.getSelectionModel().select(GlobalEnums.NONE_ID);
        donationFilter.getSelectionModel().select(GlobalEnums.NONE_ID);
        regionFilter.getSelectionModel().select(GlobalEnums.NONE_ID);
        recievingFilter.getSelectionModel().select(GlobalEnums.NONE_ID);
        birthGenderFilter.getSelectionModel().select(GlobalEnums.NONE_ID);
        rangeSlider.setLowValue(0);
        rangeSlider.setHighValue(100);
        searchEntry.clear();
        isRecieverCheckbox.setSelected(false);
        isDonorCheckbox.setSelected(false);
    }


    /**
     * Populate dropdowns with enum values
     */
    private void populateDropdowns() {
        regionFilter.getItems().add(GlobalEnums.NONE_ID); //for empty selection
        for (Region region : Region.values()) { //add values to region choice box
            regionFilter.getItems().add(StringUtils.capitalize(region.getValue()));
        }
        donationFilter.getItems().add(GlobalEnums.NONE_ID);
        for (Organ organ : Organ.values()) {
            donationFilter.getItems().add(StringUtils.capitalize(organ.getValue()));
        }
        recievingFilter.getItems().add(GlobalEnums.NONE_ID);
        for (Organ organ : Organ.values()) {
            recievingFilter.getItems().add(StringUtils.capitalize(organ.getValue()));
        }
        birthGenderFilter.getItems().add(GlobalEnums.NONE_ID);
        for (BirthGender gender : BirthGender.values()) {
            birthGenderFilter.getItems().add(StringUtils.capitalize(gender.getValue()));
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
        filter.put(FilterOption.RECIEVER,String.valueOf(isRecieverCheckbox.isSelected()));

        //3.
        regionFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.REGION, filter.get(FilterOption.REGION), newValue);
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        //4.
        donationFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.DONATIONS, filter.get(FilterOption.DONATIONS), newValue);
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        //5.
        recievingFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.REQUESTEDDONATIONS, filter.get(FilterOption.REQUESTEDDONATIONS), newValue);
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        //6.
        birthGenderFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.BIRTHGENDER, filter.get(FilterOption.BIRTHGENDER), newValue);
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        isDonorCheckbox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.DONOR, filter.get(FilterOption.DONOR), newValue.toString());
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        isRecieverCheckbox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.RECIEVER, filter.get(FilterOption.RECIEVER), newValue.toString());
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        rangeSlider.highValueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.AGEUPPER, filter.get(FilterOption.AGEUPPER), String.valueOf(newValue.intValue()));
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));

        rangeSlider.lowValueProperty().addListener(((observable, oldValue, newValue) -> {
            masterData.clear();
            filter.replace(FilterOption.AGELOWER, filter.get(FilterOption.AGELOWER), String.valueOf(newValue.intValue()));
            Searcher.getSearcher().search(searchEntry.getText(),new UserTypes[] {UserTypes.PATIENT},
                    numResults, filter).forEach(x ->  masterData.add((Patient)x));
        }));
    }
}
