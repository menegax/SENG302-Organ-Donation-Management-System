package controller;
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

    private boolean checkUserStrings(String input){
        for (int i = 0; i<input.length(); i++){
            //if (input.substring(i, i+1)){

            //}
        }
        return true; //TODO: just to compile
    }

    private void displayAllOptions() {
        System.out.println("Here are the available options!\n");
        System.out.println("1. Add a new Donor \n" +
                "2. Set attributes of a particular donor \n" +
                "3. View attributes of a particular donor \n");
    }

    private void addDonorUI(){
        System.out.println("You decided to add a donor!");
        System.out.print("Please enter the new donors first name: ");
        checkUserStrings(getInputScanner().next());
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
