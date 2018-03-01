package controller;

import model.Donor;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import service.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class App {
    //TODO: This will need to be reworked... NB: just skeleton

    private String userInput;

    private Scanner inputScanner;

    private DateTimeFormatter dateFormatter;


    public App() {
        userInput = "No Input";
        inputScanner = new Scanner(System.in);
        dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    }

    private void displayAllOptions() {
        System.out.print("\nChoose from the following:\n");
        System.out.println("\n1. Add a donor " +
                "\n2. View all donors " +
                "\n3. Update a donor " +
                "\n4. View a donor ");
    }


    private boolean validateUserDate(String input) {
        try {
            LocalDate.parse(input);
        } catch (IllegalArgumentException e) {
            setUserInput(null);
            return false;
        }
        setUserInput(input);
        return true;
    }


    /**
     * Checks that string does not contain any digits
     */
    private boolean validateUserInputString(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                setUserInput(null);
                return false;
            }
        }
        setUserInput(input);
        return true;
    }

    /**
     * Should be after each action. A way for user to see results of action, and continue when ready
     */
    private void displayContinueUI() {
        System.out.print("Press enter to continue...\n");
        inputScanner.nextLine();
    }

    /**
     * Continues to ask user for input if invalid, add more cases to this as program grows
     */
    private void validateUserInput(String type, String userPrompt) {
        boolean passResult = false;
        System.out.println(userPrompt);
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
                    if (validateString.length() == 0) {
                        setUserInput(null); //set to null if no input
                        passResult = true;
                    } else if (validateUserInputString(validateString)) //need else if so that method isnt called and middle is set to first name
                        passResult = true;
                    else
                        System.out.println("Sorry, invalid input. Please try again.");
            }
            if (!passResult) {
                System.out.print(userPrompt);
                validateString = inputScanner.nextLine();
            }
        }
    }

    private void addDonorUI() {
        //first name
        validateUserInput("String", "First name: ");
        String firstName = getUserInput();

        //middle name
        ArrayList<String> middleNames = new ArrayList<>();
        String middleName = "";
        while (middleName != null) {
            validateUserInput("nullablestring", "Enter a single middle name (if applicable): ");
            middleName = getUserInput();
            middleNames.add(middleName);
        }

        //last name
        validateUserInput("String", "Last name: ");
        String lastName = getUserInput();

        //date of birth
        validateUserInput("date", "Date of birth (yyyy-mm-dd): ");
        LocalDate dateOfBirth = new LocalDate(LocalDate.parse(userInput));

        //add the donor
        Donor newDonor = new Donor(firstName, middleNames, lastName, dateOfBirth);
        Database.addDonor(newDonor);
        System.out.println(newDonor);
        displayContinueUI();
        }

//    private void selectDonorForAttributesUI() {
//        System.out.print("Select a Donor (by ID) to add attributes to:");
//        viewAllDonorsUI();
//        int selectedDonorId = inputScanner.nextInt();
//        inputScanner.nextLine(); //to get rid of newlines/carriage returns
//        try {
//            setAttributesUI(Database.getDonorById(selectedDonorId));
//        } catch (DonorNotFoundException e) {
//            System.out.println(e.toString());
//        }
//    }

        private void setAttributesUI (Donor d){
            System.out.println("Please select an attribute (by ID) to update for donor " + d.getNameConcatenated());
            System.out.print("1. Gender\n " +
                    "2. Height\n" +
                    "3. Weight\n" +
                    "4. Blood Group\n" +
                    "5. Current Address\n" +
                    "6. Organs to donate\n");
            displayContinueUI(); //TODO: implement after here! will need to get attribute and set
        }

        private void viewAttributes () {

        }

        private void viewAllDonorsUI () {
            for (Donor donor : Database.getDonors()) {
                System.out.println(String.format("\nID: %s, Name: %s", donor.getNameConcatenated()));
            }
        }

        public void start () {
            System.out.println("Welcome to the Organ Donor Management System!");
            System.out.println("==========================================================");
            while (!getUserInput().equals("q")) {
                displayAllOptions();
                System.out.print("Please enter a number option (q to quit): ");
                setUserInput(getInputScanner().nextLine());
                switch (getUserInput()) {
                    case "1":
                        addDonorUI(); //TODO: separate into classes
                        break;
                    case "2":
                        viewAllDonorsUI();
                        break;
                    case "3":
//                    selectDonorForAttributesUI();//TODO: separate into classes
                        break;
                    case "4":
                        viewAttributes();
                    default:

                }
            }
        }

        public String getUserInput () {
            return userInput;
        }

        public void setUserInput (String userInput){
            this.userInput = userInput;
        }

        public Scanner getInputScanner () {
            return inputScanner;
        }

        public void setInputScanner (Scanner inputScanner){
            this.inputScanner = inputScanner;
        }

    }
