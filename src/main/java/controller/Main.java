package controller;

import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import utility.UserActionHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

public class Main extends Application {

    Database database = Database.getDatabase();

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
//        database.importFromDiskPatients("./patient.json");
//        database.importFromDiskClinicians("./clinician.json");
//        database.importFromDiskWaitlist("./");
        database.loadAll();
        addDummyTestObjects();
        ensureDefaultClinician();
        SearchPatients.createFullIndex(); // index patients for search, needs to be after importing or adding any patients

        setUpMenuBar(primaryStage);

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
            database.add(new Patient("ABC1238", "Joe", middles, "Bloggs", LocalDate.of(1990, 2, 9)));
            database.getPatientByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.LIVER);
            database.getPatientByNhi("ABC1238")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            database.getPatientByNhi("ABC1238")
                    .setRegion(GlobalEnums.Region.AUCKLAND);
            database.getPatientByNhi("ABC1238")
                    .setGender(GlobalEnums.Gender.OTHER);

            database.add(new Patient("ABC1234", "Jane", middles, "Doe", LocalDate.of(1990, 2, 9)));
            database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.LIVER);
            database.getPatientByNhi("ABC1234")
                    .addDonation(GlobalEnums.Organ.CORNEA);
            database.getPatientByNhi("ABC1234")
                    .setRegion(GlobalEnums.Region.CANTERBURY);
            database.getPatientByNhi("ABC1234")
                    .setGender(GlobalEnums.Gender.FEMALE);
        }
        catch (Exception e) {
            userActions.log(Level.WARNING, "Unable to add dummy objects", "Attempted to load dummy objects for testing");
        }

    }


    /**
     * Adds the default clinician if there isn't one already
     */
    private void ensureDefaultClinician() {

        // if default clinician 0 not in db, add it
        if (!database.isClinicianInDb(0)) {
            database.addClinician(new Clinician(0, "initial", new ArrayList<String>() {{
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
}
