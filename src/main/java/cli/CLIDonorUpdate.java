package cli;

import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Command(name = "update", description = "used to update donor attributes")
public class CLIDonorUpdate implements Runnable {

    @Option(names = {"-s", "--search"}, required = true, description = "Search donor by the IRD number of the donor.")
    private int searchIrd;

    @Option(names = {"-f", "--firstname"}, description = "The first name of the donor.")
    private String firstName;

    @Option(names = {"-m", "--middlenames"}, split = ",", description = "Comma-separated list of middle names of the donor.")
    private ArrayList<String> middleNames;

    @Option(names = {"-l", "--lastname"}, description = "The last name of the donor.")
    private String lastName;

    @Option(names = {"-b", "--dateofbirth"}, description = "The date of birth of the donor (yyyy-mm-dd).")
    private LocalDate birth;

    @Option(names = {"-d", "--dateofdeath"}, description = "The date of death of the donor (yyyy-mm-dd).")
    private LocalDate death;

    @Option(names = {"--gender"}, description = "The gender of the donor.")
    private String gender;

    @Option(names = {"--height"}, description = "The height of the donor (cm).")
    private double height;

    @Option(names = {"--weight"}, description = "The weight of the donor (kg).")
    private double weight;

    @Option(names = {"--street1"}, description = "The street1 field for the address of the donor.")
    private String street1;

    @Option(names = {"--street2"}, description = "The street2 field for the address of the donor.")
    private String street2;

    @Option(names = {"--suburb"}, description = "The suburb field for the address of the donor.")
    private String suburb;

    @Option(names = {"--region"}, description = "NORTHLAND, AUCKLAND, WAIKATO, BAYOFPLENTY,\n" +
            "GISBORNE, HAWKESBAY, TARANAKI, MANAWATU,\n" +
            "WELLINGTON, TASMAN, NELSON, MARLBOROUGH,\n" +
            "WESTCOAST, CANTERBURY, OTAGO, SOUTHLAND")
    private String region;


    @Option(names = {"--zip"}, description = "The zip field for the address of the donor.")
    private int zip;

    @Option(names = {"--ird"}, description = "The IRD number of the donor.")
    private int ird;

    @Option(names = {"--bloodgroup"}, description = "The blood group of the donor. Valid groups are:\n" +
            "A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE,\n" +
            "AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, O_NEGATIVE")
    private String bloodGroup;


    //todo needs unit tests
    private ArrayList<String> updateAttributes(Donor d) {
        Enum globalEnum;
        ArrayList<String> informationMessage = new ArrayList<>();
        if (firstName != null) d.setFirstName(firstName);
        if (lastName != null) d.setLastName(lastName);
        if (middleNames != null) d.setMiddleNames(middleNames);
        if (birth != null) d.setBirth(birth);
        if (death != null) d.setDeath(death);
        if (street1 != null) d.setStreet1(street1);
        if (street2 != null) d.setStreet2(street2);
        if (suburb != null) d.setSuburb(suburb);
        if (region != null) {
            globalEnum = GlobalEnums.Region.getEnumFromString(region);
            if (globalEnum != null) {
                d.setRegion((GlobalEnums.Region) globalEnum);
            } else informationMessage.add("Invalid region, for help on what entries are valid, use donor update -h.");
        }
        if (gender != null) {
            globalEnum = GlobalEnums.Gender.getEnumFromString(gender);
            if (globalEnum != null) d.setGender((GlobalEnums.Gender) globalEnum);
            else informationMessage.add("Invalid gender, for help on what entries are valid, use donor update -h.");
        }
        if (bloodGroup != null) {
            globalEnum = GlobalEnums.BloodGroup.getEnumFromString(bloodGroup);
            if (globalEnum != null) d.setBloodGroup((GlobalEnums.BloodGroup) globalEnum);
            else
                informationMessage.add("Invalid blood group, for help on what entries are valid, use donor update -h.");
        }
        if (height > 0) d.setHeight(height);
        if (weight > 0) d.setWeight(weight);
        if (ird > 0) d.setIrdNumber(ird);
        return informationMessage;
    }

    //todo needs unit test
    private void displayUpdateMessages(ArrayList<String> messages) {
        if (messages.size() == 0) {
            System.out.println("Successfully updated all fields provided");
        }
        else {
            for (String message : messages) System.out.println(message);
        }
    }

    public void run() {
        try {
            ArrayList<String> messages = updateAttributes(Database.getDonorByIrd(searchIrd));
            displayUpdateMessages(messages);
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
