package model;

import utility.GlobalEnums;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Human {

//    public Timestamp CREATED;
//
//    public String firstName;
//
//    public ArrayList<String> middleNames;
//
//    public String lastName;
//
//    public LocalDate birth;
//
//    public LocalDate death;
//
//    public GlobalEnums.Gender gender;
//
//    public double height; //Height in meters
//
//    public double weight; //Weight in kilograms
//
//    public GlobalEnums.BloodGroup bloodGroup;
//
//    public String street1;
//
//    public String street2;
//
//    public String suburb;
//
//    public GlobalEnums.Region region;
//
//    public int zip;
//
//    public ArrayList<GlobalEnums.Organ> requirements;
//
//    public Timestamp modified;
//
//    public String nhiNumber;

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
     * @param height      height
     * @param weight      weight
     * @param nhi         nhi
     */
    public abstract void updateAttributes(String firstName, String lastName, ArrayList<String> middleNames, LocalDate birth, LocalDate death, String street1,
                                 String street2, String suburb, String region, String gender, String bloodGroup, double height, double weight,
                                 String nhi) throws IllegalArgumentException;

    /**
     * Update the organ donations list of the donor
     *
     * @param newDonations - list of organs to add
     * @param rmDonations  - list of organs to remove
     */
    public abstract void updateDonations(ArrayList<String> newDonations, ArrayList<String> rmDonations);

    /**
     * Checks that the nhi number consists (only) of 3 letters then 4 numbers
     *
     * @exception IllegalArgumentException when the nhi number given is not in the valid format
     */
    public abstract void ensureValidNhi() throws IllegalArgumentException;

    /**
     * Checks the uniqueness of the nhi number
     *
     * @exception IllegalArgumentException when the nhi number given is already in use
     */
    public abstract void ensureUniqueNhi() throws IllegalArgumentException;

    /**
     * Returns the name of the donor as a formatted concatenated string
     *
     * @return string named
     */
    public abstract String getNameConcatenated();

    public abstract ArrayList<GlobalEnums.Organ> getDonations();

    public abstract void setDonations(ArrayList<GlobalEnums.Organ> donations);

    public abstract Timestamp getCREATED();

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract ArrayList<String> getMiddleNames();

    public abstract void setMiddleNames(ArrayList<String> middleNames);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract LocalDate getBirth();

    public abstract void setBirth(LocalDate birth);

    public abstract LocalDate getDeath();

    public abstract void setDeath(LocalDate death);

    /**
     * Calculates the donors current age. If the patient is living, it is the difference between the current datetime
     * and their date of birth, else if they are dead it is the difference between their date of death and date of birth
     *
     * @return Their calculated age
     */
    public abstract int getAge();

    public abstract GlobalEnums.Gender getGender();

    public abstract void setGender(GlobalEnums.Gender gender);

    public abstract double getHeight();

    public abstract void setHeight(double height);

    public abstract double getWeight();

    public abstract void setWeight(double weight);

    /**
     * Calculates the Body Mass Index of the donor
     *
     * @return The calculated BMI
     */
    public abstract double getBmi();

    public abstract GlobalEnums.BloodGroup getBloodGroup();

    public abstract void setBloodGroup(GlobalEnums.BloodGroup bloodGroup);

    public abstract String getStreet1();

    public abstract void setStreet1(String street1);

    public abstract String getStreet2();

    public abstract void setStreet2(String street2);

    public abstract String getSuburb();

    public abstract void setSuburb(String suburb);

    public abstract GlobalEnums.Region getRegion();

    public abstract void setRegion(GlobalEnums.Region region);

    public abstract int getZip();

    public abstract void setZip(int zip);

    public abstract String getFormattedAddress();

    public abstract Timestamp getModified();

    /**
     * Add organs to donor donations list
     *
     * @param organ - organ to add to the donors donation list
     * @return string of message
     */
    public abstract String addDonation(GlobalEnums.Organ organ);

    /**
     * Remove organs from donors donations list
     *
     * @param organ - organ to remove from the donors donations list
     * @return string of message
     */
    public abstract String removeDonation(GlobalEnums.Organ organ);

    public abstract String getNhiNumber();

    public abstract void setNhiNumber(String nhiNumber) throws IllegalArgumentException;

    public abstract void donorModified();

    public abstract String toString();

    public abstract boolean equals(Object obj);
}
