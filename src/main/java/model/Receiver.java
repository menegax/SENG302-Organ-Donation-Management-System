package model;

import service.Database;
import utility.GlobalEnums;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class Receiver extends Human {

    private final Timestamp CREATED;

    private String firstName;

    private ArrayList<String> middleNames;

    private String lastName;

    private LocalDate birth;

    private LocalDate death;

    private GlobalEnums.Gender gender;

    private double height; //Height in meters

    private double weight; //Weight in kilograms

    private GlobalEnums.BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private GlobalEnums.Region region;

    private int zip;

    private ArrayList<GlobalEnums.Organ> requirements;

    private Timestamp modified;

    private String nhiNumber;

    public Receiver(String nhiNumber, String firstName, ArrayList<String> middleNames, String lastName, LocalDate date) {
        this.CREATED = new Timestamp(System.currentTimeMillis());
        this.modified = CREATED;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = date;
        this.nhiNumber = nhiNumber.toUpperCase();
        this.requirements = new ArrayList<>();
    }

    /**
     * Sets the attributes of the receiver
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
     * @param height      height
     * @param weight      weight
     * @param nhi         nhi
     */
    public void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames, LocalDate birth, LocalDate death, String street1,
                                 String street2, String suburb, String region, String gender, String bloodGroup, double height, double weight,
                                 String nhi) throws IllegalArgumentException {
        Enum globalEnum;
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
                userActions.log(Level.WARNING, "Invalid region", "attempted to update receiver attributes");
            }
        }
        if (gender != null) {
            globalEnum = GlobalEnums.Gender.getEnumFromString(gender);
            if (globalEnum != null) {
                setGender((GlobalEnums.Gender) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid gender", "attempted to update receiver attributes");
            }
        }
        if (bloodGroup != null) {
            globalEnum = GlobalEnums.BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) {
                setBloodGroup((GlobalEnums.BloodGroup) globalEnum);
            }
            else {
                userActions.log(Level.WARNING, "Invalid blood group", "attempted to update receiver attributes");
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
        userActions.log(Level.INFO, "Successfully updated receiver " + getNhiNumber(), "attempted to update receiver attributes");
        //todo receiverModified();
        donorModified();
    }


    /**
     * Update the organ requirement list of the receiver
     * @param newRequirements - list of organs to add
     * @param rmRequirements  - list of organs to remove
     */
    // todo updateRequirements()
    public void updateDonations(ArrayList<String> newRequirements, ArrayList<String> rmRequirements) {
        if (newRequirements != null) {
            for (String organ : newRequirements) {
                GlobalEnums.Organ organEnum = (GlobalEnums.Organ) GlobalEnums.Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    userActions.log(Level.WARNING, "Invalid organ \"" + organ + "\"given and not added", "attempted to add to receiver requirements");
                }
                else {
                    userActions.log(Level.INFO, addDonation(organEnum), "attempted to update receiver requirements");
                    donorModified();
                }
            }
        }
        if (rmRequirements != null) {
            for (String organ : rmRequirements) {
                GlobalEnums.Organ organEnum = (GlobalEnums.Organ) GlobalEnums.Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    userActions.log(Level.SEVERE,
                            "Invalid organ \"" + organ + "\" given and not removed",
                            "attempted to remove from receiver requirements");
                }
                else {
                    userActions.log(Level.INFO, removeDonation(organEnum), "attempted to remove from receiver requirements");
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
     * @exception IllegalArgumentException when the nhi number given is already in use
     */
    public void ensureUniqueNhi() throws IllegalArgumentException {
        for (Human r : Database.getReceivers()) {
            String nhi = r.getNhiNumber();
            if (nhi.equals(nhiNumber.toUpperCase())) {
                throw new IllegalArgumentException("NHI number " + nhiNumber.toUpperCase() + " is not unique");
            }
        }
    }


    /**
     * Returns the name of the receiver as a formatted concatenated string
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


    //todo getRequirements()
    public ArrayList<GlobalEnums.Organ> getDonations() {
        return requirements == null ? new ArrayList<>() : requirements;
    }

    //todo setRequirements(ArrayList<GlobalEnums.Organ> requirements)
    public void setDonations(ArrayList<GlobalEnums.Organ> donations) {
        if (this.requirements != donations) {
            this.requirements = donations;
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
     * Calculates the receivers current age. If the patient is living, it is the difference between the current datetime
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


    public GlobalEnums.Gender getGender() {
        return gender;
    }


    public void setGender(GlobalEnums.Gender gender) {
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
     * Calculates the Body Mass Index of the receiver
     *
     * @return The calculated BMI
     */
    public double getBmi() {
        return (this.weight / (Math.pow(this.height, 2)));
    }


    public GlobalEnums.BloodGroup getBloodGroup() {
        return bloodGroup;
    }


    public void setBloodGroup(GlobalEnums.BloodGroup bloodGroup) {
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


    public GlobalEnums.Region getRegion() {
        return region;
    }


    public void setRegion(GlobalEnums.Region region) {
        if (this.region != region) {
            this.region = region;
            donorModified();
        }
    }


    public int getZip() {
        return zip;
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
     * Add organs to receiver requirements list
     *
     * @param organ - organ to add to the receiver requirements list
     * @return string of message
     */
    //todo addRequirement(GlobalEnums.Organ organ)
    public String addDonation(GlobalEnums.Organ organ) {
        if (requirements.contains(organ)) {
            return "Organ " + organ + " is already part of the receiver's requirements, so was not added.";
        }
        else {
            requirements.add(organ);
            donorModified();
            return "Successfully added " + organ + " to requirements";
        }
    }


    /**
     * Remove organs from receiver requirements list
     *
     * @param organ - organ to remove from the receiver requirements list
     * @return string of message
     */
    public String removeDonation(GlobalEnums.Organ organ) {
        if (requirements.contains(organ)) {
            requirements.remove(organ);
            donorModified();
            return "Successfully removed " + organ + " from requirements";
        }
        else {
            return "Organ " + organ + " is not part of the receiver requirements, so could not be removed.";
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

    //todo receiverModified()
    public void donorModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }


    public String toString() {
        return "Receiver: \n" + "NHI: " + nhiNumber + "\n" + "Created date: " + CREATED + "\n" + "Modified date: " + modified + "\n" + "First name: "
                + firstName + "\n" + "Middle names: " + middleNames + "\n" + "Last name: " + lastName + "\n" + "Gender: " + gender + "\n"
                + "Date of birth: " + birth + "\n" + "Organs required: " + requirements + "\n" + "Street1: " + street1 + "\n" + "Street2: " + street2
                + "\n" + "Suburb:" + suburb + "\n" + "Region: " + region + "\n" + "Zip: " + zip + "\n" + "Date of death: " + death + "\n" + "Height: "
                + height + "\n" + "Weight: " + weight + "\n" + "Blood group: " + bloodGroup + "\n";
    }


    public boolean equals(Object obj) {
        Receiver receiver = (Receiver) obj;
        return this.nhiNumber.equals(receiver.nhiNumber);
    }
}
