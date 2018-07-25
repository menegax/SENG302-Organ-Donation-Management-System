package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import controller.ScreenControl;
import de.codecentric.centerdevice.MenuToolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();

    @Override
    public void start(Stage primaryStage) throws IOException {

        // setup GUI
        ScreenControl screenControl = ScreenControl.getScreenControl();
        primaryStage.setTitle("Login");
        screenControl.addStage(uuid, primaryStage);
        primaryStage.setResizable(false);
        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        screenControl.show(uuid, loginScreen);

        // add objects
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        Database.importFromDiskWaitlist("./waitlist.json");
        Database.importFromDiskAdministrators("./administrator.json");

        addDummyTestObjects();
        ensureDefaultClinician();
        ensureDefaultAdministrator();
        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app");
        openKeyboard();
        primaryStage.show();
    }

    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        SystemLogger.setup();
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
                    .setBirthGender(GlobalEnums.BirthGender.MALE);

            Database.addPatient(new Patient("ABC1234", "Jane", middles, "Doe", LocalDate.of(1990, 2, 9)));
            Database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.LIVER);
            Database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            Database.getPatientByNhi("ABC1234")
                    .setRegion(GlobalEnums.Region.CANTERBURY);
            Database.getPatientByNhi("ABC1234")
                    .setBirthGender(GlobalEnums.BirthGender.FEMALE);
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Unable to add dummy patients", "Attempted to load dummy patients for testing");
            systemLogger.log(INFO, "Unable to add dummy patients");
        }
    }


    /**
     * Adds the default clinician if there isn't one already
     */
    private void ensureDefaultClinician() {
        // if default clinician 0 not in db, add it
        if (!Database.isClinicianInDb(0)) {
            systemLogger.log(INFO, "Default clinician not in database. Adding default clinician to database.");
            Database.addClinician(new Clinician(0, "Phil", new ArrayList<String>() {{
                add("");
            }}, "McGraw", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
        }

    }

    /**
     * Adds the default administrator if there isn't one already
     */
    private void ensureDefaultAdministrator() {
        // if default administrator 'admin' not in db, add it
        if (!Database.isAdministratorInDb("admin")) {
            systemLogger.log(INFO, "Default admin not in database. Adding default admin to database.");
            Database.addAdministrator(new Administrator("admin", "John", new ArrayList<>(), "Smith", "password"));
        }
    }

    /**
     * Gets the uuid hash key used for the primary stage
     * @return the uuid hash key used in the primary stage
     */
    public static UUID getUuid() {
        return uuid;
    }

    /**
     * Opens the Windows system on-screen keyboard.
     * This is only called on a Windows setup because touch controls are only available for a Windows system.
     */
    private void openKeyboard() {
        if(System.getProperty("os.name")
                .startsWith("Windows")) {
            try {
                Runtime.getRuntime().exec("cmd /c osk");
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("System keyboard could not be opened");
                alert.show();
            }
        }
    }
}
