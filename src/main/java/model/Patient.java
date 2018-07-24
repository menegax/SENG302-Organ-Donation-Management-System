package model;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Validate;
import service.Database;
import utility.parsing.DateConverterCSV;
import utility.parsing.EnumConverterCSV;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import utility.PatientActionRecord;
import utility.Searcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class Patient extends User {

    private final Timestamp CREATED;

    @Parsed(field = "first_names")
    private String firstName;

    @Parsed(field = "last_names")
    private String lastName;

    private String preferredName;

    @Parsed(field = "date_of_birth")
    @Convert(conversionClass = DateConverterCSV.class)
    private LocalDate birth;

    @Parsed(field = "date_of_death")
    @Convert(conversionClass = DateConverterCSV.class)
    private LocalDate death;

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
    private String street1;

    @Parsed(field = "street_name")
    private String street2;

    @Parsed(field = "neighborhood")
    private String suburb;

    @Parsed(field = "region")
    @Convert(conversionClass = EnumConverterCSV.class)
    private Region region;

    @Parsed(field = "zip_code")
    private int zip;

    @Parsed(field = "nhi")
    private String nhiNumber;

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

    private ArrayList<Organ> donations;

    private ArrayList<Organ> requiredOrgans;

    private Timestamp modified;

    private ArrayList<Medication> currentMedications = new ArrayList<>();

    private ArrayList<Medication> medicationHistory = new ArrayList<>();

    private List<Procedure> procedures = new ArrayList<>();

    private ArrayList<PatientActionRecord> userActionsList;

    private ArrayList<Disease> currentDiseases = new ArrayList<>();

    private ArrayList<Disease> pastDiseases = new ArrayList<>();

    private GlobalEnums.Organ removedOrgan;

    public Patient() {
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
    }

    /**
     * Constructor for the patient class. Initializes basic attributes and adds listeners for status changes
     * @param nhiNumber unique number to identify the patient by
     * @param firstName first name of the patient
     * @param middleNames middle names of the patient
     * @param lastName last name of the patient
     * @param date date of birth of patient
     */
    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate date) {
        super(firstName, middleNames, lastName);
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.preferredName = firstName;
        this.birth = date;
        this.nhiNumber = nhiNumber.toUpperCase();
        this.donations = new ArrayList<>();
        this.userActionsList = new ArrayList<>();
        this.requiredOrgans = new ArrayList<>();
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(evt -> {
            refreshStatus();
        });
    }


    /**
     * Sets the attributes of the patient
     *
     * @param firstName         first name
     * @param lastName          last name
     * @param middleNames       middle names
     * @param preferredName     preferred name
     * @param birth             birth date
     * @param death             death date
     * @param street1           street 1 of address
     * @param street2           street2 of address
     * @param suburb            suburb of address
     * @param region            region of address
     * @param birthGender       gender of patient at birth
     * @param preferredGender   chosen gender of patient
     * @param bloodGroup        blood group
     * @param height            height in meters
     * @param weight            weight in kilograms
     * @param nhi               NHI
     */
    public void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames, String preferredName,
                                 LocalDate birth, LocalDate death, String street1, String street2, String suburb,
                                 String region, String birthGender, String preferredGender, String bloodGroup,
                                 double height, double weight, String nhi) throws IllegalArgumentException {
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
            setDeath(death);
        }
        if (street1 != null) {
            setStreet1(street1);
        }
        if (street2 != null) {
            setStreet2(street2);
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
                userActions.log(Level.WARNING, "Invalid region", "attempted to update patient attributes");
            }
        }
        if (birthGender != null) {
            globalEnum = BirthGender.getEnumFromString(birthGender);
            if (globalEnum != null) {
                setBirthGender((BirthGender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid birth gender", "attempted to update patient attributes");
            }
        }
        if (preferredGender != null) {
            globalEnum = PreferredGender.getEnumFromString(preferredGender);
            if (globalEnum != null) {
                setPreferredGender((PreferredGender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid preferred gender", "attempted to update patient attributes");
            }
        }
        if (bloodGroup != null) {
            globalEnum = BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) {
                setBloodGroup((BloodGroup) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid blood group", "attempted to update patient attributes");
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
        userActions.log(Level.INFO, "Successfully updated patient " + getNhiNumber(), "attempted to update patient attributes");
        userModified();
        Searcher.getSearcher().addIndex(this);
    }

    /**
     * Update the organ donations list of the patient
     *
     * @param newDonations - list of organs to add
     * @param rmDonations  - list of organs to remove
     */
    public void updateDonations(ArrayList<String> newDonations, ArrayList<String> rmDonations) {
        if (newDonations != null) {
            for (String organ : newDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    userActions.log(Level.WARNING, "Invalid organ \"" + organ + "\"given and not added", "attempted to add to patient donations");
                } else {
                    userActions.log(Level.INFO, addDonation(organEnum), "attempted to update patient donations");
                    userModified();
                }
            }
        }
        if (rmDonations != null) {
            for (String organ : rmDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    userActions.log(Level.SEVERE,"Invalid organ \"" + organ + "\" given and not removed", "attempted to remove from patient donations");}
                 else {
                    userActions.log(Level.INFO, removeDonation(organEnum), "attempted to remove from patient donations");
                    userModified();
                }
            }
        }
    }

    /**
     * Checks that the nhi number consists (only) of 3 letters then 4 numbers
     *
     * @exception IllegalArgumentException when the nhi number given is not in the valid format
     */
    public void ensureValidNhi(String nhiNumber) throws IllegalArgumentException {
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}", nhiNumber.toUpperCase())) {
            throw new IllegalArgumentException(
                    "NHI number " + nhiNumber.toUpperCase() + " is not in the correct format (3 letters followed by 4 numbers)");
        }
    }

    /**
     * Checks the uniqueness of the nhi number
     *
     * @throws IllegalArgumentException when the nhi number given is already in use
     */
    public void ensureUniqueNhi() throws IllegalArgumentException {
        for (Patient p : Database.getPatients()) {
            if (p.nhiNumber.equals(nhiNumber.toUpperCase())) {
                throw new IllegalArgumentException("NHI number " + nhiNumber.toUpperCase() + " is not unique");
            }
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
            concatName = new StringBuilder( preferredName + " " );
        } else {
            concatName = new StringBuilder( firstName + " " );
        }
        if (middleNames != null && middleNames.size() > 0) {
            for (String middleName : middleNames) {
                concatName.append(middleName)
                        .append(" ");
            }
        }
        concatName.append(lastName);
        return concatName.toString();
    }

    public ArrayList<Organ> getDonations() {
        return donations == null ? new ArrayList<>() : donations;
    }

    /**
     * Sets the donation organs of the patient to the list parsed through
     * @param donations The donations being set to the patient donations array list
     */
    public void setDonations(ArrayList<Organ> donations) {
        if (this.donations != donations) {
            this.donations = donations;
            userModified();
        }
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
                setPreferredName( firstName );
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

    public String getPreferredName() { return preferredName; }

    public void setPreferredName(String preferredName) {
        if (preferredName != null && !preferredName.equals(this.preferredName)) {
            this.preferredName = preferredName.substring(0, 1).toUpperCase() + preferredName.substring(1);
            userModified();
        }
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        if (this.birth == null || (!birth.equals(this.birth))) {
            this.birth = birth;
            userModified();
        }
    }

    public LocalDate getDeath() {
        return death;
    }

    public void setDeath(LocalDate death) {
        if (this.death == null || (!death.equals(this.death))) {
            this.death = death;
            userModified();
        }
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
        }
        else {
            return (int) ChronoUnit.YEARS.between(this.birth, LocalDate.now());
        }
    }

    /**
     * Gets the status of the patient; donating, receiving, both, neither (null)
     * @return The patient's status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the patient; donating, receiving, both, neither (null)
     * @param status The status of the patient
     */
    public void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
        }
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
            newStatus = (Status) Status.getEnumFromString( "both" );
        }
        else if (this.donations.size() > 0) {
            newStatus = (Status) Status.getEnumFromString( "donating" );
        }
        else if (this.requiredOrgans.size() > 0) {
            newStatus = (Status) Status.getEnumFromString( "receiving" );
        }
        if (getStatus() != newStatus) {
            setStatus(newStatus);
        }
    }

    public PreferredGender getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(PreferredGender gender) {
        if (this.preferredGender != gender) {
            this.preferredGender = gender;
            userModified();
        }
    }

    public BirthGender getBirthGender() {
        return birthGender;
    }

    public void setBirthGender(BirthGender gender) {
        if (this.birthGender != gender) {
            this.birthGender = gender;

            if (getPreferredGender() == null) {
                if (gender.getValue().equals("Male")) {
                    setPreferredGender( PreferredGender.MAN );
                } else {
                    setPreferredGender( PreferredGender.WOMAN );
                }
            }
            userModified();
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (this.height != height) {
            this.height = height;
            userModified();
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (this.weight != weight) {
            this.weight = weight;
            userModified();
        }
    }

    /**
     * Calculates the Body Mass Index of the patient
     *
     * @return The calculated BMI
     */
    public double getBmi() {
        DecimalFormat df = new DecimalFormat("#.0");
        if(this.height == 0) return 0.0;
        else return Double.valueOf(df.format(this.weight / (Math.pow(this.height, 2))));
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        if (this.bloodGroup != bloodGroup) {
            this.bloodGroup = bloodGroup;
            userModified();
        }
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        if (this.street1 == null || (!street1.equals(this.street1))) {
            this.street1 = street1;
            userModified();
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        if (this.street2 == null || (!street2.equals(this.street2))) {
            this.street2 = street2;
            userModified();
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        if (this.suburb == null || !suburb.equals(this.suburb)) {
            this.suburb = suburb;
            userModified();
        }
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        if (this.region != region) {
            this.region = region;
            userModified();
        }
    }

    public int getZip() {
        return zip;
    }

    /**
     * Gets the current medication list for a Patient
     * @return ArrayList medications the Patient currently uses
     */
    public ArrayList<Medication> getCurrentMedications() {
        return currentMedications;
    }

    /**
     * Gets the medication history for a Patient
     * @return ArrayList medications the Patient used to use
     */
    public ArrayList<Medication> getMedicationHistory() {
        return medicationHistory;
    }

    /**
     * Sets the current medication list for a Patient
     * @param currentMedications medications to set as current for the Patient
     */
    public void setCurrentMedications(ArrayList<Medication> currentMedications) {
        this.currentMedications = currentMedications;
    }

    /**
     * Sets the medication history for a Patient
     * @param medicationHistory medication list to set as history for a Patient
     */
    public void setMedicationHistory(ArrayList<Medication> medicationHistory) {
        this.medicationHistory = medicationHistory;
    }

    public void setZip(int zip) {
        if (this.zip != zip) {
            this.zip = zip;
            userModified();
        }
    }

    /**
     * gets the current requred organs of the patient
     * @return required organs of the patient
     */
    public ArrayList<Organ> getRequiredOrgans() {
        return this.requiredOrgans;
    }

    /**
     * sets the required organs of the patient to the list parsed through
     * @param requiredOrgans organs the patient is to receive
     */
    public void setRequiredOrgans(ArrayList requiredOrgans) {
        this.requiredOrgans = requiredOrgans;
    }

    public String getFormattedAddress() {
        return street1 + " " + street2 + " " + suburb + " " + region + " " + zip;
    }

    /**
     * Add organs to patient donations list
     *
     * @param organ - organ to add to the patients donation list
     * @return string of message
     */
    public String addDonation(Organ organ) {
        if (donations.contains(organ)) {
            return "Organ " + organ + " is already part of the patient's donations, so was not added.";
        }
        else {
            donations.add(organ);
            userModified();
            userActions.log(Level.INFO, "Added organ " + organ + " to patient donations", "Attempted to add organ " + organ + " to patient donations");
            return "Successfully added " + organ + " to donations";
        }
    }

    /**
     * Add organs to patient requirements list
     *
     * @param organ - organ to add to the patient required organs list
     * @return string of message
     */
    public String addRequired(Organ organ) {
        if (requiredOrgans != null) {
            if (requiredOrgans.contains(organ)) {
                return "Organ " + organ + " is already part of the patient's required organs, so was not added.";
            }
        }
        if (requiredOrgans == null) {
            requiredOrgans = new ArrayList<>();
        }
        requiredOrgans.add(organ);
        userModified();
        userActions.log(Level.INFO, "Added organ " + organ + " to patient required organs", "Attempted to add organ " + organ + " to patient required organs");
        return "Successfully added " + organ + " to required organs";
    }

    /**
     * Remove organs from patients donations list
     *
     * @param organ - organ to remove from the patients donations list
     * @return string of message
     */
    public String removeDonation(Organ organ) {
        if (donations.contains(organ)) {
            donations.remove(organ);
            userModified();
            userActions.log(Level.INFO, "Removed " + organ + " from patient donations", "Attempted to remove donation from a patient");
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
        if (requiredOrgans.contains(organ)) {
            requiredOrgans.remove(organ);
            userModified();
            setRemovedOrgan(organ);
            return "Successfully removed " + organ + " from required organs";
        } else {
            return "Organ " + organ + " is not part of the patient's required organs, so could not be removed.";
        }
    }

    public GlobalEnums.Organ getRemovedOrgan() {
        return removedOrgan;
    }

    public void setRemovedOrgan(GlobalEnums.Organ organ) {
        removedOrgan = organ;
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
            this.nhiNumber = nhiNumber.toUpperCase();
            Searcher.getSearcher().removeIndex(this);
            Searcher.getSearcher().addIndex(this);
        }
    }


    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
    }

    public String getContactHomePhone() {
        return contactHomePhone;
    }

    public void setContactHomePhone(String contactHomePhone) {
        this.contactHomePhone = contactHomePhone;
    }

    public String getContactMobilePhone() {
        return contactMobilePhone;
    }

    public void setContactMobilePhone(String contactMobilePhone) {
        this.contactMobilePhone = contactMobilePhone;
    }

    public String getContactWorkPhone() {
        return contactWorkPhone;
    }

    public void setContactWorkPhone(String contactWorkPhone) {
        this.contactWorkPhone = contactWorkPhone;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
    }

    public List<Procedure> getProcedures() {
        if (procedures == null) {
            procedures = new ArrayList<>();
        }
        return procedures;
    }


    /**
     * Gets the list of user action history logs
     * DO NOT USE UNLESS FROM LOGGER CLASS
     * @return the list of user records
     */
    public ArrayList<PatientActionRecord> getUserActionsList() {
        return userActionsList; //this is modifiable on purpose!
    }


    /**
     * Gets the current diseases infecting a donor
     * @return ArrayList current diseases
     */
    public ArrayList<Disease> getCurrentDiseases() {
        return this.currentDiseases;
    }

    /**
     * Sets the donor's current diseases to the given list
     * @param currentDiseases list of diseases currently infecting a donor
     */
    public void setCurrentDiseases(ArrayList<Disease> currentDiseases) { this.currentDiseases = currentDiseases; }

    /**
     * Gets the diseases the donor used to be infected with
     * @return ArrayList past diseases
     */
    public ArrayList<Disease> getPastDiseases() {
        return this.pastDiseases;
    }

    /**
     * Set the donor's past diseases to the given list
     * @param pastDiseases list of diseases that used to infect a donor
     */
    public void setPastDiseases(ArrayList<Disease> pastDiseases) { this.pastDiseases = pastDiseases; }

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
    }

    public void removeProcedure(Procedure procedure) { procedures.remove(procedure); }

    //!!??
    public String toString() {
        return "Patient: \n" + "NHI: " + nhiNumber + "\n" + "Created date: " + CREATED + "\n" + "Modified date: " + modified + "\n" + "First name: "
                + firstName + "\n" + "Middle names: " + middleNames + "\n" + "Last name: " + lastName + "\n" + "Preferred name: " + preferredName +
                "\n" + "Gender Assigned at Birth: " + birthGender + "\n" + "Gender Identity: " + preferredGender + "\n" + "Date of birth: " + birth +
                "\n" + "Organs to donate: " + donations + "\n" + "Street1: " + street1 + "\n" + "Street2: " + street2 + "\n" + "Suburb:" + suburb +
                "\n" + "Region: " + region + "\n" + "Zip: " + zip + "\n" + "Date of death: " + death + "\n" + "Height: " + height + "\n" + "Weight: "
                + weight + "\n" + "Blood group: " + bloodGroup + "\n";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Patient)) {
            return false;
        }
        Patient patient = (Patient) obj;
        return this.nhiNumber.equals(patient.nhiNumber);
    }
}
