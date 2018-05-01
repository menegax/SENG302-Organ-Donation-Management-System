package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Patient;
import service.Database;
import service.OrganWaitlist;
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

//        setUpDummyPatients();
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

        setUpDummyPatients();
        setUpDummyOrganRequests();
        
        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }


    private void setUpDummyOrganRequests() throws InvalidObjectException {
    	OrganWaitlist waitlist = Database.getWaitingList();
    	waitlist.add(Database.getPatientByNhi("ABC1237"), GlobalEnums.Organ.HEART);
    	waitlist.add(Database.getPatientByNhi("ABC1236"), GlobalEnums.Organ.BONE);
    	waitlist.add(Database.getPatientByNhi("ABC1239"), GlobalEnums.Organ.LIVER);
    	waitlist.add(Database.getPatientByNhi("ABC1237"), GlobalEnums.Organ.BONE);
    	
    	Patient patient = Database.getPatientByNhi("ABC1236");
    	waitlist.add(patient.getNameConcatenated(), GlobalEnums.Organ.LIVER, LocalDate.of(2001, 5, 10), patient.getRegion(), patient.getNhiNumber());
    }
    
    private void setUpDummyPatients() throws InvalidObjectException {
    	Database.resetDatabase();
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.importFromDisk("./patient.json");
        Database.addPatients(new Patient("ABC1237", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("ABC1237").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("ABC1237").addDonation(GlobalEnums.Organ.CORNEA);
        Database.getPatientByNhi("ABC1237").setRegion(GlobalEnums.Region.CANTERBURY);
        
        Database.addPatients(new Patient("ABC1239", "Jane", dal,"Bags", LocalDate.of(1990, 5, 27)));
        Database.getPatientByNhi("ABC1239").setRegion(GlobalEnums.Region.CANTERBURY);
        
        Database.addPatients(new Patient("ABC1236", "Bob", dal,"Brain", LocalDate.of(1998, 1, 26)));
        Database.getPatientByNhi("ABC1236").setRegion(GlobalEnums.Region.CANTERBURY);
    }
}
