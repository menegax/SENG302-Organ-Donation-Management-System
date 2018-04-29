package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Donor;
import model.Receiver;
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
        Database.importFromDisk("./donor.json");

        // Add FXML screens to ScreenControl
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml")));
        ScreenControl.addScreen("clinicianHome", FXMLLoader.load(getClass().getResource("/scene/clinicianHome.fxml")));
        ScreenControl.addScreen("donorHome", FXMLLoader.load(getClass().getResource("/scene/donorHome.fxml")));

        //initialising new receiver
        Database.importFromDisk("./receiver.json");

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

    private void setUpDummyReceivers() throws InvalidObjectException {
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Andre");
        Database.importFromDisk("./receiver.json");
        Database.addReceiver(new Receiver("JAM3577", "Joshua", dal,"Meneghini", LocalDate.of(1997, 9, 17)));
        Database.getDonorByNhi("JAM3577").addDonation(GlobalEnums.Organ.HEART);
        Database.getDonorByNhi("JAM3577").addDonation(GlobalEnums.Organ.INTESTINE);
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
