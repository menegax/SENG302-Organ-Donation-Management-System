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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.undoRedo.UndoableStage;

import java.io.File;
import java.io.IOException;

public class GUIHome {

    @FXML
    public BorderPane homePane;

    @FXML
    private TabPane horizontalTabPane;

    @FXML
    private MenuBar menuBar;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    @FXML
    public void initialize() {
        UserControl userControl = new UserControl();
        try {
            if (userControl.getLoggedInUser() instanceof Patient){
                addTabsPatient();
            } else if (userControl.getLoggedInUser() instanceof Clinician) {
                if (userControl.getTargetPatient() != null) {
                    addTabsForPatientClinician(); //if we are a clinician looking at a patient
                } else {
                    addTabsClinician();
                }
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


    /**
     *
     * @param title - title of the new tab
     * @param fxmlPath - path of the fxml to be loaded
     */
    private void createTab(String title, String fxmlPath) throws IOException {
        Tab newTab = new Tab();
        newTab.setText(title);
        newTab.setOnSelectionChanged(event -> {
            try {
                newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
            } catch (IOException e) {
                e.printStackTrace(); //todo: remove
            }
        });
        newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
        horizontalTabPane.getTabs().add(newTab);
    }

    /**
     *
     * @throws IOException
     */
    private void addTabsPatient() throws IOException {
        createTab("Profile", "/scene/patientProfile.fxml");
        createTab("Update", "/scene/testUpdate.fxml");
        createTab("Donations", "/scene/testDonationUpdate.fxml");
        createTab("History", "/scene/patientHistory.fxml");
        createTab("Contact Details", "/scene/testPatientContactUpdate.fxml");
        //todo setUpMenuBar() call here
    }


    /**
     *
     * @throws IOException
     */
    private void addTabsForPatientClinician() throws IOException{
        createTab("Profile", "/scene/patientProfile.fxml");
        createTab("Update", "/scene/testUpdate.fxml");
        createTab("Medications", "/scene/patientMedication.fxml");
        createTab("Donations", "/scene/testDonationUpdate.fxml");
        createTab("Contact Details", "/scene/testPatientContactUpdate.fxml");
    }


    /**
     *
     * @throws IOException
     */
    private void addTabsClinician() throws IOException {
        createTab("Profile", "/scene/clinicianProfile.fxml");
        createTab("Search Patients", "/scene/clinicianSearchPatients.fxml");
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
        menu3Item1.setOnAction(event -> ((UndoableStage) stage).undo()); //Todo undo doesn't work yet
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
            bar.getMenus().add(0, tk.createDefaultApplicationMenu(screenControl.getAppName())); // set leftmost MacOS system menu
            tk.setMenuBar(stage, bar);
        }
        else {// if windows
            menuBar.getMenus().clear();
            menuBar.getMenus().addAll(menu1, menu2, menu3);
        }


    }

}
