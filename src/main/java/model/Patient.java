package model;

import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import utility.PatientActionRecord;
import utility.SearchPatients;
import utility.UserActionRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Patient extends User {

    private Timestamp CREATED;

    private String firstName;

    private ArrayList<String> middleNames;

    private String lastName;

    private String preferredName;

    private LocalDate birth;

    private LocalDate death;

    private BirthGender birthGender;

    private PreferredGender preferredGender;

    private double height; // Height in meters

    private double weight; // Weight in kilograms

    private BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private Region region;

    private int zip;

    private ArrayList<Organ> donations;

    private ArrayList<Organ> requiredOrgans;

    private Timestamp modified;

    private String nhiNumber;

    private ArrayList<Medication> currentMedications = new ArrayList<>();

    private ArrayList<Medication> medicationHistory = new ArrayList<>();

    private List<Procedure> procedures = new ArrayList<>();

    private String homePhone;

    private String mobilePhone;

    private String workPhone;

    private String emailAddress;

    private String contactName;

    private String contactRelationship;

    private String contactHomePhone;

    private String contactMobilePhone;

    private String contactWorkPhone;

    private String contactEmailAddress;

    private ArrayList<PatientActionRecord> userActionsList;

    private ArrayList<Disease> currentDiseases = new ArrayList<>();

    private ArrayList<Disease> pastDiseases = new ArrayList<>();

    private boolean hasBeenModified = false;

    private GlobalEnums.Organ removedOrgan;

    /**
     * Constructor for the patient class. Initializes basic attributes
     * @param nhiNumber unique number to identify the patient by
     * @param firstName first name of the patient
     * @param middleNames middle names of the patient
     * @param lastName last name of the patient
     * @param date date of birth of patient
     */
    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate date) {
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.firstName = firstName;
        this.preferredName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = date;
        this.nhiNumber = nhiNumber.toUpperCase();
        this.donations = new ArrayList<>();
        this.userActionsList = new ArrayList<>();
        this.requiredOrgans = new ArrayList<>();
    }

    public Patient(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate birth,
                   Timestamp created, Timestamp modified, LocalDate death, GlobalEnums.BirthGender gender,
                   GlobalEnums.PreferredGender prefGender, String preferredName, double height, double weight,
                   BloodGroup bloodType, ArrayList<Organ> donations, ArrayList<Organ> receiving, String street1,
                   String street2, String suburb, Region region, int zip, String homePhone, String workPhone,
                   String mobilePhone, String emailAddress, String contactName, String contactRelationship,
                   String contactHomePhone, String contactWorkPhone, String contactMobilePhone, String contactEmailAddress,
                   ArrayList<PatientActionRecord> userActionsList, ArrayList<Disease> currentDiseases,
                   ArrayList<Disease> pastDiseases, ArrayList<Medication> currentMedications,
                   ArrayList<Medication> medicationHistory, List<Procedure> procedures) {
        this.nhiNumber = nhiNumber;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = birth;
        this.CREATED = created;
        this.modified = modified;
        this.death = death;
        this.birthGender = gender;
        this.preferredGender = prefGender;
        this.preferredName = preferredName;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodType;
        this.donations = donations;
        this.requiredOrgans = receiving;
        this.street1 = street1;
        this.street2 = street2;
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
    }


    /**
     * Sets the attributes of the patient
     *
     * @param firstName         first name
     * @param lastName          last name
     * @param middleNames       middle names
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
        SearchPatients.removeIndex(this);
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
        patientModified();
        SearchPatients.addIndex(this);
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
                    patientModified();
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
                    patientModified();
                }
            }
        }
    }

    /**
     * Checks that the nhi number consists (only) of 3 letters then 4 numbers
     *
     * @exception IllegalArgumentException when the nhi number given is not in the valid format
     */
    public void ensureValidNhi() throws IllegalArgumentException {
        if (!Pattern.matches("[A-Z]{3}[0-9]{4}", nhiNumber.toUpperCase())) {
            throw new IllegalArgumentException(
                    "NHI number " + nhiNumber.toUpperCase() + " is not in the correct format (3 letters followed by 4 numbers)");
        }
    }

//    /**
//     * Checks the uniqueness of the nhi number
//     *
//     * @throws IllegalArgumentException when the nhi number given is already in use
//     */
//    public void ensureUniqueNhi() throws IllegalArgumentException {
//        for (Patient p : database.getPatients()) {
//            if (p.nhiNumber.equals(nhiNumber.toUpperCase())) {
//                throw new IllegalArgumentException("NHI number " + nhiNumber.toUpperCase() + " is not unique");
//            }
//        }
//    }

    /**
     * Returns the name of the patient as a formatted concatenated string
     *
     * @return string named
     */
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
            patientModified();
        }
    }

    public Timestamp getCREATED() {
        return CREATED;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (this.firstName == null || (!firstName.equals(this.firstName))) {
        	SearchPatients.removeIndex(this);
            this.firstName = firstName;
            if (getPreferredName() == null) {
                setPreferredName( firstName );
            }
            SearchPatients.addIndex(this);
            patientModified();
        }
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleNames) {
        if (this.middleNames == null || (!middleNames.equals(this.middleNames))) {
        	SearchPatients.removeIndex(this);
            this.middleNames = middleNames;
            SearchPatients.addIndex(this);
            patientModified();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (this.lastName == null || (!lastName.equals(this.lastName))) {
        	SearchPatients.removeIndex(this);
            this.lastName = lastName;
            SearchPatients.addIndex(this);
            patientModified();
        }
    }

    public String getPreferredName() { return preferredName; }

    public void setPreferredName(String preferredName) {
        if (preferredName != null && !preferredName.equals(this.preferredName)) {
            this.preferredName = preferredName.substring(0, 1).toUpperCase() + preferredName.substring(1);
            patientModified();
        }
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        if (this.birth == null || (!birth.equals(this.birth))) {
            this.birth = birth;
            patientModified();
        }
    }

    public LocalDate getDeath() {
        return death;
    }

    public void setDeath(LocalDate death) {
        if (this.death == null || (!death.equals(this.death))) {
            this.death = death;
            patientModified();
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

    public PreferredGender getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(PreferredGender gender) {
        if (this.preferredGender != gender) {
            this.preferredGender = gender;
            patientModified();
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
            patientModified();
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (this.height != height) {
            this.height = height;
            patientModified();
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (this.weight != weight) {
            this.weight = weight;
            patientModified();
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
            patientModified();
        }
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        if (this.street1 == null || (!street1.equals(this.street1))) {
            this.street1 = street1;
            patientModified();
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        if (this.street2 == null || (!street2.equals(this.street2))) {
            this.street2 = street2;
            patientModified();
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        if (this.suburb == null || !suburb.equals(this.suburb)) {
            this.suburb = suburb;
            patientModified();
        }
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        if (this.region != region) {
            this.region = region;
            patientModified();
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
        patientModified();
    }

    /**
     * Sets the medication history for a Patient
     * @param medicationHistory medication list to set as history for a Patient
     */
    public void setMedicationHistory(ArrayList<Medication> medicationHistory) {
        this.medicationHistory = medicationHistory;
        patientModified();
    }

    public void setZip(int zip) {
        if (this.zip != zip) {
            this.zip = zip;
            patientModified();
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
        patientModified();
    }

    public String getFormattedAddress() {
        return street1 + " " + street2 + " " + suburb + " " + region + " " + zip;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) { this.modified = modified; }

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
            patientModified();
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
        patientModified();
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
            patientModified();
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
            patientModified();
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
        patientModified();
    }

    public String getNhiNumber() {
        return nhiNumber;
    }

    public void setNhiNumber(String nhiNumber) throws IllegalArgumentException {
        ensureValidNhi();
        if (!this.nhiNumber.equals(nhiNumber.toUpperCase())) {
            SearchPatients.removeIndex(this);
        	this.nhiNumber = nhiNumber.toUpperCase();
            SearchPatients.addIndex(this);
        	patientModified();
        }
    }


    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
        patientModified();
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
        patientModified();
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
        patientModified();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        patientModified();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
        patientModified();
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
        patientModified();
    }

    public String getContactHomePhone() {
        return contactHomePhone;
    }

    public void setContactHomePhone(String contactHomePhone) {
        this.contactHomePhone = contactHomePhone;
        patientModified();
    }

    public String getContactMobilePhone() {
        return contactMobilePhone;
    }

    public void setContactMobilePhone(String contactMobilePhone) {
        this.contactMobilePhone = contactMobilePhone;
        patientModified();
    }

    public String getContactWorkPhone() {
        return contactWorkPhone;
    }

    public void setContactWorkPhone(String contactWorkPhone) {
        this.contactWorkPhone = contactWorkPhone;
        patientModified();
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
        patientModified();
    }

    public List<Procedure> getProcedures() {
        if (procedures == null) {
            procedures = new ArrayList<>();
        }
        return procedures;
    }

    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
        patientModified();
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
    public void setCurrentDiseases(ArrayList<Disease> currentDiseases) {
        this.currentDiseases = currentDiseases;
        patientModified();
    }

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
    public void setPastDiseases(ArrayList<Disease> pastDiseases) {
        this.pastDiseases = pastDiseases;
        patientModified();
    }


    /**
     *
     * Updates the modified timestamp of the patient
     */
    private void patientModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
        this.hasBeenModified = true;
    }

    public void setNotModified() {
        this.hasBeenModified = false;
    }

    public void addProcedure(Procedure procedure) {
        procedures.add(procedure);
        patientModified();
    }

    public void removeProcedure(Procedure procedure) {
        procedures.remove(procedure);
        patientModified();
    }

    public String toString() {
        return "Patient: \n" + "NHI: " + nhiNumber + "\n" + "Created date: " + CREATED + "\n" + "Modified date: " + modified + "\n" + "First name: "
                + firstName + "\n" + "Middle names: " + middleNames + "\n" + "Last name: " + lastName + "\n" + "Preferred name: " + preferredName +
                "\n" + "Gender Assigned at Birth: " + birthGender + "\n" + "Gender Identity: " + preferredGender + "\n" + "Date of birth: " + birth +
                "\n" + "Organs to donate: " + donations + "\n" + "Street1: " + street1 + "\n" + "Street2: " + street2 + "\n" + "Suburb:" + suburb +
                "\n" + "Region: " + region + "\n" + "Zip: " + zip + "\n" + "Date of death: " + death + "\n" + "Height: " + height + "\n" + "Weight: "
                + weight + "\n" + "Blood group: " + bloodGroup + "\n";
    }

    public boolean equals(Object obj) {
        Patient patient = (Patient) obj;
        return this.nhiNumber.equals(patient.nhiNumber) && obj.getClass() == this.getClass();
    }
}
