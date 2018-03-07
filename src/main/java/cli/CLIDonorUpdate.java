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

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

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

    public void run() {
        try {
            Donor donor = Database.getDonorByIrd(searchIrd);
            donor.updateAttributes(firstName, lastName, middleNames, birth, death, street1,
                    street2, suburb, region, gender, bloodGroup, height, weight, ird);
        } catch (InvalidObjectException e) {
            System.out.println(e.getMessage());
        }
    }

}
