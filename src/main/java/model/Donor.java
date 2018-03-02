package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Hashtable;

import service.Database;
import utility.GlobalEnums;

public class Donor {

    private final Timestamp CREATED;

    private String firstName;

    private ArrayList<String> middleNames;

    private String lastName;

    private LocalDate birth;

    private LocalDate death;

    private GlobalEnums.Gender gender;

    private double height;

    private int weight;

    private GlobalEnums.BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private GlobalEnums.Region region;

    private int zip;

    private ArrayList<GlobalEnums.Organ> organsToDonate;

    private Timestamp modified;

    private int irdNumber;

    public Donor(int irdNumber, String firstName,
                 ArrayList<String> middleNames, String lastName, LocalDate date) throws IllegalArgumentException{

        this.CREATED = new Timestamp(System.currentTimeMillis());
        ensureUniqueIrd(irdNumber);
        this.modified = CREATED;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.birth = date;
        this.irdNumber = irdNumber;
    }

    public static void ensureUniqueIrd(int irdNumber) throws IllegalArgumentException{
        for (Donor d: Database.getDonors()){
            if (d.irdNumber == irdNumber){
                throw new IllegalArgumentException("IRD number is not unique");
            }
        }
    }

    public String getNameConcatenated() {
        return firstName + " " + (middleNames == null ? "" : middleNames + " ") + lastName;
    }

    public ArrayList<GlobalEnums.Organ> getOrgansToDonate() {
        return organsToDonate;
    }

    public void setOrgansToDonate(ArrayList<GlobalEnums.Organ> organsToDonate) {
        this.organsToDonate = organsToDonate;
    }

    public Timestamp getCREATED() {
        return CREATED;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleName) {
        this.middleNames = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public LocalDate getDeath() {
        return death;
    }

    public void setDeath(LocalDate death) {
        this.death = death;
    }

    public GlobalEnums.Gender getGender() {
        return gender;
    }

    public void setGender(GlobalEnums.Gender gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public GlobalEnums.BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(GlobalEnums.BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public GlobalEnums.Region getRegion() {
        return region;
    }

    public void setRegion(GlobalEnums.Region region) {
        this.region = region;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public void addOrganToDonate(GlobalEnums.Organ organ){
        organsToDonate.add(organ);
    }

    public int getIrdNumber() {
        return irdNumber;
    }

    public void setIrdNumber(int irdNumber) {
        this.irdNumber = irdNumber;
    }


    public String toString() {
        return "Donor: " +
                "created: " + CREATED + " " +
                "ird: " + irdNumber + " " +
                "firstName: " + firstName + " " +
                "middleNames: " + middleNames + " " +
                "lastName: " + lastName + " " +
                "dateOfBirth: " + birth.toString() + " " +
                "Organs to Donate: " + organsToDonate;
    }
}
