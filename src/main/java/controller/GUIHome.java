package controller;

import static java.util.logging.Level.INFO;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;

import java.io.File;
import java.io.IOException;

public class GUIHome {

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    @FXML
    private TabPane horizontalTabPane;

    @FXML
    private MenuBar menuBar;


    @FXML
    public void initialize() { //Todo catch exception
        //Todo possibly create a smart way to check logged in user type
        // and then create the tabs based off that
        UserControl userControl = new UserControl();
        try {
            if (userControl.getLoggedInUser() instanceof Patient){
                addTabsPatient();
            } else if (userControl.getLoggedInUser() instanceof Clinician) {
                //addTabsClinican();
            }

            horizontalTabPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((observable1, oldStage, newStage) -> {
                        if (newStage != null) {
                            setUpMenuBar((Stage) newStage);
                        }
                    });
                }
            });

        } catch (IOException e) {
            new Alert(ERROR, "Unable to load home").show();
            e.printStackTrace(); //todo : remove
//            systemLog.log(SEVERE, "Failed to load home scene and its fxmls", Arrays.toString(e.getStackTrace())); //Todo
        }

    }


    private void addTabsPatient() throws IOException {
        // create profile tab and add fxml into
        Tab profileViewTab = new Tab();
        profileViewTab.setOnSelectionChanged(event -> {
            try {
                profileViewTab.setContent(FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
            } catch (IOException e) {
                e.printStackTrace(); //todo: remove
            }
        });
        profileViewTab.setText("Profile");
        Pane pane = FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml"));
        profileViewTab.setContent(pane);
        horizontalTabPane.getTabs().add(profileViewTab);

        Tab updateProfileTab = new Tab();
        updateProfileTab.setText("Update");
        Pane updatePane = FXMLLoader.load(getClass().getResource("/scene/testUpdate.fxml"));
        updateProfileTab.setContent(updatePane);
        horizontalTabPane.getTabs().add(updateProfileTab);

        Tab patientDonationsTab = new Tab();
        patientDonationsTab.setText("Donations");
        Pane donationsPane = FXMLLoader.load(getClass().getResource("/scene/testDonationUpdate.fxml"));
        patientDonationsTab.setContent(donationsPane);
        horizontalTabPane.getTabs().add(patientDonationsTab);


        // create history tab and add fxml into
        Tab historyViewTab = new Tab();
        historyViewTab.setText("History");
        historyViewTab.setContent(FXMLLoader.<Pane>load(getClass().getResource("/scene/patientHistory.fxml")));
        horizontalTabPane.getTabs().add(historyViewTab);


        //TODO: remove to clinician pop up
        Tab medicationsTab = new Tab();
        medicationsTab.setText("Medication");
        Pane medicationPane = FXMLLoader.load(getClass().getResource("/scene/patientMedication.fxml"));
        medicationsTab.setContent(medicationPane);
        horizontalTabPane.getTabs().add(medicationsTab);

        Tab contactsTab = new Tab();
        contactsTab.setText("Contact Details");
        Pane contactsPane = FXMLLoader.load(getClass().getResource("/scene/testPatientContactUpdate.fxml"));
        contactsTab.setContent(contactsPane);
        horizontalTabPane.getTabs().add(contactsTab);


        //todo setUpMenuBar() call here

    }



    private void addTabsClinican() throws IOException {
        Tab clinicianProfileTab = new Tab();
        clinicianProfileTab.setText("Profile");
        Pane clinicianProfilePane = FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml"));
        clinicianProfileTab.setContent(clinicianProfilePane);
        horizontalTabPane.getTabs().add(clinicianProfileTab);

        Tab searchTab = new Tab();
        searchTab.setText("Search Patients");
        Pane searchPane = FXMLLoader.load(getClass().getResource("/scene/clinicianSearchPatients.fxml"));
        searchTab.setContent(searchPane);
        horizontalTabPane.getTabs().add(searchTab);
    }


    /**
     * Creates a native-looking MacOS menu bar for the application
     */
    private void setUpMenuBar(Stage stage) {

        // Create a new menu bar
        MenuBar bar = new MenuBar();

        /* Build the menu bar with new menus and menu items */
        Menu menu1 = new Menu("App");
        MenuItem menu1Item1 = new MenuItem("Log out");
        menu1Item1.setAccelerator(screenControl.getLogOut());
        menu1Item1.setOnAction(event -> {
            new UserControl().rmLoggedInUserCache();
            userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        });
        menu1.getItems().addAll(menu1Item1);

        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        menu2Item1.setAccelerator(screenControl.getSave());
        menu2Item1.setOnAction(event -> {
            Database.saveToDisk();
            userActions.log(INFO, "Successfully saved to disk", "Attempted to save to disk");
        });
        Menu subMenuImport = new Menu("Import"); // import submenu
        MenuItem menu2Item2 = new MenuItem("Import patients...");
        menu2Item2.setAccelerator(screenControl.getImportt());
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
        subMenuImport.getItems().addAll(menu2Item2, menu2Item3);
        menu2.getItems().addAll(menu2Item1, subMenuImport);

        Menu menu3 = new Menu("Edit");
        MenuItem menu3Item1 = new MenuItem("Undo");
        menu3Item1.setAccelerator(screenControl.getUndo());
        menu3Item1.setOnAction(event -> System.out.println("Undo clicked")); //Todo add functionality
        MenuItem menu3Item2 = new MenuItem("Redo");
        menu3Item2.setAccelerator(screenControl.getRedo());
        menu3Item2.setOnAction(event -> System.out.println("Redo clicked")); //Todo add functionality
        menu3.getItems()
                .addAll(menu3Item1, menu3Item2);

        bar.getMenus()
                .addAll(menu1, menu2, menu3);

        // Use the menu bar for primary stage
        if (screenControl.isMacOs()) {
            // Get the toolkit THIS IS MAC OS ONLY
            MenuToolkit tk = MenuToolkit.toolkit();
            // Add the default application menu
            bar.getMenus().add(tk.createDefaultApplicationMenu(screenControl.getAppName()));
            tk.setMenuBar(stage, bar);
        }
        else {// if windows
            menuBar.getMenus().clear();
            menuBar.getMenus().addAll(menu1, menu2, menu3);
        }


    }

}
