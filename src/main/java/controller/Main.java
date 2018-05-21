package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;
import service.OrganWaitlist;
import utility.GlobalEnums;
import utility.SearchPatients;
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
        ScreenControl screenControl = ScreenControl.getScreenControl();
        screenControl.addStage(uuid, primaryStage);
        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        screenControl.show(uuid, loginScreen);

        // add objects
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        Database.importFromDiskWaitlist("./");
        addDummyTestObjects();
        ensureDefaultClinician();
        SearchPatients.createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        setUpMenuBar(primaryStage);
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app");
        primaryStage.setResizable(false);
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
            Database.addClinician(new Clinician(0, "initial", new ArrayList<String>() {{
                add("Middle");
            }}, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
        }

    }


    /**
     * Sets up the menu bar depending on the OS
     *
     * @param primaryStage the stage to set the menu bar to
     */
    private void setUpMenuBar(Stage primaryStage) {
        if (System.getProperty("os.name")
                .startsWith("Mac")) {
            setUpMacOsMenuBar(primaryStage);
        }
        // if windows, call here...
    }


    /**
     * Creates a native-looking MacOS menu bar for the application
     *
     * @param primaryStage the root stage of the application on which to set the menu
     */
    private void setUpMacOsMenuBar(Stage primaryStage) {

        systemLogger.log(INFO, "Setting up menu bar for operating system MacOS");


        String appName = "Big Pharma";
        // Get the toolkit
        MenuToolkit tk = MenuToolkit.toolkit();

        // Create a new menu bar
        MenuBar bar = new MenuBar();

        // Add the default application menu
        bar.getMenus()
                .add(tk.createDefaultApplicationMenu(appName));

        // Add some more Menus...
        Menu menu1 = new Menu("App");
        MenuItem menu1Item1 = new MenuItem("Log out");
        menu1Item1.setOnAction(event -> System.out.println("Log out clicked"));
        MenuItem menu1Item2 = tk.createQuitMenuItem(appName);
        menu1.getItems()
                .addAll(menu1Item1, menu1Item2);

        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        menu2Item1.setOnAction(event -> System.out.println("Save clicked"));
        MenuItem menu2Item2 = new MenuItem("Import...");
        menu2Item2.setOnAction(event -> System.out.println("Import clicked"));
        menu2.getItems()
                .addAll(menu2Item1, menu2Item2);

        Menu menu3 = new Menu("Edit");
        MenuItem menu3Item1 = new MenuItem("Undo ⌃Z");
        menu3Item1.setOnAction(event -> System.out.println("Undo clicked. Soon to be  ⌘⇧Y"));
        MenuItem menu3Item2 = new MenuItem("Redo ⌃Y");
        menu3Item2.setOnAction(event -> System.out.println("Redo clicked. Soon to be ⌘Z"));
        menu3.getItems()
                .addAll(menu3Item1, menu3Item2);

        bar.getMenus()
                .addAll(menu1, menu2, menu3);

        // Use the menu bar for primary stage
        tk.setMenuBar(primaryStage, bar);

    }

    /**
     * Gets the uuid hash key used for the primary stage
     * @return the uuid hash key used in the primary stage
     */
    public static UUID getUuid() {
        return uuid;
    }
}
