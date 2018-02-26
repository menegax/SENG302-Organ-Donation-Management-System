package controller;
import model.Donor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import service.Database;

import java.util.Date;
import java.util.Scanner;

public class App
{
    //TODO: This will need to be reworked... NB: just skeleton
    private String userInput;
    private Scanner inputScanner;
    private DateTimeFormatter dateFormatter;
    private Database databaseInstance;

    public App(){
        userInput = "No Input";
        inputScanner = new Scanner( System.in );
        dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        databaseInstance = new Database();
    }

    private void displayAllOptions() {
        System.out.println("Here are the available options!\n");
        System.out.println("\n1. Add a new Donor " +
                "\n2. View all donors " +
                "\n3. Set attributes of a particular donor " +
                "\n4. View attributes of a particular donor ");
    }


    private boolean validateUserDate(String input){
        try{
            dateFormatter.parseDateTime(input).toDate();
        }catch (IllegalArgumentException e){ //TODO: get correct exception
            setUserInput(null);
            return false;
        }
        setUserInput(input);
        return true;
    }


    //Checks that string does not contain any digits
    private boolean validateUserInputString(String input){
        for (char c : input.toCharArray()){
            if (Character.isDigit(c)){
                setUserInput(null);
                return false;
            }
        }
        setUserInput(input);
        return true;
    }

    //Should be after each action. A way for user to see results of action, and continue when ready
    private void displayConintueUI(){
        System.out.print("Press enter to continue...\n");
        inputScanner.nextLine();
    }

    //Continues to ask user for input if invalid, add more cases to this as program grows
    private void validateUserInput(String type, String userPrompt) {
        boolean passResult = false;
        String validateString = inputScanner.nextLine();
        while (!passResult) {
            switch (type.toLowerCase()) {
                case "date":
                    if (validateUserDate(validateString)) passResult = true;
                    else System.out.println("Sorry, invalid format. Please use (dd/mm/yyy).");
                    break;
                case "string": // has to have something in the input
                    if (validateString.length() != 0 && validateUserInputString(validateString))
                        passResult = true;
                    else System.out.println("Sorry, invalid input. Please try again.");
                    break;
                case "nullablestring": // if we allow users to just hit enter, use this one
                    if (validateString.length() == 0 || validateUserInputString(validateString))
                        passResult = true;
                    else
                        System.out.println("Sorry, invalid input. Please try again.");
            }
            if (!passResult){
                System.out.print(userPrompt);
                validateString = inputScanner.nextLine();
            }
        }
    }

    private void addDonorUI() {
        //first name
        System.out.print("Please enter the new donors first name:");
        validateUserInput("String", "Please enter the new donors first name:");
        String firstName = getUserInput();

        //middle name
        System.out.print("Please enter middle name(s) (if applicable):");
        validateUserInput("nullablestring","Please enter middle name(s) (if applicable):");
        String middleName = getUserInput();

        //last name
        System.out.print("Please enter the last name(s): ");
        validateUserInput("String","Please enter the last name(s):");
        String lastName = getUserInput();

        //date of birth
        System.out.print("Please enter the date of birth (dd/mm/yyyy):");
        validateUserInput("date","Please enter the date of birth (dd/mm/yyyy):");
        Date dateOfBirth = dateFormatter.parseDateTime(getUserInput()).toDate();

        //add the donor
        Donor newDonor = new Donor(firstName,middleName, lastName, dateOfBirth);
        databaseInstance.addDonor(newDonor);
        System.out.println(String.format("Successfully added:\n  Donor: %s%s%s",
                firstName,
                middleName == null ? "" : middleName + " ",
                lastName));
        displayConintueUI();
    }

    private void setAttributesUI(){

    }

    private void viewAttributes(){

    }

    private void viewAllDonorsUI(){
        for(Donor donor : databaseInstance.getDonors()){
            System.out.println("\n" + donor);
        }
        displayConintueUI();
    }

    public void start() {
        System.out.println("Welcome to ODMS!");
        while (!getUserInput().equals("q")){
            displayAllOptions();
            System.out.print("Please enter an option (as a number): ");
            setUserInput(getInputScanner().nextLine());
            switch (getUserInput()){
                case "1":
                    addDonorUI(); //TODO: seperate into classes
                    break;
                case "2":
                    viewAllDonorsUI();
                    break;
                case "3":
                    setAttributesUI();//TODO: seperate into classes
                    break;
                case "4":
                    viewAttributes();
                default:

            }
        }
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public Scanner getInputScanner() {
        return inputScanner;
    }

    public void setInputScanner(Scanner inputScanner) {
        this.inputScanner = inputScanner;
    }

}
