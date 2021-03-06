package model;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Validate;
import org.apache.commons.lang3.StringUtils;
import service.APIGoogleMaps;
import utility.*;
import utility.GlobalEnums.*;
import utility.parsing.DateConverterCSV;
import utility.parsing.DateTimeConverterCSV;
import utility.parsing.EnumConverterCSV;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

public class Patient extends User {

    private Timestamp CREATED;

    private String preferredName;

    @Parsed(field = "date_of_birth")
    @Convert(conversionClass = DateConverterCSV.class)
    private LocalDate birth;

    @Parsed(field = "date_of_death")
    @Convert(conversionClass = DateTimeConverterCSV.class)
    private LocalDateTime death;

    private String deathStreet;

    private String deathCity;

    private Region deathRegion;

    @Parsed(field = "birth_gender")
    @Validate(oneOf = {"Male", "male", "m", "Female", "female", "f"})
    @EnumOptions(customElement = "value")
    private BirthGender birthGender;

    @Parsed(field = "gender")
    @Validate(oneOf = {"Male", "male", "m", "Female", "female", "f"})
    @Convert(conversionClass = EnumConverterCSV.class)
    private PreferredGender preferredGender;

    @Parsed(field = "height")
    private double height; // Height in meters

    @Parsed(field = "weight")
    private double weight; // Weight in kilograms

    @Parsed(field = "blood_type")
    @Validate(oneOf = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
    @EnumOptions(customElement = "value")
    private BloodGroup bloodGroup;

    @Parsed(field = "street_number")
    private String streetNumber;

    @Parsed(field = "street_name")
    private String streetName;

    @Parsed(field = "neighborhood")
    private String suburb;

    @Parsed(field = "city")
    private String city;

    @Parsed(field = "region")
    @Convert(conversionClass = EnumConverterCSV.class)
    private Region region;

    @Parsed(field = "zip_code")
    private int zip;

    private LatLng currentLocation;

    private Map<Organ, String> donations;

    private Map<Organ, OrganReceival> requiredOrgans;

    @Parsed(field = "nhi")
    private String nhiNumber;

    private List<Medication> currentMedications = new ArrayList<>();

    private List<Medication> medicationHistory = new ArrayList<>();

    private List<Procedure> procedures = new ArrayList<>();

    @Parsed(field = "home_number")
    private String homePhone;

    @Parsed(field = "mobile_number")
    private String mobilePhone;

    @Parsed(field = "email")
    private String emailAddress;

    private String workPhone;

    private String contactName;

    private String contactRelationship;

    private String contactHomePhone;

    private String contactMobilePhone;

    private String contactWorkPhone;

    private String contactEmailAddress;

    private Status status; // Whether patient is receiving/donating/both/neither

    private List<PatientActionRecord> userActionsList;

    private List<Disease> currentDiseases = new ArrayList<>();

    private List<Disease> pastDiseases = new ArrayList<>();

    private GlobalEnums.Organ removedOrgan;

    private transient Logger systemLogger = SystemLogger.systemLogger;

    /**
     * Used only for importing. Don't use elsewhere.
     */
    public Patient() {
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.requiredOrgans = new HashMap<>();
        this.donations = new HashMap<>();
    }


    /**
     * Constructor for the patient class. Initializes basic attributes and adds listeners for status changes
     *
     * @param nhiNumber   unique number to identify the patient by
     * @param firstName   first name of the patient
     * @param middleNames middle names of the patient
     * @param lastName    last name of the patient
     * @param date        date of birth of patient
     */
    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate date) {
        super(firstName, middleNames, lastName);
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.preferredName = firstName;
        this.birth = date;
        this.nhiNumber = nhiNumber.toUpperCase();
        this.donations = new HashMap<>();
        this.userActionsList = new ArrayList<>();
        this.requiredOrgans = new HashMap<>();
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(evt -> refreshStatus());
    }

    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate birth,
                   Timestamp created, Timestamp modified, LocalDateTime death, String deathStreet, String deathCity,Region deathRegion, GlobalEnums.BirthGender gender,
                   GlobalEnums.PreferredGender prefGender, String preferredName, double height, double weight,
                   BloodGroup bloodType, HashMap<Organ, String> donations, Map<Organ, OrganReceival> receiving, String streetNumber,
                   String city, String suburb, Region region, int zip, String homePhone, String workPhone,
                   String mobilePhone, String emailAddress, String contactName, String contactRelationship,
                   String contactHomePhone, String contactWorkPhone, String contactMobilePhone, String contactEmailAddress,
                   List<PatientActionRecord> userActionsList, List<Disease> currentDiseases,
                   List<Disease> pastDiseases, List<Medication> currentMedications,
                   List<Medication> medicationHistory, List<Procedure> procedures) {
        super(firstName, middleNames, lastName);
        this.nhiNumber = nhiNumber;
        this.birth = birth;
        this.CREATED = created;
        this.modified = modified;
        this.death = death;
        this.deathStreet = deathStreet;
        this.deathCity = deathCity;
        this.deathRegion = deathRegion;
        this.birthGender = gender;
        this.preferredGender = prefGender;
        this.preferredName = preferredName;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodType;
        this.donations = donations;
        this.requiredOrgans = receiving;
        this.streetNumber = streetNumber;
        this.streetName = city;
        this.suburb = suburb;
        this.region = region;
        this.zip = zip;
        this.homePhone = homePhone;
        this.workPhone = workPhone;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
        this.contactName = contactName;
        this.contactRelationship = contactRelationship;
        this.contactHomePhone = contactHomePhone;
        this.contactWorkPhone = contactWorkPhone;
        this.contactMobilePhone = contactMobilePhone;
        this.contactEmailAddress = contactEmailAddress;
        this.userActionsList = userActionsList;
        this.currentDiseases = currentDiseases;
        this.pastDiseases = pastDiseases;
        this.currentMedications = currentMedications;
        this.medicationHistory = medicationHistory;
        this.procedures = procedures;
        if (this.CREATED == null) {
            this.CREATED = new Timestamp(System.currentTimeMillis());
            if (this.modified == null) {
            	this.modified = CREATED;
            }
        }
        databaseImport();
    }

    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate birth,
                   Timestamp created, Timestamp modified, LocalDateTime death, GlobalEnums.BirthGender gender,
                   GlobalEnums.PreferredGender prefGender, String preferredName) {
        super(firstName, middleNames, lastName);
        this.nhiNumber = nhiNumber;
        this.birth = birth;
        this.CREATED = created;
        this.modified = modified;
        this.death = death;
        this.birthGender = gender;
        this.preferredGender = prefGender;
        this.preferredName = preferredName;
        this.userActionsList = new ArrayList<>();
        this.requiredOrgans = new HashMap<>();
        this.donations = new HashMap<>();
        databaseImport();
    }

    /**
     * Sets the attributes of the patient
     *
     * @param firstName       first name
     * @param lastName        last name
     * @param middleNames     middle names
     * @param preferredName   preferred name
     * @param birth           birth date
     * @param death           death date
     * @param streetName      street name of current address
     * @param streetNumber    street number of current address
     * @param city         city of address
     * @param suburb          suburb of address
     * @param region          region of address
     * @param birthGender     gender of patient at birth
     * @param preferredGender chosen gender of patient
     * @param bloodGroup      blood group
     * @param height          height in meters
     * @param weight          weight in kilograms
     * @param nhi             NHI
     * @throws DataFormatException If data poorly formatted
     */
    public void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames, String preferredName,
                                 LocalDate birth, LocalDateTime death, String streetName, String streetNumber, String city, String suburb,
                                 String region, String birthGender, String preferredGender, String bloodGroup,
                                 double height, double weight, String nhi) throws IllegalArgumentException, DataFormatException {
        Enum globalEnum;
        Searcher.getSearcher().removeIndex(this);
        if (firstName != null) {
            setFirstName(firstName);
        }
        if (lastName != null) {
            setLastName(lastName);
        }
        if (middleNames != null) {
            setMiddleNames(middleNames);
        }
        if (preferredName != null) {
            setPreferredName(preferredName);
        }
        if (birth != null) {
            setBirth(birth);
        }
        if (death != null) {
            setDeathDate(death);
        }
        if (streetName != null) {
            setStreetName(streetName);
        }
        if (streetNumber != null) {
            setStreetNumber(streetNumber);
        }
        if (city != null) {
            setCity(city);
        }
        if (suburb != null) {
            setSuburb(suburb);
        }
        if (region != null) {
            globalEnum = Region.getEnumFromString(region);
            if (globalEnum != null) {
                setRegion((Region) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid region", new String[]{"attempted to update patient attributes", getNhiNumber()});
            }
        }
        if (birthGender != null) {
            globalEnum = BirthGender.getEnumFromString(birthGender);
            if (globalEnum != null) {
                setBirthGender((BirthGender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid birth gender", new String[]{"attempted to update patient attributes", getNhiNumber()});
            }
        }
        if (preferredGender != null) {
            globalEnum = PreferredGender.getEnumFromString(preferredGender);
            if (globalEnum != null) {
                setPreferredGender((PreferredGender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid preferred gender", new String[]{"attempted to update patient attributes", getNhiNumber()});
            }
        }
        if (bloodGroup != null) {
            globalEnum = BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) {
                setBloodGroup((BloodGroup) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid blood group", new String[]{"attempted to update patient attributes", getNhiNumber()});
            }
        }
        if (height > 0) {
            setHeight(height);
        }
        if (weight > 0) {
            setWeight(weight);
        }
        if (nhi != null) {
            setNhiNumber(nhi);
        }
        userActions.log(Level.INFO, "Successfully updated patient " + getNhiNumber(), new String[]{"attempted to update patient attributes", getNhiNumber()});
        userModified();
        Searcher.getSearcher().addIndex(this);
    }


    /**
     * Sets the attributes of the patient to the attributes of the provided patient
     *
     * @param newUserAttributes a user whose attributes this function copies
     */
    public void setAttributes(User newUserAttributes) {

        Patient newPatientAttributes = (Patient) newUserAttributes.deepClone();

        setFirstName(newPatientAttributes.getFirstName());
        setLastName(newPatientAttributes.getLastName());
        setMiddleNames(newPatientAttributes.getMiddleNames());
        setPreferredName(newPatientAttributes.getPreferredName());
        setBirth(newPatientAttributes.getBirth());
        setDeathDate(newPatientAttributes.getDeathDate());
        setDeathStreet(newPatientAttributes.getDeathStreet());
        setDeathCity(newPatientAttributes.getDeathCity());
        setDeathRegion(newPatientAttributes.getDeathRegion());
        setCity(newPatientAttributes.getCity());
        try {
            setSuburb(newPatientAttributes.getSuburb());
        } catch (DataFormatException e) {
            userActions.log(Level.SEVERE, "","" );
        }
        setRegion(newPatientAttributes.getRegion());
        setStreetName(newPatientAttributes.getStreetName());
        setStreetNumber(newPatientAttributes.getStreetNumber());
        setBirthGender(newPatientAttributes.getBirthGender());
        setPreferredGender(newPatientAttributes.getPreferredGender());
        setBloodGroup(newPatientAttributes.getBloodGroup());
        setHeight(newPatientAttributes.getHeight());
        setWeight(newPatientAttributes.getWeight());
        setNhiNumber(newPatientAttributes.getNhiNumber());
        setCurrentDiseases(newPatientAttributes.getCurrentDiseases());
        setPastDiseases(newPatientAttributes.getPastDiseases());
        setProcedures(newPatientAttributes.getProcedures());
        setStatus(newPatientAttributes.getStatus());
        setContactEmailAddress(newPatientAttributes.getContactEmailAddress());
        setContactHomePhone(newPatientAttributes.getContactHomePhone());
        setContactMobilePhone(newPatientAttributes.getContactMobilePhone());
        setContactName(newPatientAttributes.getContactName());
        setContactRelationship(newPatientAttributes.getContactRelationship());
        setContactWorkPhone(newPatientAttributes.getContactWorkPhone());
        setCurrentMedications(newPatientAttributes.getCurrentMedications());
        setDonations(newPatientAttributes.getDonations());
        setMedicationHistory(newPatientAttributes.getMedicationHistory());
        setRemovedOrgan(newPatientAttributes.getRemovedOrgan());
        setEmailAddress(newPatientAttributes.getEmailAddress());
        setHomePhone(newPatientAttributes.getHomePhone());
        setMobilePhone(newPatientAttributes.getMobilePhone());
        setRequiredOrgans(newPatientAttributes.getRequiredOrgans());
        setWorkPhone(newPatientAttributes.getWorkPhone());
        setZip(newPatientAttributes.getZip());
        MapBridge mp = new MapBridge(); //coupling to map bridge
        mp.updateInfoWindow(newPatientAttributes);
    }

    /**
     * Update the organ donations list of the patient
     *
     * @param newDonations - map of organs to add
     * @param rmDonations  - map of organs to remove
     */
    public void updateDonations(Map<Organ, String> newDonations, Map<Organ, String> rmDonations) {
        if (newDonations != null) {
            for (Organ organ : newDonations.keySet()) {
                userActions.log(INFO, addDonation(organ), "attempted to update patient donations");
                userModified();
            }
        }
        if (rmDonations != null) {
            for (Organ organ : rmDonations.keySet()) {
                userActions.log(INFO, removeDonation(organ), "attempted to remove from patient donations");
                userModified();
            }
        }
    }

    /**
     * Checks that the nhi number consists (only) of 3 letters then 4 numbers
     *
     * @throws IllegalArgumentException when the nhi number given is not in the valid format
     */
    private void ensureValidNhi(String nhi) throws IllegalArgumentException {
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}", nhi.toUpperCase())) {
            throw new IllegalArgumentException(
                    "NHI number " + nhiNumber.toUpperCase() + " is not in the correct format (3 letters followed by 4 numbers)");
        }
    }


    /**
     * Returns the name of the patient as a formatted concatenated string
     *
     * @return string named
     */
    @Override
    public String getNameConcatenated() {
        StringBuilder concatName;

        if (preferredName != null) {
            concatName = new StringBuilder(StringUtils.capitalize(preferredName) + " ");
        } else {
            concatName = new StringBuilder(StringUtils.capitalize(firstName) + " ");
        }
        if (middleNames != null && middleNames.size() > 0) {
            for (String middleName : middleNames) {
                concatName.append(StringUtils.capitalize(middleName))
                        .append(" ");
            }
        }
        concatName.append(StringUtils.capitalize(lastName));
        return concatName.toString();
    }

    public Map<Organ, String> getDonations() {
        return donations == null ? new HashMap<>() : donations;
    }

    /**
     * Sets the donation organs of the patient to the list parsed through
     *
     * @param donations The donations being set to the patient donations array list
     */
    public void setDonations(Map<Organ, String> donations) {
        this.donations = donations;
        userModified();
    }

    @Override
    public void setFirstName(String firstName) {
        //added for when csv parsing creates obj from default constructor
        if (this.firstName == null) {
            this.firstName = firstName;
        }
        if ((!firstName.equals(this.firstName))) {
            Searcher.getSearcher().removeIndex(this);
            this.firstName = firstName;
            if (getPreferredName() == null) {
                setPreferredName(firstName);
            }
            Searcher.getSearcher().addIndex(this);
            userModified();
        }
    }

    public void setLastName(String lastName) {
        //added for when csv parsing creates obj from default constructor
        if (this.lastName == null) {
            this.lastName = lastName;
        }
        if ((!lastName.equals(this.lastName))) {
        	Searcher.getSearcher().removeIndex(this);
            this.lastName = lastName;
            Searcher.getSearcher().addIndex(this);
        }
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName.substring(0, 1).toUpperCase() + preferredName.substring(1);
        userModified();
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
        userModified();
    }

    public LocalDateTime getDeathDate() {
        return death;
    }

    public void setDeathDate(LocalDateTime death) {
        this.death = death;
        userModified();
    }

    public String getDeathStreet() {
        return deathStreet;
    }

    public void setDeathStreet(String deathStreet) {
        this.deathStreet = deathStreet;
        clearCurrentLocation();
        userModified();
    }


    public String getDeathCity() {
        return deathCity;
    }

    public void setDeathCity(String deathCity) {
        this.deathCity = deathCity;
        clearCurrentLocation();
        userModified();
    }

    public Region getDeathRegion() {
        return deathRegion;
    }

    public void setDeathRegion(Region region) {
        this.deathRegion = region;
        clearCurrentLocation();
        userModified();
    }

    /**
     * Calculates the patients current age. If the patient is living, it is the difference between the current datetime
     * and their date of birth, else if they are dead it is the difference between their date of death and date of birth
     *
     * @return Their calculated age
     */
    public int getAge() {
        if (this.death != null) {
            return (int) ChronoUnit.YEARS.between(this.birth, this.death);
        } else {
            return (int) ChronoUnit.YEARS.between(this.birth, LocalDate.now());
        }
    }

    /**
     * Gets the status of the patient; donating, receiving, both, neither (null)
     *
     * @return The patient's status
     */
    private Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the patient; donating, receiving, both, neither (null)
     *
     * @param status The status of the patient
     */
    private void setStatus(Status status) {
        this.status = status;
        userModified();
    }

    public Timestamp getCREATED() {return CREATED;}

    /**
     * Refreshes the status of the patient to the correct status
     * Always called after patient is modified
     */
    private void refreshStatus() {
        Status newStatus = null;
        if (this.donations.size() > 0 && this.requiredOrgans.size() > 0) {
            newStatus = (Status) Status.getEnumFromString("both");
        } else if (this.donations.size() > 0) {
            newStatus = (Status) Status.getEnumFromString("donating");
        } else if (this.requiredOrgans.size() > 0) {
            newStatus = (Status) Status.getEnumFromString("receiving");
        }
        if (getStatus() != newStatus) {
            setStatus(newStatus);
        }
    }
    public PreferredGender getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(PreferredGender gender) {
        this.preferredGender = gender;
        userModified();
    }

    public BirthGender getBirthGender() {
        return birthGender;
    }

    public void setBirthGender(BirthGender gender) {
        this.birthGender = gender;

        if (getPreferredGender() == null && gender != null) {
            if (gender.getValue().equals("Male")) {
                setPreferredGender(PreferredGender.MAN);
            } else {
                setPreferredGender(PreferredGender.WOMAN);
            }
        }
        userModified();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        userModified();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        userModified();
    }

    /**
     * Calculates the Body Mass Index of the patient
     *
     * @return The calculated BMI
     */
    public double getBmi() {
        DecimalFormat df = new DecimalFormat("#.0");
        if (this.height == 0) return 0.0;
        else return Double.valueOf(df.format(this.weight / (Math.pow(this.height, 2))));
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
        userModified();
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        clearCurrentLocation();
        userModified();
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
        clearCurrentLocation();
        userModified();
    }

    public void setCity(String city) {
        this.city = city;
        clearCurrentLocation();
        userModified();
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) throws DataFormatException {
        if (suburb != null && !suburb.equals(this.suburb)) {
            for (char c : suburb.toCharArray()) {
                if (c > 127) {
                    throw new DataFormatException("");
                }
            }
            this.suburb = suburb;
            clearCurrentLocation();
            userModified();
        }
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        clearCurrentLocation();
        userModified();
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
        clearCurrentLocation();
        userModified();
    }


    /**
     * Don't use! Unless this is for testing purposes
     * @return the current LatLng
     */
    public LatLng getCurrentLocationForTestingOnly() {
        return this.currentLocation;
    }

    /**
     * Gets the current location of the patient as a LatLng
     * Returns the death location if the patient is dead
     * Returns null if the location could not be geocoded correctly
     * @return the location of the patient as a latLong
     * @throws InterruptedException InterruptedException
     * @throws ApiException ApiException
     * @throws IOException IOException
     */
    public LatLng getCurrentLocation() throws InterruptedException, ApiException, IOException {
        if (currentLocation == null) {
            if (this.isDead()) {
                return APIGoogleMaps.getApiGoogleMaps().geocodeAddress(this.getDeathLocationConcat());
            } else {
                if (this.getFormattedAddress().trim().equals("0")) { //if only part of address to google is 0 for default zip
                    return null;
                }
                return APIGoogleMaps.getApiGoogleMaps().geocodeAddress(this.getFormattedAddress());
            }
        }
        return currentLocation;
    }

    public boolean isDead() {
        if (deathStreet == null || deathCity == null || deathRegion == null) {
            return false;
        }

        return !(deathStreet.isEmpty() && deathCity.isEmpty());
    }


    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Clears the current latitude and longitude coordinates of the patient
     */
    private void clearCurrentLocation() {
        this.currentLocation = null;
    }

    /**
     * Gets the current medication list for a Patient
     *
     * @return ArrayList medications the Patient currently uses
     */
    public List<Medication> getCurrentMedications() {
        return currentMedications;
    }

    /**
     * Gets the medication history for a Patient
     *
     * @return ArrayList medications the Patient used to use
     */
    public List<Medication> getMedicationHistory() {
        return medicationHistory;
    }

    /**
     * Sets the current medication list for a Patient
     *
     * @param currentMedications medications to set as current for the Patient
     */
    public void setCurrentMedications(List<Medication> currentMedications) {
        this.currentMedications = currentMedications;
        currentMedications.forEach(x -> x.setMedicationStatus(MedicationStatus.CURRENT));
        userModified();
    }

    /**
     * Sets the medication history for a Patient
     *
     * @param medicationHistory medication list to set as history for a Patient
     */
    public void setMedicationHistory(List<Medication> medicationHistory) {
        this.medicationHistory = medicationHistory;
        medicationHistory.forEach(x -> x.setMedicationStatus(MedicationStatus.HISTORY));
        userModified();
    }

    /**
     * gets the current required organs of the patient
     *
     * @return required organs of the patient
     */
    public Map<Organ, OrganReceival> getRequiredOrgans() {
        return this.requiredOrgans;
    }

    public void clearRequiredOrgans() {
        requiredOrgans.clear();
    }


    /**
     * Returns true if there exists at least one required organ that does not have an assignee
     * @return whether it exists or not
     */
    public boolean hasUnassignedRequiredOrgans() {

        for (OrganReceival receival : this.requiredOrgans.values()) {
            if (receival.getDonorNhi() == null || receival.getDonorNhi().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * sets the required organs of the patient to the list parsed through
     *
     * @param requiredOrgans organs the patient is to receive
     */
    public void setRequiredOrgans(Map<GlobalEnums.Organ, OrganReceival> requiredOrgans) {
        this.requiredOrgans = requiredOrgans;
        userModified();
    }


    /**
     * Gets a formatted address that contains no nulls
     * @return - return formatted address string
     */
    @SuppressWarnings("WeakerAccess")
    public String getFormattedAddress() {
        return String.format("%s %s %s %s %s %s",
                Objects.toString(streetNumber, ""),
                Objects.toString(streetName, ""),
                Objects.toString(suburb, ""),
                Objects.toString(city, ""),
                Objects.toString(region, ""),
                Objects.toString(zip, ""));
    }

    /**
     * Add organs to patient donations list
     *
     * @param organ - organ to add to the patients donation list
     * @return string of message
     */
    public String addDonation(Organ organ) {
        if (donations.keySet().contains(organ)) {
            return "Organ " + organ + " is already part of the patient's donations, so was not added.";
        } else {
            donations.put(organ, null);
            userModified();
            userActions.log(INFO, "Added organ " + organ + " to patient donations", new String[] {"Attempted to add organ " + organ + " to patient donations", nhiNumber});
            return "Successfully added " + organ + " to donations";
        }
    }

    /**
     * Add organs to patient requirements map, along with the current datetime
     *
     * @param organ - organ to add to the patient required organs map
     * @return string of message
     */
    public String addRequired(Organ organ) {
        if (requiredOrgans != null) {
            if (requiredOrgans.containsKey(organ)) {
                return "Organ " + organ + " is already part of the patient's required organs, so was not added.";
            }
        }
        if (requiredOrgans == null) {
            requiredOrgans = new HashMap<>();
        }
        OrganReceival organReceival = new OrganReceival(LocalDate.now());
        requiredOrgans.put(organ, organReceival);
        userActions.log(INFO, "Added organ " + organ + " to patient required organs", "Attempted to add organ " + organ + " to patient required organs");
        return "Successfully added " + organ + " to required organs";
    }

    /**
     * Remove organs from patients donations list
     *
     * @param organ - organ to remove from the patients donations list
     * @return string of message
     */
    public String removeDonation(Organ organ) {
        if (donations.keySet().contains(organ)) {
            donations.remove(organ);
            userModified();
            userActions.log(INFO, "Removed " + organ + " from patient donations", "Attempted to remove donation from a patient");
            return "Successfully removed " + organ + " from donations";
        } else {
            return "Organ " + organ + " is not part of the patients donations, so could not be removed.";
        }
    }

    /**
     * Remove organs from patients required organs list
     *
     * @param organ - organ to remove from the patients required organs list
     * @return string of message
     */
    public String removeRequired(Organ organ) {
        if (requiredOrgans.containsKey(organ)) {
            requiredOrgans.remove(organ);
            userModified();
            setRemovedOrgan(organ);
            return "Successfully removed " + organ + " from required organs";
        } else {
            return "Organ " + organ + " is not part of the patient's required organs, so could not be removed.";
        }
    }

    private GlobalEnums.Organ getRemovedOrgan() {
        return removedOrgan;
    }

    private void setRemovedOrgan(GlobalEnums.Organ organ) {
        removedOrgan = organ;
        userModified();
    }


    public String getNhiNumber() {
        return nhiNumber;
    }


    public void setNhiNumber(String nhiNumber) throws IllegalArgumentException {
            ensureValidNhi(nhiNumber);
            //added for when csv parsing creates obj from default constructor
            if (this.nhiNumber == null) {
                this.nhiNumber = nhiNumber;
            }
            if (!this.nhiNumber.equals(nhiNumber.toUpperCase())) {
                Searcher.getSearcher().removeIndex(this);
                this.nhiNumber = nhiNumber.toUpperCase();
                userModified();
            }
        }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        userModified();
        this.homePhone = homePhone;
        userModified();
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        userModified();
        this.mobilePhone = mobilePhone;
        userModified();
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        userModified();
        this.workPhone = workPhone;
        userModified();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        userModified();
        this.emailAddress = emailAddress;
        userModified();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        userModified();
        this.contactName = contactName;
        userModified();
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        userModified();
        this.contactRelationship = contactRelationship;
        userModified();
    }

    public String getContactHomePhone() {
        return contactHomePhone;
    }

    public void setContactHomePhone(String contactHomePhone) {
        userModified();
        this.contactHomePhone = contactHomePhone;
        userModified();
    }

    public String getContactMobilePhone() {
        return contactMobilePhone;
    }

    public void setContactMobilePhone(String contactMobilePhone) {
        userModified();
        this.contactMobilePhone = contactMobilePhone;
        userModified();
    }

    public String getContactWorkPhone() {
        return contactWorkPhone;
    }

    public void setContactWorkPhone(String contactWorkPhone) {
        this.contactWorkPhone = contactWorkPhone;
        userModified();
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
        userModified();
    }


    public List<Procedure> getProcedures() {
        if (procedures == null) {
            procedures = new ArrayList<>();
        }
        return procedures;
    }


    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
        userModified();
    }

    /**
     * Gets the list of user action history logs
     * DO NOT USE UNLESS FROM LOGGER CLASS
     *
     * @return the list of user records
     */
    public List<PatientActionRecord> getUserActionsList() {
        return userActionsList; //this is modifiable on purpose!
    }

    /**
     * Gets the current diseases infecting a donor
     *
     * @return ArrayList current diseases
     */
    public List<Disease> getCurrentDiseases() {
        return this.currentDiseases;
    }

    /**
     * Sets the donor's current diseases to the given list
     *
     * @param currentDiseases list of diseases currently infecting a donor
     */
    public void setCurrentDiseases(List<Disease> currentDiseases) {
        this.currentDiseases = currentDiseases;
        userModified();
    }

    /**
     * Gets the diseases the donor used to be infected with
     *
     * @return ArrayList past diseases
     */
    public List<Disease> getPastDiseases() {
        return this.pastDiseases;
    }

    /**
     * Set the donor's past diseases to the given list
     *
     * @param pastDiseases list of diseases that used to infect a donor
     */
    public void setPastDiseases(List<Disease> pastDiseases) {
        this.pastDiseases = pastDiseases;
        userModified();
    }

    /**
     * Checks all diseases for tags and orders them into the correct list
     * Cured - Past Diseases
     * Chronic, Null - Current Diseases
     */
    public void sortDiseases() {
        for (Disease disease : new ArrayList<>(pastDiseases)) {
            if (disease.getDiseaseState() == DiseaseState.CHRONIC || disease.getDiseaseState() == null) {
                currentDiseases.add(disease);
                pastDiseases.remove(disease);
            }
        }
        for (Disease disease : new ArrayList<>(currentDiseases)) {
            if (disease.getDiseaseState() == DiseaseState.CURED) {
                pastDiseases.add(disease);
                currentDiseases.remove(disease);
            }
        }
    }

    public void addProcedure(Procedure procedure) {
        procedures.add(procedure);
        userModified();
    }

    public void removeProcedure(Procedure procedure) {
        procedures.remove(procedure);
        userModified();
    }

    public void setUserActionsList(List<PatientActionRecord> records) {
        this.userActionsList = records;
        userModified();
    }

    public String getAddressString() {
        String addressString = "";
        if (streetNumber != null) {
            addressString += streetNumber + " ";
        }
        if (streetName != null) {
            addressString += streetName;
        }
        if (suburb != null) {
            if (addressString.length() != 0) {
                addressString += ", " + suburb;
            } else {
                addressString += suburb;
            }
        }
        return addressString;
    }

    public String getStreetName() {
        return streetName;
    }


    public String getCity() {
        return city;
    }

    public String getDeathLocationConcat(){
        String addressString = "";
        if (deathStreet != null) {
            addressString += deathStreet;
        }
        if (deathCity != null) {
            if (addressString.length() != 0) {
                addressString += ", " + deathCity;
            } else {
                addressString += deathCity;
            }
        }
        if (deathRegion != null) {
            if (addressString.length() != 0) {
                addressString += ", " + deathRegion;
            } else {
                addressString += deathRegion;
            }
        }
        return addressString;
    }

    public List<String> getDonationNhis() {
        List<String> nhis = new ArrayList<>();
        for (String nhi : donations.values()) {
            if (nhi != null) {
                nhis.add(nhi);
            }
        }
        return nhis;
    }

    public String toString() {
        return "Patient: \n" + "NHI: " + nhiNumber + "\n" + "Created date: " + CREATED + "\n" + "Modified date: " + modified + "\n" + "First name: "
                + firstName + "\n" + "Middle names: " + middleNames + "\n" + "Last name: " + lastName + "\n" + "Preferred name: " + preferredName +
                "\n" + "Gender Assigned at Birth: " + birthGender + "\n" + "Gender Identity: " + preferredGender + "\n" + "Date of birth: " + birth +
                "\n" + "Organs to donate: " + donations + "\n" + "Organs requested: " + requiredOrgans + "\n" + "Street1: " + streetNumber + "\n" +
                "City: " + streetName + "\n" + "Suburb:" + suburb + "\n" + "Region: " + region + "\n" + "Zip: " + zip + "\n" + "Date of death: "
                + death + "\n" + "Death location: " + deathStreet + "\n" + "Death city: " + deathCity + "\n" + "Death region: " + deathRegion + "\n"
                + "Height: " + height + "\n" + "Weight: " + weight + "\n" + "Blood group: " + bloodGroup + "\n";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Patient)) {
            return false;
        }
        Patient patient = (Patient) obj;
        return this.nhiNumber.equals(patient.nhiNumber);
    }


    @Override
    public int hashCode() {
        return nhiNumber.hashCode();
    }
}
