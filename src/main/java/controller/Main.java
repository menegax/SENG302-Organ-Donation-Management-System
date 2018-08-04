package controller;

import static java.util.logging.Level.ALL;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.OFF;
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
import service.AdministratorDataService;
import service.ClinicianDataService;
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

        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app");
        prepareApplication();
       // openKeyboard();
        primaryStage.show();
    }

    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        SystemLogger.setup();
        launch(args);
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

    private void prepareApplication() {
        AdministratorDataService dataService = new AdministratorDataService();
        ClinicianDataService clinicianDataService = new ClinicianDataService();
        if (clinicianDataService.getClinician(0) == null) {
            systemLogger.log(INFO, "Default clinician not in database. Adding default clinician to database.");
            clinicianDataService.save(new Clinician(0, "Rob", new ArrayList<>(), "Burns", GlobalEnums.Region.CANTERBURY));
        }
        if (dataService.getAdministratorByUsername("admin") == null) {
            systemLogger.log(INFO, "Default admin not in database. Adding default admin to database.");
            dataService.save(new Administrator("admin", "John", new ArrayList<>(), "Smith", "password"));
        }

    }
}
