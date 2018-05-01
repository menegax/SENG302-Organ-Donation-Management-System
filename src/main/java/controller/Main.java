package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Clinician;
import model.Donor;
import service.Database;
import utility.GlobalEnums;
import utility.UserActionHistory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

        Database.importFromDisk("./donor.json");

        // Add FXML screens to ScreenControl
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml")));
        ScreenControl.addScreen("clinicianHome", FXMLLoader.load(getClass().getResource("/scene/clinicianHome.fxml")));
        ScreenControl.addScreen("donorHome", FXMLLoader.load(getClass().getResource("/scene/donorHome.fxml")));

        try {
            Database.importFromDiskClinicians("clinician.json");
        } catch (IOException e) {
            if (Database.getClinicians().size() == 0) {
                //Initialise default clinciian
                ArrayList<String> mid = new ArrayList<>();
                mid.add("Middle");
                Database.addClinician(new Clinician(Database.getNextStaffID(), "initial", mid, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
            }
        }

        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }


    private void setUpDummyDonors() throws InvalidObjectException {
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.importFromDisk("./donor.json");
        Database.addDonor(new Donor("ABC1238", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getDonorByNhi("ABC1238").addDonation(GlobalEnums.Organ.LIVER);
        Database.getDonorByNhi("ABC1238").addDonation(GlobalEnums.Organ.CORNEA);
    }
}
