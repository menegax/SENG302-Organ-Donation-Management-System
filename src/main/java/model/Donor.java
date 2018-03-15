package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.logging.Level;

import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.*;

import static utility.UserActionHistory.userActions;

public class Donor {

    private final Timestamp CREATED;

    private String firstName;

    private ArrayList<String> middleNames;

    private String lastName;

    private LocalDate birth;

    private LocalDate death;

    private Gender gender;

    private double height;

    private double weight;

    private BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private Region region;

    private int zip;

    private ArrayList<Organ> donations;

    private Timestamp modified;

    private int irdNumber;

    public Donor(int irdNumber, String firstName,
                 ArrayList<String> middleNames, String lastName, LocalDate date) throws IllegalArgumentException {

        this.CREATED = new Timestamp(System.currentTimeMillis());
        ensureUniqueIrd(irdNumber);
        this.modified = CREATED;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = date;
        this.irdNumber = irdNumber;
        this.donations = new ArrayList<>();
    }


    /**
     * Sets the attributes of the donor
     * @param firstName first name
     * @param lastName last name
     * @param middleNames middle names
     * @param birth birth date
     * @param death death date
     * @param street1 street 1 of address
     * @param street2 street2 of address
     * @param suburb suburb of address
     * @param region region of address
     * @param gender gender of address
     * @param bloodGroup blood group
     * @param height height
     * @param weight weight
     * @param ird ird
     */
    public void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames,
                                 LocalDate birth, LocalDate death, String street1, String street2,
                                 String suburb, String region, String gender, String bloodGroup,
                                 double height, double weight, int ird) {
        Enum globalEnum;
        if (firstName != null) setFirstName(firstName);
        if (lastName != null) setLastName(lastName);
        if (middleNames != null) setMiddleNames(middleNames);
        if (birth != null) setBirth(birth);
        if (death != null) setDeath(death);
        if (street1 != null) setStreet1(street1);
        if (street2 != null) setStreet2(street2);
        if (suburb != null) setSuburb(suburb);
        if (region != null) {
            globalEnum = GlobalEnums.Region.getEnumFromString(region);
            if (globalEnum != null) {
                setRegion((GlobalEnums.Region) globalEnum);
            } else{
                userActions.log(Level.WARNING, "Invalid region, for help on what entries are valid, use donor update -h.");
            }
        }
        if (gender != null) {
            globalEnum = GlobalEnums.Gender.getEnumFromString(gender);
            if (globalEnum != null) setGender((GlobalEnums.Gender) globalEnum);
            else {
                userActions.log(Level.WARNING, "Invalid gender, for help on what entries are valid, use donor update -h.");
            }
        }
        if (bloodGroup != null) {
            globalEnum = GlobalEnums.BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) setBloodGroup((GlobalEnums.BloodGroup) globalEnum);
            else{
                userActions.log(Level.WARNING, "Invalid blood group, for help on what entries are valid, use donor update -h.");
            }

        }
        if (height > 0) setHeight(height);
        if (weight > 0) setWeight(weight);
        if (ird > 0) setIrdNumber(ird);
        userActions.log(Level.INFO, "Successfully updated " + getNameConcatenated() + "\n");
    }

    /**
     * Update the organ donations list of the donor
     * @param newDonations - list of organs to add
     * @param rmDonations - list of organs to remove
     */
    public void updateDonations(ArrayList<String> newDonations, ArrayList<String> rmDonations) {
        if (newDonations != null) {
            for (String organ : newDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    userActions.log(Level.SEVERE, "Invalid organ " + organ + "given, hence was not added.");
                }
                else {
                    userActions.log(Level.INFO, addDonation(organEnum));
                }
            }
        }
        if (rmDonations != null) {
            for (String organ : rmDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    userActions.log(Level.SEVERE,"Invalid organ " + organ + " given, hence was not removed.");
                } else {
                    userActions.log(Level.INFO, removeDonation(organEnum));
                }
            }
        }
    }

    /**
     * Checks the uniqueness of the ird number
     * @param irdNumber - ird number of the donor
     * @throws IllegalArgumentException when the ird number given is already in use
     */
    private static void ensureUniqueIrd(int irdNumber) throws IllegalArgumentException {
        for (Donor d : Database.getDonors()) {
            if (d.irdNumber == irdNumber) {
                throw new IllegalArgumentException("IRD number " + irdNumber + " is not unique");
            }
        }
    }

    /**
     * Returns the name of the donor as a formatted concatenated string
     * @return string named
     */
    public String getNameConcatenated() {
        StringBuilder concatName = new StringBuilder(firstName + " ");
        if (middleNames != null && middleNames.size() > 0) {
            for (String middleName : middleNames) {
                concatName.append(middleName).append(" ");
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

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
       if (this.bloodGroup != bloodGroup){
            this.bloodGroup = bloodGroup;
            donorModified();
        }
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        if (this.street1 == null || (!street1.equals(this.street1))){
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

    public void setZip(int zip) {
        if (this.zip != zip) {
            this.zip = zip;
            donorModified();
        }
    }

    public String getAddress() {
        return street1 + " " + street2 + " " + suburb + " " + region + " " + zip;
    }

    public Timestamp getModified() {
        return modified;
    }

    /**
     * Add organs to donor donations list
     * @param organ - organ to add to the donors donation list
     * @return string of message
     */
    public String addDonation(Organ organ) {
        if (donations.contains(organ)) {
            return "Organ " + organ + " is already part of the donor's donations, so was not added.";
        } else {
            donations.add(organ);
            donorModified();
        }
        return "Successfully added " + organ + " to donations";
    }

    /**
     * Remove organs from donors donations list
     * @param organ - organ to remove from the donors donations list
     * @return string of message
     */
    public String removeDonation(Organ organ){
        if (donations.contains(organ)) {
            donations.remove(organ);
            donorModified();
            return "Successfully removed " + organ + " from donations";
        }
        else
           return "Organ " + organ + " is not part of the donors donations, so could not be removed.";
    }

    public int getIrdNumber() {
        return irdNumber;
    }

    public void setIrdNumber(int irdNumber) {
        if (this.irdNumber != irdNumber) {
            this.irdNumber = irdNumber;
            donorModified();
        }
    }

    private void donorModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }


    public String toString() {
        return "Donor: \n" +
                "IRD: " + irdNumber + "\n" +
                "Created date: " + CREATED + "\n" +
                "Modified date: " + modified + "\n" +
                "First name: " + firstName + "\n" +
                "Middle names: " + middleNames + "\n" +
                "Last name: " + lastName + "\n" +
                "Gender: " + gender + "\n" +
                "Date of birth: " + birth + "\n" +
                "Organs to donate: " + donations + "\n" +
                "Street1: " + street1 + "\n" +
                "Street2: " + street2 + "\n" +
                "Suburb:" + suburb + "\n" +
                "Region: " + region + "\n" +
                "Zip: " + zip + "\n" +
                "Date of death: " + death + "\n" +
                "Height: " + height + "\n" +
                "Weight: " + weight + "\n" +
                "Blood group: " + bloodGroup + "\n";
    }

    public boolean equals(Object obj){
        Donor donor = (Donor) obj;
        return this.irdNumber == donor.irdNumber;
    }

}
