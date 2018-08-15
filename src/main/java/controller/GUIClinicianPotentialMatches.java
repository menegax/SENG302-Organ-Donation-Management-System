package controller;

import data_access.factories.MySqlFactory;
import data_access.interfaces.IPatientDataAccess;
import data_access.localDAO.PatientLocalDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.RangeSlider;
import org.joda.time.Days;
import service.ClinicianDataService;
import service.OrganWaitlist;
import service.PatientDataService;
import utility.GlobalEnums;
import utility.GlobalEnums.*;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static utility.UserActionHistory.userActions;

public class GUIClinicianPotentialMatches extends TargetedController implements IWindowObserver {

    public TableView<OrganWaitlist.OrganRequest> potentialMatchesTable;
    public TableColumn<OrganWaitlist.OrganRequest, String> nhiCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> nameCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> ageCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> regionCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> addressCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> waitingTimeCol;

    public Text nameLabel;
    public Text nhiLabel;
    public Text organLabel;
    public Text bloodTypeLabel;
    public Text regionLabel;
    public Text deathLocationLabel;

    @FXML
    private GridPane filterGrid;

    @FXML
    private ComboBox<String> regionFilter;

    @FXML
    private ComboBox<String> birthGenderFilter;

    @FXML
    private Text ageLabel;

    private Organ targetOrgan;

    private ObservableList<OrganWaitlist.OrganRequest> openProfiles = FXCollections.observableArrayList();
    private ObservableList<OrganWaitlist.OrganRequest> allRequests = FXCollections.observableArrayList();

    private Map<Region, List<Region>> adjacentRegions = new HashMap<>();

    private PatientDataService patientDataService = new PatientDataService();

    private RangeSlider rangeSlider;

    private Map<FilterOption, String> filter = new HashMap<>();

    /**
     * Sets the target donor and organ for this controller and loads the data accordingly
     * @param donor the donating patient
     * @param organ the organ they are donating
     */
    public void setTarget(Patient donor, Organ organ) {
        target = donor;
        targetOrgan = organ;
        load();
    }

    /**
     * Initializes matches list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void load() {
        loadRegionDistances();
        ClinicianDataService clinicianDataService = new ClinicianDataService();
        OrganWaitlist organRequests = clinicianDataService.getOrganWaitList();
        for (OrganWaitlist.OrganRequest request: organRequests) {
            if(checkMatch(request)) {
                allRequests.add(request);
            }
        }
        setLabels();
        populateTable();
        setupDoubleClickToPatientEdit();
        setupAgeSliderListeners();
        setupFilterListeners();
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
        filterGrid.add(rangeSlider, 0, 2, 3, 1);
        rangeSlider.highValueProperty().addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s", ((int) rangeSlider.getLowValue()), String.valueOf(newValue.intValue())))));
        rangeSlider.lowValueProperty().addListener(((observable, oldValue, newValue) -> ageLabel.setText(String.format("%s - %s", String.valueOf(newValue.intValue()), (int) rangeSlider.getHighValue()))));
    }
    /**
     * Sets the labels displayed to the requirements of the donated organ
     */
    private void setLabels() {
        nameLabel.setText(target.getNameConcatenated());
        nhiLabel.setText(((Patient) target).getNhiNumber());
        organLabel.setText(targetOrgan.toString());
        bloodTypeLabel.setText(((Patient) target).getBloodGroup().toString());
        regionLabel.setText(((Patient) target).getDeathRegion().toString());
        deathLocationLabel.setText(((Patient) target).getDeathStreet() + ", " + ((Patient) target).getDeathCity());
    }

    /**
     * Checks that the provided request matches the organ being donated
     * @param request the potential match
     * @return whether the match is valid or not
     */
    private boolean checkMatch(OrganWaitlist.OrganRequest request) {
        boolean match = true;
        if (request.getRequestedOrgan() != targetOrgan || request.getBloodGroup() != ((Patient) target).getBloodGroup()) {
            match = false;
        } else if ((request.getAge() < 12 && ((Patient) target).getAge() > 12) || (request.getAge() > 12 && ((Patient) target).getAge() < 12)
                || abs(request.getAge() - ((Patient) target).getAge()) > 15) {
            match = false;
        }
        return match;
    }

    /**
     * Populates the potential matches table with the potential matches in the right order
     */
    private void populateTable() {
        nhiCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getReceiverNhi()));
        nameCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getReceiverName()));
        ageCol.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(r.getValue().getAge())));
        regionCol.setCellValueFactory(r -> {
            if (r.getValue().getRequestRegion() != null) {
                return new SimpleStringProperty(r.getValue().getRequestRegion().toString());
            }
            return null;
        });
        addressCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getAddress()));
        waitingTimeCol.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(DAYS.between(LocalDate.now(), r.getValue().getDate()))));

        // wrap ObservableList in a FilteredList
        FilteredList<OrganWaitlist.OrganRequest> filteredRequests = filterRequests();

        // wrap the FilteredList in a SortedList.
        SortedList<OrganWaitlist.OrganRequest> sortedRequests = new SortedList<>(filteredRequests);

        // bind the SortedList comparator to the TableView comparator.
        sortedRequests.setComparator((request1, request2) -> {
             if (request1.getDate().isBefore(request2.getDate())) {
                 return -1;
             } else if (request2.getDate().isBefore(request1.getDate())) {
                 return 1;
             } else {
                 return (getRegionDistance(request1.getRequestRegion(), new ArrayList<>())).compareTo((getRegionDistance(request2.getRequestRegion(), new ArrayList<>())));
             }
        });
        // add sorted (and filtered) data to the table.
        potentialMatchesTable.setItems(sortedRequests);

        regionFilter.getItems().add(GlobalEnums.NONE_ID); //for empty selection
        for (Region region : Region.values()) { //add values to region choice box
            regionFilter.getItems().add(StringUtils.capitalize(region.getValue()));
        }
        birthGenderFilter.getItems().add(GlobalEnums.NONE_ID);
        for (BirthGender birthGender: BirthGender.values()){
            birthGenderFilter.getItems().addAll(StringUtils.capitalize(birthGender.getValue()));
        }
    }

    /**
     * Story 51 functionality goes here
     * @return filtered list
     */
    private FilteredList<OrganWaitlist.OrganRequest> filterRequests() {
        return new FilteredList<>(allRequests, d -> true);
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update, ensures no duplicate profiles
     */
    private void setupDoubleClickToPatientEdit() {
        ScreenControl screenControl = ScreenControl.getScreenControl();
        // Add double-click event to rows
        potentialMatchesTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && potentialMatchesTable.getSelectionModel()
                    .getSelectedItem() != null && !openProfiles.contains(potentialMatchesTable.getSelectionModel()
                    .getSelectedItem())) {
                OrganWaitlist.OrganRequest request = potentialMatchesTable.getSelectionModel().getSelectedItem();
                try {
                    Patient selectedUser = patientDataService.getPatientByNhi(request.getReceiverNhi());
                    patientDataService.save(selectedUser);
                    GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selectedUser);
                    controller.setTarget(selectedUser);
                    openProfiles.add(request);
                } catch (Exception e) {
                    userActions.log(Level.SEVERE, "Failed to retrieve selected patient from database", new String[]{"Attempted to retrieve selected patient from database", request.getReceiverNhi()});
                }
            }
        });
    }

    /**
     * Called when a profile opened from this window is closed
     */
    public void windowClosed() {
        potentialMatchesTable.refresh();
    }

    /**
     * Gets the lowest amount of adjacent regions traversed between provided and target region
     * @param region the region to search from
     * @return how many steps away the region is from the target region
     */
    private Integer getRegionDistance(Region region, List<Region> visitedRegions) {
        if (region == ((Patient) target).getRegion()) {
            return 0;
        } else if (region == null) {
            return 100;
        } else {
            int minDistance = -1;
            for (Region adjacentRegion: adjacentRegions.get(region)) {
                if (!visitedRegions.contains(adjacentRegion)) {
                    visitedRegions.add(adjacentRegion);
                    int distance = getRegionDistance(adjacentRegion, new ArrayList<>(visitedRegions));
                    if (minDistance == -1 || distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
            return minDistance + 1;
        }
    }

    /**
     * Loads into the region distances map regions adjacent to key region
     */
    private void loadRegionDistances() {
        adjacentRegions.put(Region.NORTHLAND, new ArrayList<Region>(){{ add(Region.AUCKLAND); }});
        adjacentRegions.put(Region.AUCKLAND, new ArrayList<Region>(){{ add(Region.NORTHLAND); add(Region.WAIKATO); }});
        adjacentRegions.put(Region.WAIKATO, new ArrayList<Region>(){{ add(Region.AUCKLAND); add(Region.BAYOFPLENTY); add(Region.TARANAKI); add(Region.MANAWATU); add(Region.HAWKESBAY); }});
        adjacentRegions.put(Region.BAYOFPLENTY, new ArrayList<Region>(){{ add(Region.WAIKATO); add(Region.GISBORNE); add(Region.HAWKESBAY); }});
        adjacentRegions.put(Region.GISBORNE, new ArrayList<Region>(){{ add(Region.BAYOFPLENTY); add(Region.HAWKESBAY); }});
        adjacentRegions.put(Region.TARANAKI, new ArrayList<Region>(){{ add(Region.WAIKATO); add(Region.MANAWATU); }});
        adjacentRegions.put(Region.MANAWATU, new ArrayList<Region>(){{ add(Region.WAIKATO); add(Region.TARANAKI); add(Region.HAWKESBAY); add(Region.WELLINGTON); }});
        adjacentRegions.put(Region.HAWKESBAY, new ArrayList<Region>(){{ add(Region.WAIKATO); add(Region.BAYOFPLENTY); add(Region.GISBORNE); add(Region.MANAWATU); }});
        adjacentRegions.put(Region.WELLINGTON, new ArrayList<Region>(){{ add(Region.MANAWATU); add(Region.MARLBOROUGH); }});
        adjacentRegions.put(Region.TASMAN, new ArrayList<Region>(){{ add(Region.NELSON); add(Region.MARLBOROUGH); add(Region.WESTCOAST); }});
        adjacentRegions.put(Region.NELSON, new ArrayList<Region>(){{ add(Region.TASMAN); add(Region.MARLBOROUGH); }});
        adjacentRegions.put(Region.MARLBOROUGH, new ArrayList<Region>(){{ add(Region.WELLINGTON); add(Region.NELSON); add(Region.TASMAN); add(Region.CANTERBURY); }});
        adjacentRegions.put(Region.WESTCOAST, new ArrayList<Region>(){{ add(Region.TASMAN); add(Region.CANTERBURY); add(Region.OTAGO); add(Region.SOUTHLAND); }});
        adjacentRegions.put(Region.CANTERBURY, new ArrayList<Region>(){{ add(Region.MARLBOROUGH); add(Region.TASMAN); add(Region.WESTCOAST); add(Region.OTAGO); }});
        adjacentRegions.put(Region.OTAGO, new ArrayList<Region>(){{ add(Region.CANTERBURY); add(Region.WESTCOAST); add(Region.SOUTHLAND); }});
        adjacentRegions.put(Region.SOUTHLAND, new ArrayList<Region>(){{ add(Region.OTAGO); add(Region.WESTCOAST); }});
    }


    private void filter() {
        List<OrganWaitlist.OrganRequest> results = new ArrayList<>();
        PatientDataService patientDataService = new PatientDataService();
        PatientLocalDAO localDAO = new PatientLocalDAO();
        for (OrganWaitlist.OrganRequest organRequest : allRequests) {
            if (localDAO.matchesFilter(patientDataService.getPatientByNhi(organRequest.getReceiverNhi()), filter)) {
                results.add(organRequest);
            }
        }
        allRequests.clear();
        allRequests.addAll(results);
    }
    
    private void setupFilterListeners(){
        regionFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            filter.replace(FilterOption.REGION, filter.get(FilterOption.REGION), newValue);
            filter();
        }));

        birthGenderFilter.valueProperty().addListener(((observable, oldValue, newValue) -> {
            filter.replace(FilterOption.BIRTHGENDER, filter.get(FilterOption.BIRTHGENDER), newValue);
            filter();
        }));

        rangeSlider.onMouseReleasedProperty().addListener((observable, oldvalue, newvalue) -> {
            filter.replace(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
            filter.replace(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
            filter();

        });

        rangeSlider.setOnMouseReleased(event -> {
            filter.replace(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
            filter.replace(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
            filter();
        });
    }
}
