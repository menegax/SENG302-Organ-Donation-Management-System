package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.SearchDonors;
import utility.UserActionHistory;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

        addDummyTestObjects();
        SearchDonors.createFullIndex(); // index donors for search, needs to be after importing or adding any donors

        // Add scenes
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml")));
        ScreenControl.addScreen("home", FXMLLoader.load(getClass().getResource("/scene/home.fxml")));

        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }


    /**
     * Adds dummy test objects for testing purposes
     *
     * @exception Exception when the database cannot locate the given donor
     */
    private void addDummyTestObjects() throws Exception {

        // Add dummy donors for testing
        Database.addDonor(new Donor("ABC1238", "Joe", null, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getDonorByNhi("ABC1238")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getDonorByNhi("ABC1238")
                .addDonation(GlobalEnums.Organ.CORNEA);
        Database.getDonorByNhi("ABC1238")
                .setRegion(GlobalEnums.Region.AUCKLAND);
        Database.getDonorByNhi("ABC1238")
                .setGender(GlobalEnums.Gender.OTHER);

        Database.addDonor(new Donor("ABC1234", "Jane", null, "Doe", LocalDate.of(1990, 2, 9)));
        Database.getDonorByNhi("ABC1234")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getDonorByNhi("ABC1234")
                .addDonation(GlobalEnums.Organ.CORNEA);
        Database.getDonorByNhi("ABC1234")
                .setRegion(GlobalEnums.Region.CANTERBURY);
        Database.getDonorByNhi("ABC1234")
                .setGender(GlobalEnums.Gender.FEMALE);

        // Add dummy clinician for testing
        ArrayList<String> mid = new ArrayList<>();
        mid.add("Middle");
        Database.addClinician("initial", mid, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY);

    }
}
