package cli;
import model.Donor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

@Command(name = "update", description = "used to update donor attributes")
public class CLIDonorUpdate implements Runnable{

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help message.")
    private boolean helpRequested = false;

    @Option (names = {"-s", "--search"}, required = true, description = "Search donor by the IRD number of the donor.")
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

    @Option(names = {"-g", "--gender"}, description = "The gender of the donor.")
    private String gender;

    @Option(names = {"-ht", "--height"}, description = "The height of the donor (cm).")
    private double height;

    @Option(names = {"-w", "--weight"}, description = "The weight of the donor (kg).")
    private int weight;

    @Option(names = {"-street1", "--street1"}, description = "The street1 field for the address of the donor.")
    private String street1;

    @Option(names = {"-street2", "--street2"}, description = "The street2 field for the address of the donor.")
    private String street2;

    @Option(names = {"-suburb", "--suburb"}, description = "The suburb field for the address of the donor.")
    private String suburb;

    @Option(names = {"-ird", "--ird"}, description = "The IRD number of the donor.")
    private int ird;

    @Option(names = {"-bg", "--bloodgroup"}, description = "The bloodgroup of the donor." +
            " Valid groups are {A+, A-, B+, B-, AB+, AB-}")
    private String bloodGroup;



    public ArrayList<String> updateAttributes(Donor d){
        ArrayList<String> informationMessage = new ArrayList<>();
        if (!firstName.equals(null)) d.setFirstName(firstName);
        if (!lastName.equals(null)) d.setLastName(lastName);
        if (!middleNames.equals(null)) d.setMiddleNames(middleNames);
        if (!birth.equals(null)) d.setBirth(birth);
        if (!death.equals(null)) d.setDeath(death);
        if (!gender.equals(null)){
            switch (gender.toLowerCase()){
                case "male":
                    d.setGender(GlobalEnums.Gender.MALE);
                    break;
                case "female":
                    d.setGender(GlobalEnums.Gender.FEMALE);
                    break;
                case "other":
                    d.setGender(GlobalEnums.Gender.OTHER);
                default:
                    informationMessage.add("Gender could not be updated." +
                            " Please enter either male, female or other.");
            }
        }
        if (height != 0) d.setHeight(height);
        if (weight != 0) d.setWeight(weight);
        if (ird != 0) d.setIrdNumber(ird);
        if (!bloodGroup.equals(null)) {
            switch (bloodGroup.substring(0,0).toLowerCase() +
                    bloodGroup.substring(1,1)){
                case "a+":
                    d.setBloodGroup(GlobalEnums.BloodGroup.A_POSTIVE);
                    break;
                case "a-":
                    d.setBloodGroup(GlobalEnums.BloodGroup.A_NEGATIVE);
                    break;
                case "b+":
                    d.setBloodGroup(GlobalEnums.BloodGroup.B_POSTIVE);
                    break;
                case "b-":
                    d.setBloodGroup(GlobalEnums.BloodGroup.B_NEGATIVE);
                    break;
                case "ab+":
                    d.setBloodGroup(GlobalEnums.BloodGroup.AB_POSITIVE);
                    break;
                case "ab-":
                    d.setBloodGroup(GlobalEnums.BloodGroup.AB_NEGATIVE);
                    break;
                default:
                    informationMessage.add("Invalid blood group");
            }
        }
        return informationMessage;
    }
    public void run() {
        try{
            Donor d = Database.getDonorByIrd(searchIrd);
            updateAttributes(d);
        }catch (InvalidObjectException i){
            System.out.println(i.getMessage());
        }
    }


}
