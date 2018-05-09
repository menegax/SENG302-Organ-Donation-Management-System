package controller;

import static utility.UserActionHistory.userActions;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.SearchPatients;
import utility.UserActionHistory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

        // Add scenes
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("patientRegister", FXMLLoader.load(getClass().getResource("/scene/patientRegister.fxml")));
        ScreenControl.addScreen("clinicianHome", FXMLLoader.load(getClass().getResource("/scene/clinicianHome.fxml")));
        ScreenControl.addScreen("patientHome", FXMLLoader.load(getClass().getResource("/scene/patientHome.fxml")));

        // add objects
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        addDummyTestObjects();
        ensureDefaultClinician();
        SearchPatients.createFullIndex(); // index patients for search, needs to be after importing or adding any patients

        primaryStage.setResizable(false);
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

            // Add dummy patients for testing
            ArrayList<String> middles = new ArrayList<>();
            middles.add("Middle");
            middles.add("Xavier");
            Database.addPatient(new Patient("ABC1238", "Joe", middles, "Bloggs", LocalDate.of(1990, 2, 9)));
            Database.getPatientByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.LIVER);
            Database.getPatientByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            Database.getPatientByNhi("ABC1238")
                    .setRegion(GlobalEnums.Region.AUCKLAND);
            Database.getPatientByNhi("ABC1238")
                    .setGender(GlobalEnums.Gender.OTHER);

            Database.addPatient(new Patient("ABC1234", "Jane", middles, "Doe", LocalDate.of(1990, 2, 9)));
            Database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.LIVER);
            Database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            Database.getPatientByNhi("ABC1234")
                    .setRegion(GlobalEnums.Region.CANTERBURY);
            Database.getPatientByNhi("ABC1234")
                    .setGender(GlobalEnums.Gender.FEMALE);
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Unable to add dummy patients", "Attempted to load dummy patients for testing");
        }

    }

    private void ensureDefaultClinician() {

        // if default clinician 0 not in db, add it
        if (!Database.isClinicianInDb(0)) {
            Database.addClinician(new Clinician(0, "initial", new ArrayList<String>() {{ add("Middle"); }}, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
        }

    }
}
