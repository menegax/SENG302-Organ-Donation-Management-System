package controller;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums;
import utility.SearchPatients;
import utility.UserActionHistory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // setup GUI
        ScreenControl.getScreenControl().addStage(GlobalEnums.Stages.PRIMARY, primaryStage); //add primary stage to screen control to construct properly
        ScreenControl screenControl = ScreenControl.getScreenControl();
        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        screenControl.show(GlobalEnums.Stages.PRIMARY, loginScreen);
        setUpMenuBar(primaryStage);


        // add objects
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        addDummyTestObjects();
        ensureDefaultClinician();
        SearchPatients.createFullIndex(); // index patients for search, needs to be after importing or adding any patients

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


    /**
     * Adds the default clinician if there isn't one already
     */
    private void ensureDefaultClinician() {

        // if default clinician 0 not in db, add it
        if (!Database.isClinicianInDb(0)) {
            Database.addClinician(new Clinician(0, "initial", new ArrayList<String>() {{
                add("Middle");
            }}, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
        }

    }

    /**
     * Sets up the menu bar depending on the OS
     *
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
     */
    private void setUpMacOsMenuBar(Stage stage) {

        String appName = "Big Pharma"; //Todo move to a string resources or config file

        UserControl userControl = new UserControl();

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
//        menu1Item1.setMnemonicParsing(true); //Todo it will parse menu item string for a key command and bind it to the stage if found
        menu1Item1.setOnAction(event -> {
            userControl.rmLoggedInUserCache();
            userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        });
        MenuItem menu1Item2 = tk.createQuitMenuItem(appName);
        menu1.getItems()
                .addAll(menu1Item1, menu1Item2);

        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        menu2Item1.setOnAction(event -> {
            Database.saveToDisk();
            userActions.log(INFO, "Successfully saved to disk", "Attempted to save to disk");
        });
        MenuItem menu2Item2 = new MenuItem("Import patients... ⌘I");
        menu2Item2.setOnAction(event -> {
            File file = new FileChooser().showOpenDialog(stage);
            if (file != null) {
                Database.importFromDiskPatients(file.getAbsolutePath());
                userActions.log(INFO, "Selected patient file for import", "Attempted to find a file for import");
                userActions.log(INFO, "Imported patient file from disk", "Attempted to import file from disk");
            }
        });
        MenuItem menu2Item3 = new MenuItem("Import clinicians...");
        menu2Item3.setOnAction(event -> {
            File file = new FileChooser().showOpenDialog(stage);
            if (file != null) {
                Database.importFromDiskPatients(file.getAbsolutePath());
                userActions.log(INFO, "Selected clinician file for import", "Attempted to find a file for import");
                userActions.log(INFO, "Imported clinician file from disk", "Attempted to import file from disk");
            }
        });
        menu2.getItems()
                .addAll(menu2Item1, menu2Item2);

        Menu menu3 = new Menu("Edit");
        MenuItem menu3Item1 = new MenuItem("Undo ⌘Z");
        menu3Item1.setOnAction(event -> System.out.println("Undo clicked")); //Todo add functionality
        MenuItem menu3Item2 = new MenuItem("Redo ⌘⇧Y");
        menu3Item2.setOnAction(event -> System.out.println("Redo clicked")); //Todo add functionality
        menu3.getItems()
                .addAll(menu3Item1, menu3Item2);

        bar.getMenus()
                .addAll(menu1, menu2, menu3);

        // Use the menu bar for primary stage
        tk.setMenuBar(stage, bar);
    }


}
