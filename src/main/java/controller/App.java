package controller;
import model.Donor;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Scanner;

public class App
{
    //TODO: This will need to be reworked... NB: just skeleton


    private String userInput;
    private Scanner inputScanner;

    public App(){
        userInput = "No Input";
        inputScanner = new Scanner( System.in );
    }

    private boolean validateUserInputString(String input){
        //TODO: check if correct input (contains only chars)
        setUserInput(input);
        return true; //TODO: just to compile
    }

    private void displayAllOptions() {
        System.out.println("Here are the available options!\n");
        System.out.println("1. Add a new Donor \n" +
                "2. Set attributes of a particular donor \n" +
                "3. View attributes of a particular donor \n");
    }


    private boolean validateUserDate(String input){
        setUserInput(input);
        return true; //TODO: implement
    }

    private void addDonorUI() {
        //TODO: leave this to Hayden, i will finish this tonight.
        System.out.println("You decided to add a donor!");
        System.out.print("Please enter the new donors first name: ");
        validateUserInputString(getInputScanner().next());
        String firstName = getUserInput();
        System.out.print("Please enter middle name(s) (if applicable): ");
        validateUserInputString(getInputScanner().next()); //TODO:
        String middleName = getUserInput();
        System.out.print("Please enter the last name(s): ");
        validateUserInputString(getInputScanner().next());
        String lastName = getUserInput();
        System.out.print("Please enter the date of birth (dd/mm/yyyy): "); //TODO:
        validateUserDate(getInputScanner().next());
        DateTime date = DateTime.parse(getUserInput());
//        Donor newDonor = new Donor(firstName,middleName, lastName, date); //TODO: Donor constructor needs to take all 3 names
//        Database.AddDonor(newDonor);

    }

    private void setAttributesUI(){

    }

    private void viewAttributes(){

    }

    public void start() {
        System.out.println("Welcome to ODMS!");
        while (!getUserInput().equals("q")){
            displayAllOptions();
            System.out.print("Please enter an option (as a number): ");
            setUserInput(getInputScanner().next());
            switch (getUserInput()){
                case "1":
                    addDonorUI();
                    break;
                case "2":
                    setAttributesUI();
                    break;
                case "3":
                    viewAttributes();
                    break;
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
