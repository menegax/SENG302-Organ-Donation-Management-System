package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDate;

import service.Database;
import utility.GlobalEnums.*;

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


    public void updateDonations(Donor d, ArrayList<String> newDonations, ArrayList<String> rmDonations) {
        if (newDonations != null) {
            for (String organ : newDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) {
                    System.out.println("Error: Invalid organ " + organ + "given, hence was not added.");
                }
                else {
                    System.out.println(addDonation(organEnum));
                }
            }
        }
        if (rmDonations != null) {
            for (String organ : rmDonations) {
                Organ organEnum = (Organ) Organ.getEnumFromString(organ);
                if (organEnum == null) {
                    System.out.println("Invalid organ " + organ + " given, hence was not added.");
                } else {
                    System.out.println(d.removeDonation(organEnum));
                }
            }
        }
    }

    public static void ensureUniqueIrd(int irdNumber) throws IllegalArgumentException {
        for (Donor d : Database.getDonors()) {
            if (d.irdNumber == irdNumber) {
                throw new IllegalArgumentException("IRD number " + irdNumber + " is not unique");
            }
        }
    }

    public String getNameConcatenated() {
        return firstName + " " + (middleNames == null ? "" : middleNames + " ") + lastName;
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
        if (!this.firstName.equals(firstName)) {
            this.firstName = firstName;
            donorModified();
        }
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleName) {
        if (!this.middleNames.equals(middleName)) {
            this.middleNames = middleName;
            donorModified();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!this.lastName.equals(lastName)) {
            this.lastName = lastName;
            donorModified();
        }
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        if (!this.birth.equals(birth)) {
            this.birth = birth;
            donorModified();
        }
    }

    public LocalDate getDeath() {
        return death;
    }

    public void setDeath(LocalDate death) {
        if (!this.death.equals(death)) {
            this.death = death;
            donorModified();
        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if (!this.gender.equals(gender)) {
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
        if (!this.bloodGroup.equals(bloodGroup)) {
            this.bloodGroup = bloodGroup;
            donorModified();
        }
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        if (!this.street1.equals(street1)) {
            this.street1 = street1;
            donorModified();
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        if (!this.street2.equals(street2)) {
            this.street2 = street2;
            donorModified();
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        if (!this.suburb.equals(suburb)) {
            this.suburb = suburb;
            donorModified();
        }
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        if (!this.region.equals(region)) {
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

    public String addDonation(Organ organ) {
        if (donations.contains(organ)) {
            return "Organ " + organ + " is already part of the donor's donations, so was not added.";
        } else {
            donations.add(organ);
            donorModified();
        }
        return "Successfully added " + organ + " to donations";
    }

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

    public void donorModified() {
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
                "Organs to Donate: " + donations + "\n" +
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
}
