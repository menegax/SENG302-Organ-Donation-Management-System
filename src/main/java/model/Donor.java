package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.BloodGroup;
import utility.GlobalEnums.Gender;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;
import utility.SearchDonors;
import utility.UserActionRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Donor {

    private UUID uuid = UUID.randomUUID();

    private final Timestamp CREATED;

    private String firstName;

    private ArrayList<String> middleNames;

    private String lastName;

    private LocalDate birth;

    private LocalDate death;

    private Gender gender;

    private double height; //Height in meters

    private double weight; //Weight in kilograms

    private BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private Region region;

    private int zip;

    private ArrayList<Organ> donations;

    private Timestamp modified;

    private String nhiNumber;

    private ArrayList<Medication> currentMedications = new ArrayList<>();

    private ArrayList<Medication> medicationHistory = new ArrayList<>();

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

    //private HashMap<Timestamp, ArrayList<String>> medicationLog;

    private ArrayList<String> medicationLog;

    public Donor(String nhiNumber, String firstName,
                 ArrayList<String> middleNames, String lastName, LocalDate date) {
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = date;
        this.nhiNumber = nhiNumber.toUpperCase();
        this.donations = new ArrayList<>();
    }

    /**
     * Sets the attributes of the donor
     *
     * @param firstName   first name
     * @param lastName    last name
     * @param middleNames middle names
     * @param birth       birth date
     * @param death       death date
     * @param street1     street 1 of address
     * @param street2     street2 of address
     * @param suburb      suburb of address
     * @param region      region of address
     * @param gender      gender of address
     * @param bloodGroup  blood group
     * @param height      height in meters
     * @param weight      weight in kilograms
     * @param nhi         nhi
     */
    public void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames, LocalDate birth, LocalDate death, String street1,
                                 String street2, String suburb, String region, String gender, String bloodGroup, double height, double weight,
                                 String nhi) throws IllegalArgumentException {
        Enum globalEnum;
        SearchDonors.removeIndex(this);
        if (firstName != null) {
            setFirstName(firstName);
        }
        if (lastName != null) {
            setLastName(lastName);
        }
        if (middleNames != null) {
            setMiddleNames(middleNames);
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
            globalEnum = GlobalEnums.Region.getEnumFromString(region);
            if (globalEnum != null) {
                setRegion((GlobalEnums.Region) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid region", "attempted to update donor attributes");
            }
        }
        if (gender != null) {
            globalEnum = GlobalEnums.Gender.getEnumFromString(gender);
            if (globalEnum != null) {
                setGender((GlobalEnums.Gender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid gender", "attempted to update donor attributes");
            }
        }
        if (bloodGroup != null) {
            globalEnum = GlobalEnums.BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) {
                setBloodGroup((GlobalEnums.BloodGroup) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid blood group", "attempted to update donor attributes");
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
        userActions.log(Level.INFO, "Successfully updated donor " + getNhiNumber(), "attempted to update donor attributes");
        donorModified();
        SearchDonors.addIndex(this);
    }


    /**
     * Update the organ donations list of the donor
     *
     * @param newDonations - list of organs to add
     * @param rmDonations  - list of organs to remove
     */
    public void updateDonations(ArrayList<String> newDonations, ArrayList<String> rmDonations) {
        if (newDonations != null) {
            for (String organ : newDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    userActions.log(Level.WARNING, "Invalid organ \"" + organ + "\"given and not added", "attempted to add to donor donations");
                } else {
                    userActions.log(Level.INFO, addDonation(organEnum), "attempted to update donor donations");
                    donorModified();
                }
            }
        }
        if (rmDonations != null) {
            for (String organ : rmDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    userActions.log(Level.SEVERE,"Invalid organ \"" + organ + "\" given and not removed", "attempted to remove from donor donations");}
                 else {
                    userActions.log(Level.INFO, removeDonation(organEnum), "attempted to remove from donor donations");
                    donorModified();
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


    /**
     * Checks the uniqueness of the nhi number
     *
     * @throws IllegalArgumentException when the nhi number given is already in use
     */
    public void ensureUniqueNhi() throws IllegalArgumentException {
        for (Donor d : Database.getDonors()) {
            if (d.nhiNumber.equals(nhiNumber.toUpperCase())) {
                throw new IllegalArgumentException("NHI number " + nhiNumber.toUpperCase() + " is not unique");
            }
        }
    }


    /**
     * Returns the name of the donor as a formatted concatenated string
     *
     * @return string named
     */
    public String getNameConcatenated() {
        StringBuilder concatName = new StringBuilder(firstName + " ");
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


    public void setDonations(ArrayList<Organ> donations) {
        if (this.donations != donations) {
            this.donations = donations;
            donorModified();
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
            this.firstName = firstName;
            donorModified();
        }
    }


    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }


    public void setMiddleNames(ArrayList<String> middleNames) {
        if (this.middleNames == null || (!middleNames.equals(this.middleNames))) {
            this.middleNames = middleNames;
            donorModified();
        }
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        if (this.lastName == null || (!lastName.equals(this.lastName))) {
            this.lastName = lastName;
            donorModified();
        }
    }


    public LocalDate getBirth() {
        return birth;
    }


    public void setBirth(LocalDate birth) {
        if (this.birth == null || (!birth.equals(this.birth))) {
            this.birth = birth;
            donorModified();
        }
    }


    public LocalDate getDeath() {
        return death;
    }


    public void setDeath(LocalDate death) {
        if (this.death == null || (!death.equals(this.death))) {
            this.death = death;
            donorModified();
        }
    }


    /**
     * Calculates the donors current age. If the patient is living, it is the difference between the current datetime
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


    public Gender getGender() {
        return gender;
    }


    public void setGender(Gender gender) {
        if (this.gender != gender) {
            this.gender = gender;
            donorModified();
        }
    }


    public double getHeight() {
        return height;
    }


    public void setHeight(double height) {
        if (this.height != height) {
            this.height = height;
            donorModified();
        }
    }


    public double getWeight() {
        return weight;
    }


    public void setWeight(double weight) {
        if (this.weight != weight) {
            this.weight = weight;
            donorModified();
        }
    }


    /**
     * Calculates the Body Mass Index of the donor
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
            donorModified();
        }
    }


    public String getStreet1() {
        return street1;
    }


    public void setStreet1(String street1) {
        if (this.street1 == null || (!street1.equals(this.street1))) {
            this.street1 = street1;
            donorModified();
        }
    }


    public String getStreet2() {
        return street2;
    }


    public void setStreet2(String street2) {
        if (this.street2 == null || (!street2.equals(this.street2))) {
            this.street2 = street2;
            donorModified();
        }
    }


    public String getSuburb() {
        return suburb;
    }


    public void setSuburb(String suburb) {
        if (this.suburb == null || !suburb.equals(this.suburb)) {
            this.suburb = suburb;
            donorModified();
        }
    }


    public Region getRegion() {
        return region;
    }


    public void setRegion(Region region) {
        if (this.region != region) {
            this.region = region;
            donorModified();
        }
    }


    public int getZip() {
        return zip;
    }


    /**
     * Gets the current medication list for a Donor
     * @return ArrayList medications the Donor currently uses
     */
    public ArrayList<Medication> getCurrentMedications() {

        return currentMedications == null? new ArrayList<>() : currentMedications;
    }

    /**
     * Gets the medication history for a Donor
     * @return ArrayList medications the Donor used to use
     */
    public ArrayList<Medication> getMedicationHistory() {
        return medicationHistory == null? new ArrayList<>(): medicationHistory;
    }

    /**
     * Sets the current medication list for a Donor
     * @param currentMedications medications to set as current for the Donor
     */
    public void setCurrentMedications(ArrayList<Medication> currentMedications) {
        this.currentMedications = currentMedications;
    }

    /**
     * Sets the medication history for a Donor
     * @param medicationHistory medication list to set as history for a Donor
     */
    public void setMedicationHistory(ArrayList<Medication> medicationHistory) {
        this.medicationHistory = medicationHistory;
    }

    public void setZip(int zip) {
        if (this.zip != zip) {
            this.zip = zip;
            donorModified();
        }
    }


    public String getFormattedAddress() {
        return street1 + " " + street2 + " " + suburb + " " + region + " " + zip;
    }


    public Timestamp getModified() {
        return modified;
    }


    /**
     * Add organs to donor donations list
     *
     * @param organ - organ to add to the donors donation list
     * @return string of message
     */
    public String addDonation(Organ organ) {
        if (donations.contains(organ)) {
            return "Organ " + organ + " is already part of the donor's donations, so was not added.";
        }
        else {
            donations.add(organ);
            donorModified();
            return "Successfully added " + organ + " to donations";
        }
    }


    /**
     * Remove organs from donors donations list
     *
     * @param organ - organ to remove from the donors donations list
     * @return string of message
     */
    public String removeDonation(Organ organ) {
        if (donations.contains(organ)) {
            donations.remove(organ);
            donorModified();
            return "Successfully removed " + organ + " from donations";
        } else {
            return "Organ " + organ + " is not part of the donors donations, so could not be removed.";
        }
    }


    public String getNhiNumber() {
        return nhiNumber;
    }


    public void setNhiNumber(String nhiNumber) throws IllegalArgumentException {
        ensureValidNhi();
        if (!this.nhiNumber.equals(nhiNumber.toUpperCase())) {
            this.nhiNumber = nhiNumber.toUpperCase();
            donorModified();
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


    /**
     * Returns a converted medication log ArrayList to a UserActionRecord OberservableList
     * @return The medication log as a UserActionRecord ObservableList
     */
    public ObservableList<UserActionRecord> getMedicationLog() {
        ObservableList<UserActionRecord> currentLog = FXCollections.observableArrayList();
        String time = null, level = null, message = null, action;

        if (this.medicationLog != null) {
            for (int i = 0; i < medicationLog.size(); i++) {
                if (i % 4 == 0) {
                    time = medicationLog.get(i);
                } else if (i % 4 == 1) {
                    level = medicationLog.get(i);
                } else if (i % 4 == 2) {
                    message = medicationLog.get(i);
                } else {
                    action = medicationLog.get(i);
                    currentLog.add(new UserActionRecord( time, level, message, action ) );
                }
            }
        } else {
            return null;
        }
        return currentLog;
    }

    /**
     * Sets the medicationLog as a HashMap converted from a UserActionRecord ObservableList
     * @param log The UserActionRecord ObservableList
     */
    public void setMedicationLog(ObservableList<UserActionRecord> log) {
        ArrayList<String> newLog = new ArrayList<>();

        for (UserActionRecord record : log) {
            newLog.add(record.getTimestamp());
            newLog.add(record.getLevel());
            newLog.add(record.getMessage());
            newLog.add(record.getAction());
        }
        this.medicationLog = newLog;
    }

    private void donorModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }


    public String toString() {
        return "Donor: \n" + "NHI: " + nhiNumber + "\n" + "Created date: " + CREATED + "\n" + "Modified date: " + modified + "\n" + "First name: "
                + firstName + "\n" + "Middle names: " + middleNames + "\n" + "Last name: " + lastName + "\n" + "Gender: " + gender + "\n"
                + "Date of birth: " + birth + "\n" + "Organs to donate: " + donations + "\n" + "Street1: " + street1 + "\n" + "Street2: " + street2
                + "\n" + "Suburb:" + suburb + "\n" + "Region: " + region + "\n" + "Zip: " + zip + "\n" + "Date of death: " + death + "\n" + "Height: "
                + height + "\n" + "Weight: " + weight + "\n" + "Blood group: " + bloodGroup + "\n";
    }


    public boolean equals(Object obj) {
        Donor donor = (Donor) obj;
        return this.nhiNumber.equals(donor.nhiNumber) && obj.getClass() == this.getClass();
    }
}
