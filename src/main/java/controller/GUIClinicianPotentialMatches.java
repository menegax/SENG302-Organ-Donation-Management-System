package controller;

import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import com.google.maps.model.LatLng;
import data_access.localDAO.PatientLocalDAO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.converter.LocalTimeStringConverter;
import model.Patient;
import model.PatientOrgan;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.RangeSlider;
import service.APIGoogleMaps;
import service.ClinicianDataService;
import service.OrganWaitlist;
import service.PatientDataService;
import utility.*;
import utility.GlobalEnums.BirthGender;
import utility.GlobalEnums.FilterOption;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

public class GUIClinicianPotentialMatches extends TargetedController implements IWindowObserver, TouchscreenCapable {

    private int SECONDSINHOURS = 3600;

    private int SECONDSINMINUTES = 60;

    public TableView<OrganWaitlist.OrganRequest> potentialMatchesTable;

    public TableColumn<OrganWaitlist.OrganRequest, String> nhiCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> nameCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> ageCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> regionCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> addressCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> waitingTimeCol;

    public TableColumn<OrganWaitlist.OrganRequest, String> travelTimeCol;

    public Text nameLabel;

    public Text nhiLabel;

    public Text organLabel;

    public Text bloodTypeLabel;

    public Text regionLabel;

    public Text deathLocationLabel;

    public Text ageLabel;

    public Text expiryTimeLabel;

    public GridPane potentialMatchesPane;

    public Button closeButton;

    @FXML
    private GridPane filterGrid;

    @FXML
    private ComboBox<String> regionFilter;

    @FXML
    private ComboBox<String> birthGenderFilter;

    @FXML
    private Text ageSliderLabel;

    private Organ targetOrgan;

    private PatientOrgan targetPatientOrgan;

    private ObservableList<OrganWaitlist.OrganRequest> allRequests = FXCollections.observableArrayList();

    private Map<Region, List<Region>> adjacentRegions = new HashMap<>();

    private PatientDataService patientDataService = new PatientDataService();

    private RangeSlider rangeSlider;

    private List<OrganWaitlist.OrganRequest> requests;

    private ObservableList<OrganWaitlist.OrganRequest> observableList;

    private Map<FilterOption, String> filter = new HashMap<>();

    private TouchPaneController matchTouchPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private SortedList<OrganWaitlist.OrganRequest> sortedRequests;

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    /**
     *  0         1
     *   X ---- X
     *   .      .
     *   .      .
     *   X ---- X
     *  2         3
     *
     * Map of boundaries on NZ. Keys are corner indexes (refer to top image)
     */
    private HashMap<Integer, LatLng> boundsOfNz = new HashMap<>();

    /**
     * Sets the target donor and organ for this controller and loads the data accordingly
     * @param patientOrgan the selected patient organ object from the available organs
     */
    public void setTarget(PatientOrgan patientOrgan) {
        target = patientOrgan.getPatient();
        targetOrgan = patientOrgan.getOrgan();
        targetPatientOrgan = new PatientOrgan((Patient) target, targetOrgan);
        targetPatientOrgan.startTask();
        targetPatientOrgan.getProgressTask().setProgressBar(new ProgressBar()); //dummy progress task
        CachedThreadPool.getCachedThreadPool().getThreadService().submit(targetPatientOrgan.getProgressTask());
        load();
    }


    /**
     * Initializes matches list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void load() {
        boundsOfNz.put(0, new LatLng(-34.386058, 166.848052));
        boundsOfNz.put(2, new LatLng(-47.058964, 164.456064));
        boundsOfNz.put(1, new LatLng(-34.073159, 179.323815));
        boundsOfNz.put(3, new LatLng(-46.494633, 179.932955));
        allRequests.clear();
        loadRegionDistances();
        ClinicianDataService clinicianDataService = new ClinicianDataService();
        OrganWaitlist organRequests = clinicianDataService.getOrganWaitList();
        requests = new ArrayList<>();
        for (OrganWaitlist.OrganRequest request : organRequests) {
            if (checkMatch(request)) {
                allRequests.add(request);
            }
        }
        setLabels();
        populateTable();
        setupDoubleClickToPatientEdit();
        setupAgeSliderListeners();
        setupFilterListeners();
        filterTravelTimes();
        if (screenControl.isTouch()) {
            matchTouchPane = new TouchPaneController(potentialMatchesPane);
            potentialMatchesPane.setOnZoom(this::zoomWindow);
            potentialMatchesPane.setOnRotate(this::rotateWindow);
            potentialMatchesPane.setOnScroll(this::scrollWindow);
            potentialMatchesPane.setOnTouchPressed(event -> potentialMatchesPane.toFront());
        }

    }


    /**
     * Adds listener to the age label to update when slider is moved
     */
    private void setupAgeSliderListeners() {
        rangeSlider = new RangeSlider();
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setPadding(new Insets(10, 150, 0, 50));
        rangeSlider.setMaxWidth(10000);
        if (((Patient) target).getAge() < 12) {
            rangeSlider.setMin(0);
            rangeSlider.setMax(12);
            rangeSlider.setLowValue(0);
            rangeSlider.setHighValue(12);
        }
        else if (((Patient) target).getAge() > 27) {
            rangeSlider.setMin(((Patient) target).getAge() - 15);
            rangeSlider.setMax(((Patient) target).getAge() + 15);
            rangeSlider.setLowValue(((Patient) target).getAge() - 15);
            rangeSlider.setHighValue(((Patient) target).getAge() + 15);
        }
        else {
            rangeSlider.setMin(12);
            rangeSlider.setMax(((Patient) target).getAge() + 15);
            rangeSlider.setLowValue(12);
            rangeSlider.setHighValue(((Patient) target).getAge() + 15);
        }
        rangeSlider.setShowTickMarks(true);
        filterGrid.add(rangeSlider, 1, 3, 3, 1);
        ageSliderLabel.setText(String.format("%s - %s", ((int) rangeSlider.getLowValue()), (int) rangeSlider.getHighValue()));
        rangeSlider.highValueProperty()
                .addListener(((observable, oldValue, newValue) -> ageSliderLabel.setText(String.format("%s - %s",
                        ((int) rangeSlider.getLowValue()),
                        String.valueOf(newValue.intValue())))));
        rangeSlider.lowValueProperty()
                .addListener(((observable, oldValue, newValue) -> ageSliderLabel.setText(String.format("%s - %s",
                        String.valueOf(newValue.intValue()),
                        (int) rangeSlider.getHighValue()))));
    }


    @FXML
    public void onSort(Event event) {
        // bind the SortedList comparator to the TableView comparator.
        Comparator<OrganWaitlist.OrganRequest> defaultComparator = (request1, request2) -> {
            if (request1.getDate().isBefore(request2.getDate())) {
                return -1;
            } else if (request2.getDate().isBefore(request1.getDate())) {
                return 1;
            } else {
                return (getRegionDistance(request1.getRequestRegion(), new ArrayList<>())).compareTo((getRegionDistance(request2.getRequestRegion(), new ArrayList<>())));
            }
        };
        ObjectProperty<Comparator<? super OrganWaitlist.OrganRequest>> defaultObjectProperty = new SimpleObjectProperty<>(defaultComparator);

        Comparator<OrganWaitlist.OrganRequest> waitTimeComparator = Comparator.comparing(OrganWaitlist.OrganRequest::getDate);
        ObjectProperty<Comparator<? super OrganWaitlist.OrganRequest>> waitTimeObjectPropertyAsc = new SimpleObjectProperty<>(waitTimeComparator);
        ObjectProperty<Comparator<? super OrganWaitlist.OrganRequest>> waitTimeObjectPropertyDesc = new SimpleObjectProperty<>(waitTimeComparator.reversed());

        sortedRequests.comparatorProperty().unbind();
        if (potentialMatchesTable.getSortOrder().size() == 0) {
            sortedRequests.comparatorProperty().bind(defaultObjectProperty);
            potentialMatchesTable.setSortPolicy(param -> true);
        } else {
            boolean sortingByWaitingTime = false;
            boolean isAscending = true;
            ObservableList<TableColumn<OrganWaitlist.OrganRequest, ?>> sortPolicies = potentialMatchesTable.getSortOrder();
            //Search the sort policies to see if any of the tablecolumns being sorted is the waiting time column
            for (TableColumn<OrganWaitlist.OrganRequest, ?> tableColumn : sortPolicies) {
                if (tableColumn.getId().equals("waitingTimeCol")) {
                    sortingByWaitingTime = true;
                    //Get the sort order of the table column
                    isAscending = tableColumn.getSortType() == TableColumn.SortType.ASCENDING;
                }
            }
            if (sortingByWaitingTime) {
                //Apply correct comparator
                if (isAscending) {
                    sortedRequests.comparatorProperty().bind(waitTimeObjectPropertyAsc);
                } else {
                    sortedRequests.comparatorProperty().bind(waitTimeObjectPropertyDesc);
                }
            } else { //Apply default table comparator
                sortedRequests.comparatorProperty().bind(potentialMatchesTable.comparatorProperty());
            }
            potentialMatchesTable.setSortPolicy(param -> true);
        }
    }

    /**
     * Sets the labels displayed to the requirements of the donated organ
     */
    private void setLabels() {
        nameLabel.setText(target.getNameConcatenated());
        nhiLabel.setText(((Patient) target).getNhiNumber());
        organLabel.setText(targetOrgan.toString());
        bloodTypeLabel.setText(((Patient) target).getBloodGroup()
                .toString());
        ageLabel.setText(String.valueOf(((Patient) target).getAge()));
        regionLabel.setText(((Patient) target).getDeathRegion()
                .toString());
        deathLocationLabel.setText(((Patient) target).getDeathStreet() + ", " + ((Patient) target).getDeathCity());
        expiryTimeLabel.textProperty().bind(targetPatientOrgan.getProgressTask().messageProperty());
    }


    /**
     * Checks that the provided request matches the organ being donated
     *
     * @param request the potential match
     * @return whether the match is valid or not
     */
    private boolean checkMatch(OrganWaitlist.OrganRequest request) {
        boolean match = true;
        Patient potentialReceiver = patientDataService.getPatientByNhi(request.getReceiverNhi());
        //Do not match against patients that have no address or region
        if (potentialReceiver.getRegion() == null || (potentialReceiver.getStreetNumber() == null && potentialReceiver.getStreetName() == null)) {
            return false;
        }
        long requestAge = ChronoUnit.DAYS.between(request.getBirth(), LocalDate.now());
        long targetAge = ChronoUnit.DAYS.between(((Patient) target).getBirth(), ((Patient) target).getDeathDate());
        if (request.getRequestedOrgan() != targetOrgan || request.getBloodGroup() != ((Patient) target).getBloodGroup() || request.getReceiver().getDeathDate() != null) {
            match = false;
        }
        else if ((requestAge < 4383 && targetAge > 4383) || (requestAge > 4383 && targetAge < 4383) || abs(requestAge - targetAge) > 5478.75) {
            match = false;
        }
        return match;
    }


    /**
     * Calculates the travel time from the current location of the donating patient to a
     *
     * @param potentialMatch the patient potentially receiving the organ
     * @return the travel time
     */
    private long calculateTotalHeloTravelTime(Patient potentialMatch) {

        //constants using kilometers and seconds
        long organLoadTime = 1800;
        long organUnloadtime = 1800;
        long refuelTime = 1800;
        double maxTravelDistanceStatuteKilometers = 460; //on one tank of gas
        long heloTravelSpeedKmh = 260;
        double metersPerKm = 1000 / (double)3600;

        LatLng donorLocation;
        LatLng receiverLocation;

        // calculate distance between donating organ and receiving patient
        try {
            // calculate total travel time
            donorLocation = APIGoogleMaps.getApiGoogleMaps().geocodeAddress(((Patient) target).getDeathLocationConcat());
            receiverLocation = potentialMatch.getCurrentLocation();
        } catch (Exception e) {
            systemLogger.log(Level.WARNING, "Unable to calculate distance to potential receiver");
            return -1;
        }

        boolean receiverInNz = isInNz(receiverLocation);
        boolean donorInNz = isInNz(donorLocation);
        if (receiverInNz && donorInNz) {
            double distance = calculateDistanceInKilometer(donorLocation.lat, donorLocation.lng, receiverLocation.lat, receiverLocation.lng);
            long totalTravelTime = (long) Math.ceil((distance * 1000 )/ (heloTravelSpeedKmh * metersPerKm)); //time to travel in seconds

            // calculate total refuel time
            int numRefuels = (int) Math.ceil(distance / maxTravelDistanceStatuteKilometers);
            double totalRefuelTime = refuelTime * numRefuels;

            return (long) Math.ceil(organLoadTime + totalTravelTime + totalRefuelTime + organUnloadtime);
        }
        return -1;
    }


    /**
     * Checks given latlng is wirhin nz
     * @param latLng - latng to check if it is within NZ
     * @return - true if latlng is in nz
     */
    private boolean isInNz(LatLng latLng) {
        return latLng.lat > boundsOfNz.get(2).lat && latLng.lat < boundsOfNz.get(1).lat
                && latLng.lng < boundsOfNz.get(1).lng && latLng.lng > boundsOfNz.get(2).lng;

        //return true; //todo - organ wait list not updated correctly when changing address on patient profile...
    }

    /**
     * Calculates the distance between two points
     * @param userLat the first lat
     * @param userLng the first lng
     * @param venueLat the second lat
     * @param venueLng the second lng
     * @return the distance between the two in kilometers
     */
    private double calculateDistanceInKilometer(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (double) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }


    private String getTravelTimeToTravel(OrganWaitlist.OrganRequest request){
        long totalSecs = calculateTotalHeloTravelTime(request.getReceiver());
        if (totalSecs == -1) {
            return "No location";
        }
        int hours = (int)(totalSecs / 3600L);
        int minutes = (int)((totalSecs % 3600L) / 60L);
        int seconds = (int) (totalSecs % 60L);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Populates the potential matches table with the potential matches in the right order
     */
    private void populateTable() {
        nhiCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getReceiverNhi()));
        nameCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getReceiverName()));
        ageCol.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(r.getValue()
                .getAge())));
        regionCol.setCellValueFactory(r -> {
            if (r.getValue()
                    .getRequestRegion() != null) {
                return new SimpleStringProperty(r.getValue()
                        .getRequestRegion()
                        .toString());
            }
            return null;
        });
        addressCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getAddress()));
        waitingTimeCol.setCellValueFactory(r -> new SimpleStringProperty(String.valueOf(DAYS.between(r.getValue()
                .getDate(), LocalDate.now()))));
        travelTimeCol.setCellValueFactory(r -> new SimpleStringProperty(getTravelTimeToTravel(r.getValue())));


        // wrap ObservableList in a FilteredList
        //FilteredList<OrganWaitlist.OrganRequest> filteredRequests = filterRequests();

        observableList = FXCollections.observableList(requests);
        FilteredList<OrganWaitlist.OrganRequest> filteredRequests = new FilteredList<>(observableList);

        filterRequests();

        // wrap the FilteredList in a SortedList.
        sortedRequests = new SortedList<>(filteredRequests);

        // bind the SortedList comparator to the TableView comparator.
        Comparator<OrganWaitlist.OrganRequest> newComparator = (request1, request2) -> {
            if (request1.getDate().isBefore(request2.getDate())) {
                return -1;
            }
            else if (request2.getDate()
                    .isBefore(request1.getDate())) {
                return 1;
            }
            else {
                return (getRegionDistance(request1.getRequestRegion(), new ArrayList<>())).compareTo((getRegionDistance(request2.getRequestRegion(),
                        new ArrayList<>())));
            }
        };
        ObjectProperty<Comparator<? super OrganWaitlist.OrganRequest>> objectProperty = new SimpleObjectProperty<>(newComparator);

        sortedRequests.comparatorProperty()
                .bind(objectProperty);
        // add sorted (and filtered) data to the table.
        potentialMatchesTable.setItems(sortedRequests);

        regionFilter.getItems()
                .add(GlobalEnums.NONE_ID); //for empty selection
        regionFilter.getSelectionModel()
                .select(0);
        for (Region region : Region.values()) { //add values to region choice box
            regionFilter.getItems()
                    .add(StringUtils.capitalize(region.getValue()));
        }
        birthGenderFilter.getItems()
                .add(GlobalEnums.NONE_ID);
        birthGenderFilter.getSelectionModel()
                .select(0);
        for (BirthGender birthGender : BirthGender.values()) {
            birthGenderFilter.getItems()
                    .addAll(StringUtils.capitalize(birthGender.getValue()));
        }
    }


    /**
     * Filters the requests based on dropdown filters and age slider
     */
    private void filterRequests() {
        PatientDataService patientDataService = new PatientDataService();
        PatientLocalDAO localDAO = new PatientLocalDAO();
        List<OrganWaitlist.OrganRequest> organRequests = new ArrayList<>();
        for (OrganWaitlist.OrganRequest organRequest : allRequests) {
            if (localDAO.matchesFilter(patientDataService.getPatientByNhi(organRequest.getReceiverNhi()), filter)) {
                organRequests.add(organRequest);
            }
        }
        observableList.setAll(organRequests);
        potentialMatchesTable.refresh();
    }

    /**
     * Removes any potential matches whose travel time exceeds the expiry time left on the organ
     */
    private void filterTravelTimes() {
        List<OrganWaitlist.OrganRequest> toFar = new ArrayList<>();
        for (OrganWaitlist.OrganRequest organRequest : potentialMatchesTable.getItems()) {
            if (secondsInTime(getTravelTimeToTravel(organRequest)) > (secondsInTime(targetPatientOrgan.getProgressTask().getMessage()))) {
                 toFar.add(organRequest);
             }
        }
        observableList.removeAll(toFar);
    }

    /**
     * Gives the seconds in the time provided of the for hh:mm:ss
     * @param time the string value of the time in hh:mm:ss
     * @return the amount of seconds in that time
     */
    private int secondsInTime(String time) {
        String[] times = time.split(":");
        int total = 0;
        total += Integer.parseInt(times[0]) * SECONDSINHOURS;
        total += Integer.parseInt(times[1]) * SECONDSINMINUTES;
        total += Integer.parseInt(times[2]);
        return total;
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update, ensures no duplicate profiles
     */
    private void setupDoubleClickToPatientEdit() {
        ScreenControl screenControl = ScreenControl.getScreenControl();
        // Add double-click event to rows
        potentialMatchesTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && potentialMatchesTable.getSelectionModel()
                    .getSelectedItem() != null) {
                OrganWaitlist.OrganRequest request = potentialMatchesTable.getSelectionModel()
                        .getSelectedItem();
                try {
                    Patient selectedUser = patientDataService.getPatientByNhi(request.getReceiverNhi());
                    patientDataService.save(selectedUser);
                    GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selectedUser);
                    controller.setTarget(selectedUser);
                }
                catch (Exception e) {
                    userActions.log(Level.SEVERE,
                            "Failed to retrieve selected patient from database",
                            new String[] { "Attempted to retrieve selected patient from database", request.getReceiverNhi() });
                }
            }
        });
    }


    /**
     * Called when a profile opened from this window is closed
     */
    public void windowClosed() {
        load();
        potentialMatchesTable.refresh();
    }


    /**
     * Gets the lowest amount of adjacent regions traversed between provided and target region
     *
     * @param region the region to search from
     * @return how many steps away the region is from the target region
     */
    private Integer getRegionDistance(Region region, List<Region> visitedRegions) {
        // Found region
        if (region == ((Patient) target).getDeathRegion()) {
            return 0;
        }
        else if (region == null) {
            return 100;
        }
        else {
            int minDistance = -1;
            // Keep looking in adjacent regions
            for (Region adjacentRegion : adjacentRegions.get(region)) {
                if (!visitedRegions.contains(adjacentRegion)) {
                    visitedRegions.add(adjacentRegion);
                    int distance = getRegionDistance(adjacentRegion, new ArrayList<>(visitedRegions));
                    if (minDistance == -1 || distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
            // No new regions to visit
            if (minDistance == -1) {
                return 100;
            } else {
                return minDistance + 1;
            }
        }
    }


    /**
     * Loads into the region distances map regions adjacent to key region
     */
    private void loadRegionDistances() {
        adjacentRegions.put(Region.NORTHLAND, new ArrayList<Region>() {{
            add(Region.AUCKLAND);
        }});
        adjacentRegions.put(Region.AUCKLAND, new ArrayList<Region>() {{
            add(Region.NORTHLAND);
            add(Region.WAIKATO);
        }});
        adjacentRegions.put(Region.WAIKATO, new ArrayList<Region>() {{
            add(Region.AUCKLAND);
            add(Region.BAYOFPLENTY);
            add(Region.TARANAKI);
            add(Region.MANAWATU);
            add(Region.HAWKESBAY);
        }});
        adjacentRegions.put(Region.BAYOFPLENTY, new ArrayList<Region>() {{
            add(Region.WAIKATO);
            add(Region.GISBORNE);
            add(Region.HAWKESBAY);
        }});
        adjacentRegions.put(Region.GISBORNE, new ArrayList<Region>() {{
            add(Region.BAYOFPLENTY);
            add(Region.HAWKESBAY);
        }});
        adjacentRegions.put(Region.TARANAKI, new ArrayList<Region>() {{
            add(Region.WAIKATO);
            add(Region.MANAWATU);
        }});
        adjacentRegions.put(Region.MANAWATU, new ArrayList<Region>() {{
            add(Region.WAIKATO);
            add(Region.TARANAKI);
            add(Region.HAWKESBAY);
            add(Region.WELLINGTON);
        }});
        adjacentRegions.put(Region.HAWKESBAY, new ArrayList<Region>() {{
            add(Region.WAIKATO);
            add(Region.BAYOFPLENTY);
            add(Region.GISBORNE);
            add(Region.MANAWATU);
        }});
        adjacentRegions.put(Region.WELLINGTON, new ArrayList<Region>() {{
            add(Region.MANAWATU);
            add(Region.MARLBOROUGH);
        }});
        adjacentRegions.put(Region.TASMAN, new ArrayList<Region>() {{
            add(Region.NELSON);
            add(Region.MARLBOROUGH);
            add(Region.WESTCOAST);
        }});
        adjacentRegions.put(Region.NELSON, new ArrayList<Region>() {{
            add(Region.TASMAN);
            add(Region.MARLBOROUGH);
        }});
        adjacentRegions.put(Region.MARLBOROUGH, new ArrayList<Region>() {{
            add(Region.WELLINGTON);
            add(Region.NELSON);
            add(Region.TASMAN);
            add(Region.CANTERBURY);
        }});
        adjacentRegions.put(Region.WESTCOAST, new ArrayList<Region>() {{
            add(Region.TASMAN);
            add(Region.CANTERBURY);
            add(Region.OTAGO);
            add(Region.SOUTHLAND);
        }});
        adjacentRegions.put(Region.CANTERBURY, new ArrayList<Region>() {{
            add(Region.MARLBOROUGH);
            add(Region.TASMAN);
            add(Region.WESTCOAST);
            add(Region.OTAGO);
        }});
        adjacentRegions.put(Region.OTAGO, new ArrayList<Region>() {{
            add(Region.CANTERBURY);
            add(Region.WESTCOAST);
            add(Region.SOUTHLAND);
        }});
        adjacentRegions.put(Region.SOUTHLAND, new ArrayList<Region>() {{
            add(Region.OTAGO);
            add(Region.WESTCOAST);
        }});
    }


    /**
     * Sets the filter listeners for the potential matches list
     */
    private void setupFilterListeners() {
        expiryTimeLabel.textProperty().addListener(e -> {
            filterTravelTimes();
        });
        regionFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.put(FilterOption.REGION, newValue);
                    filterRequests();
                }));

        birthGenderFilter.valueProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    filter.put((FilterOption.BIRTHGENDER), newValue);
                    filterRequests();
                }));

        rangeSlider.onMouseReleasedProperty()
                .addListener((observable, oldvalue, newvalue) -> {
                    filter.put(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
                    filter.put(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
                    filterRequests();

                });

        rangeSlider.setOnMouseReleased(event -> {
            filter.put(FilterOption.AGEUPPER, String.valueOf(rangeSlider.getHighValue()));
            filter.put(FilterOption.AGELOWER, String.valueOf(rangeSlider.getLowValue()));
            filterRequests();
        });
    }


    @Override
    public void zoomWindow(ZoomEvent zoomEvent) {
        matchTouchPane.zoomPane(zoomEvent);
    }


    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        matchTouchPane.rotatePane(rotateEvent);
    }


    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        matchTouchPane.scrollPane(scrollEvent);
    }


    @FXML
    public void closeMatchWindow() {
        screenControl.closeWindow(potentialMatchesPane);
    }

    private UserControl userControl = UserControl.getUserControl();

    /**
     * View patients from table on the map
     * Sets the patients list in the JavaScript to custom set
     * Opens the map and loads
     */
    @FXML
    public void viewOnMap() {
        List<Patient> patients = new ArrayList<>();
        for (OrganWaitlist.OrganRequest anObservableList : observableList) {
            patients.add(patientDataService.getPatientByNhi(anObservableList.getReceiverNhi()));
        }
        patients.add((Patient) target);

        if (screenControl.getMapOpen()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to repopulate the map?", ButtonType.OK, ButtonType.NO);
            alert.show();
            alert.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> populateMap(patients));
        } else {
            screenControl.show("/scene/map.fxml", true, this, userControl.getLoggedInUser());
            populateMap(patients);
        }
    }

    /**
     * Populates the map with the provided collection of patients
     * @param patients the patients to populate the map with
     */
    private void populateMap(Collection<Patient> patients) {
        screenControl.setIsCustomSetMap(true);
        screenControl.getMapController().setPatients(patients);
        screenControl.setMapOpen(true);
    }
}
