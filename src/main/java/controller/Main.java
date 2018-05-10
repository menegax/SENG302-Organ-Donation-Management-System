package controller;

import static utility.UserActionHistory.userActions;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Clinician;
import model.Donor;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.SearchDonors;
import utility.UserActionHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

        // import donors
        Database.importFromDisk("./donor.json");
        addDummyTestObjects();

        SearchDonors.createFullIndex(); // index donors for search, needs to be after importing or adding any donors

        try {
            Database.importFromDiskClinicians("clinician.json");
        } catch (Exception e) {
            if (Database.getClinicians().size() == 0) {
                //Initialise default clinician
                ArrayList<String> mid = new ArrayList<>();
                mid.add("Middle");
                Database.addClinician(new Clinician(Database.getNextStaffID(), "initial", mid, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
            }
        }

        // Add scenes
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml")));
        ScreenControl.addScreen("clinicianHome", FXMLLoader.load(getClass().getResource("/scene/clinicianHome.fxml")));
        ScreenControl.addScreen("donorHome", FXMLLoader.load(getClass().getResource("/scene/donorHome.fxml")));


        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }


    /**
     * Adds dummy test objects for testing purposes
     */
    private void addDummyTestObjects() {

        try {

            // Add dummy donors for testing
            ArrayList<String> middles = new ArrayList<>();
            middles.add("Middle");
            middles.add("Xavier");
            Database.addDonor(new Donor("ABC1238", "Joe", middles, "Bloggs", LocalDate.of(1990, 2, 9)));
            Database.getDonorByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.LIVER);
            Database.getDonorByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            Database.getDonorByNhi("ABC1238")
                    .setRegion(GlobalEnums.Region.AUCKLAND);
            Database.getDonorByNhi("ABC1238")
                    .setGender(GlobalEnums.Gender.OTHER);

            Database.addDonor(new Donor("ABC1234", "Jane", middles, "Doe", LocalDate.of(1990, 2, 9)));
            Database.getDonorByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.LIVER);
            Database.getDonorByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            Database.getDonorByNhi("ABC1234")
                    .setRegion(GlobalEnums.Region.CANTERBURY);
            Database.getDonorByNhi("ABC1234")
                    .setGender(GlobalEnums.Gender.FEMALE);
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Unable to add dummy donors", "Attempted to load dummy donors for testing");
        }

    }
}
