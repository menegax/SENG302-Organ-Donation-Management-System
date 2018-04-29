package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.UserActionHistory;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

//        setUpDummyDonors();
        Database.importFromDisk("./patient.json");

        // Add FXML screens to ScreenControl
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("patientRegister", FXMLLoader.load(getClass().getResource("/scene/patientRegister.fxml")));
        ScreenControl.addScreen("clinicianHome", FXMLLoader.load(getClass().getResource("/scene/clinicianHome.fxml")));
        ScreenControl.addScreen("patientHome", FXMLLoader.load(getClass().getResource("/scene/patientHome.fxml")));

        // initialising new clinician on startup
        ArrayList<String> mid = new ArrayList<>();
        mid.add("Middle");
        Database.addClinician("initial", mid, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY);

        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }


    private void setUpDummyPatients() throws InvalidObjectException {
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.importFromDisk("./patient.json");
        Database.addPatients(new Patient("ABC1238", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("ABC1238").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("ABC1238").addDonation(GlobalEnums.Organ.CORNEA);
    }
}
