package model;

import java.util.HashSet;
import java.util.UUID;

import org.joda.time.DateTime;
import utility.GlobalEnums;

public class Donor {

    private final UUID UUID; //todo make final

    private final DateTime CREATED; //todo make final

    private String name;

    private DateTime birth;

    private DateTime death;

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

//    TODO: need constructor something like this...
//    public Donor(String firstName, String middleName, String lastName, DateTime date){
//        UUID = java.util.UUID.randomUUID(); //todo make UUID
//        CREATED = DateTime.now();
//        modified = CREATED;
//
//        this.firstName = firstName;
//        if (!middleName.equals(null)){
//            this.middleName = middleName;
//        }
//        if (!lastName.equals(null)){
//            this.lastName = lastName;
//        }
//        birth = date.toDate(); //TODO: needs just date
//    }

    public Donor(String newName, DateTime newDateOfBirth) {
        UUID = java.util.UUID.randomUUID(); //todo make UUID
        CREATED = DateTime.now();
        modified = CREATED;
        name = newName;
        birth = newDateOfBirth;
    }

    public UUID getUuid() {
        return UUID;
    }

    public DateTime getCREATED() {
        return CREATED;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getBirth() {
        return birth;
    }

    public void setBirth(DateTime birth) {
        this.birth = birth;
    }

    public DateTime getDeath() {
        return death;
    }

    public void setDeath(DateTime death) {
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
        return "Donor:" +
                " UUID: " + UUID +
                " Name: " + name;
    }
}
