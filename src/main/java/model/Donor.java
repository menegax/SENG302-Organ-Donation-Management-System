package model;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.joda.time.DateTime;
import service.Database;
import utility.GlobalEnums;

public class Donor {

    private final DateTime CREATED;

    private String firstName;

    private String middleName;

    private String lastName;

    private Date birth;

    private Date death;

    private GlobalEnums.Gender gender;

    private double height;

    private int weight;

    private GlobalEnums.BloodGroup bloodGroup;

    private String street1;

    private String street2;

    private String suburb;

    private GlobalEnums.Region region;

    private int zip;

    private HashSet<GlobalEnums.Organ> organ;

    private DateTime modified;

    public Donor(String firstName, String middleName, String lastName, Date date){
        CREATED = DateTime.now();
        modified = CREATED;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        birth = date; //TODO: needs just date
    }

    public String getNameConcatenated(){
        return  firstName + " " + (middleName == null? "" : middleName + " " ) + lastName;
    }

    public DateTime getCREATED() {
        return CREATED;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getDeath() {
        return death;
    }

    public void setDeath(Date death) {
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

    public HashSet<GlobalEnums.Organ> getOrgan() {
        return organ;
    }

    public void setOrgan(HashSet<GlobalEnums.Organ> organ) {
        this.organ = organ;
    }

    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    public String toString() {
        String donor = String.format("Donor: %s%s%s \nID: %s",
                firstName + " ",
                middleName == null ? "" : middleName + " ",
                lastName);
        return donor;
    }
}
