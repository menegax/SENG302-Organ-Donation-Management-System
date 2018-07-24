package controller;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.Database;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class GUIHome {

    @FXML
    public BorderPane homePane;

    @FXML
    private TabPane horizontalTabPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label userNameDisplay;

    @FXML
    private Label userTypeDisplay;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    
    private UserControl userControl = new UserControl();

    private  enum TabName {
        PROFILE("Profile"), UPDATE("Update"), DONATIONS("Donations"), CONTACTDETAILS("Contact Details"),
        DISEASEHISTORY("View Disease History"), HISTORY("History"), PROCEDURES("Procedures"),
        TRANSPLANTWAITINGLIST("Transplant Waiting List"), SEARCHPATIENTS("Search Patients"),
        REQUESTEDDONATIONS("Requested Donations"), MEDICATIONS("Medications");

        private String value;

        TabName(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

    }

    @FXML
    public void initialize() {
        try {
            if (userControl.getLoggedInUser() instanceof Patient){
                addTabsPatient();
                setUpColouredBar(userControl.getLoggedInUser(), "Patient");
            } else if (userControl.getLoggedInUser() instanceof Clinician) {
                if (userControl.getTargetUser() != null) {
                    addTabsForPatientClinician(); // if we are a clinician looking at a patient
                    setUpColouredBar(userControl.getTargetUser(), "Patient");
                } else {
                    addTabsClinician();
                    setUpColouredBar(userControl.getLoggedInUser(), "Clinician");
                }
            } else if (userControl.getLoggedInUser() instanceof Administrator) {
                if (userControl.getTargetUser() instanceof Patient) {
                    addTabsForPatientClinician();
                    setUpColouredBar(userControl.getTargetUser(), "Patient");
                } else if (userControl.getTargetUser() instanceof Clinician) {
                    addTabsClinicianAdministrator();
                    setUpColouredBar(userControl.getTargetUser(), "Clinician");
                } else if (userControl.getTargetUser() instanceof Administrator) {
                    addTabsAdministrator();
                    setUpColouredBar(userControl.getTargetUser(), "Administrator");
                } else {
                	addTabsAdministrator();
                	setUpColouredBar(userControl.getLoggedInUser(), "Administrator");
                }
            }
            horizontalTabPane.sceneProperty().addListener((observable, oldScene, newScene) -> newScene.windowProperty().addListener((observable1, oldStage, newStage) -> setUpMenuBar((Stage) newStage)));
        } catch (IOException e) {
            e.printStackTrace();//todo;
            new Alert(ERROR, "Unable to load home").show();
            systemLogger.log(SEVERE, "Failed to load home scene and its fxmls " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets to the coloured bar at top of GUI the user name and type
     * @param user the currently logged in user, or observed patient
     */
    private void setUpColouredBar(User user, String userType) {
        user.addPropertyChangeListener(e -> {
            userNameDisplay.setText(user.getNameConcatenated());
            userTypeDisplay.setText(userType);
        });
        userNameDisplay.setText(user.getNameConcatenated());
        userTypeDisplay.setText(userType);
    }

    /**
     * Creates and adds tab to the tab pane
     * @param title - title of the new tab
     * @param fxmlPath - path of the fxml to be loaded
     */
    private void createTab(TabName title, String fxmlPath) throws IOException {
        Tab newTab = new Tab();
        newTab.setText(title.toString());
        if (!title.equals(TabName.SEARCHPATIENTS)) {
            newTab.setOnSelectionChanged(event -> {
                try {
                    newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
                } catch (IOException e) {
                    systemLogger.log(SEVERE, "Failed to create tab", e);
                }
            });
        }
        newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
        horizontalTabPane.getTabs().add(newTab);
    }

    /**
     * Adds tabs to the home tab pane for a patient logged in
     * @throws IOException - if fxml cannot be located
     */
    private void addTabsPatient() throws IOException {
        createTab(TabName.PROFILE, "/scene/patientProfile.fxml");
        createTab(TabName.UPDATE, "/scene/patientUpdateProfile.fxml");
        createTab(TabName.DONATIONS, "/scene/patientUpdateDonations.fxml");
        createTab(TabName.CONTACTDETAILS, "/scene/patientUpdateContacts.fxml");
        createTab(TabName.DISEASEHISTORY, "/scene/clinicianDiagnosis.fxml");
        createTab(TabName.HISTORY, "/scene/patientHistory.fxml");
        createTab(TabName.PROCEDURES, "/scene/patientProcedures.fxml");
    }


    /**
     * Adds tabs for a clinician viewing a patient
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsForPatientClinician() throws IOException{
        createTab(TabName.PROFILE, "/scene/patientProfile.fxml");
        createTab(TabName.UPDATE, "/scene/patientUpdateProfile.fxml");
        createTab(TabName.REQUESTEDDONATIONS, "/scene/patientUpdateRequirements.fxml");
        createTab(TabName.MEDICATIONS, "/scene/patientMedications.fxml");
        createTab(TabName.DONATIONS, "/scene/patientUpdateDonations.fxml");
        createTab(TabName.CONTACTDETAILS, "/scene/patientUpdateContacts.fxml");
        createTab(TabName.DISEASEHISTORY, "/scene/clinicianDiagnosis.fxml");
        createTab(TabName.PROCEDURES, "/scene/patientProcedures.fxml");
    }


    /**
     * Adds tabs for a logged in clinician
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsClinician() throws IOException {
        createTab(TabName.PROFILE, "/scene/clinicianProfile.fxml");
        createTab(TabName.UPDATE, "/scene/clinicianProfileUpdate.fxml");
        createTab(TabName.SEARCHPATIENTS, "/scene/clinicianSearchPatients.fxml");
        createTab(TabName.TRANSPLANTWAITINGLIST, "/scene/clinicianWaitingList.fxml");
        createTab(TabName.HISTORY, "/scene/clinicianHistory.fxml");
    }

    /**
     * Adds tabs for a logged in administrator
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsAdministrator() throws IOException {
        createTab("Profile", "/scene/administratorProfile.fxml");
        createTab("Update", "/scene/administratorProfileUpdate.fxml");
        createTab("Register User", "/scene/userRegister.fxml");
        createTab("Search Users", "/scene/administratorSearchUsers.fxml");
        createTab("Transplant Waiting List", "/scene/clinicianWaitingList.fxml");
        createTab("History", "/scene/adminHistory.fxml");
    }

    /**
     * Adds tabs for an administrator viewing a clinician
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsClinicianAdministrator() throws IOException {
        createTab("Profile", "/scene/clinicianProfile.fxml");
        createTab("Update", "/scene/clinicianProfileUpdate.fxml");
    }


    private void logOut() {
        systemLogger.log(FINE, "User trying to log out");
        ScreenControl.closeAllUserStages(new UserControl().getLoggedInUser());
        new UserControl().rmLoggedInUserCache();

        screenControl.setUpNewLogin(); // ONLY FOR SINGLE USER SUPPORT. REMOVE WHEN MULTI USER SUPPORT
    }

    /**
     * Creates a native-looking MacOS menu bar for the application
     */
    private void setUpMenuBar(Stage stage) {

        // Create a new menu bar
        MenuBar bar = new MenuBar();

        /* Build the menu bar with new menus and menu items */

        // APP
        Menu menu1 = new Menu("App");
        MenuItem menu1Item1 = new MenuItem("Log out");
        menu1Item1.setAccelerator(screenControl.getLogOut());
        menu1Item1.setOnAction(event -> {
            logOut();
            userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        });
        menu1.getItems().addAll(menu1Item1);

        // FILE
        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        menu2Item1.setAccelerator(screenControl.getSave());
        menu2Item1.setOnAction(event -> {
            Database.saveToDisk();
            userActions.log(INFO, "Successfully saved to disk", "Attempted to save to disk");
        });
        if (userControl.getLoggedInUser() instanceof Administrator) {
            Menu subMenuImport = new Menu("Import"); // import submenu
            MenuItem menu2Item2 = new MenuItem("Import patients...");
            menu2Item2.setAccelerator(screenControl.getImportt());
            menu2Item2.setOnAction(event -> {
                File file = new FileChooser().showOpenDialog(stage);
                if (file != null) {
                    Database.importFromDiskPatients(file.getAbsolutePath());
                    userActions.log(INFO, "Selected patient file for import", "Attempted to find a file for import");
                }
            });
            MenuItem menu2Item3 = new MenuItem("Import clinicians...");
            menu2Item3.setOnAction(event -> {
                File file = new FileChooser().showOpenDialog(stage);
                if (file != null) {
                    Database.importFromDiskPatients(file.getAbsolutePath());
                    userActions.log(INFO, "Selected clinician file for import", "Attempted to find a file for import");
                }
            });
            subMenuImport.getItems().addAll(menu2Item2, menu2Item3);
            menu2.getItems().addAll(subMenuImport);
        }
        menu2.getItems().addAll(menu2Item1);

        // EDIT
//        Menu menu3 = new Menu("Edit");
//        MenuItem menu3Item1 = new MenuItem("Undo");
//        menu3Item1.setAccelerator(screenControl.getUndo());
//        menu3Item1.setOnAction(event -> ((UndoableStage) stage).undo());
//        MenuItem menu3Item2 = new MenuItem("Redo");
//        menu3Item2.setAccelerator(screenControl.getRedo());
//        menu3Item2.setOnAction(event -> System.out.println("Redo clicked"));
//        menu3.getItems()
//                .addAll(menu3Item1, menu3Item2);

        bar.getMenus()
                .addAll(menu1, menu2);

        boolean headless = System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless")
                .equals("true");
        // Use the menu bar for primary stage
        if (!headless) { // make sure it isn't testing
            if (screenControl.isMacOs()) {
                // Get the toolkit THIS IS MAC OS ONLY
                MenuToolkit tk = MenuToolkit.toolkit();

                menuBar.getMenus()
                        .clear();

                // Add the default application menu
                bar.getMenus()
                        .add(0, tk.createDefaultApplicationMenu(screenControl.getAppName())); // set leftmost MacOS system menu
                tk.setMenuBar(stage, bar);
                systemLogger.log(FINER, "Set MacOS menu bar");
            }
            else {// if windows
                menuBar.getMenus()
                        .clear();
                menuBar.getMenus()
                        .addAll(menu1, menu2);
                systemLogger.log(FINER, "Set non-MacOS menu bar");
            }
        }


    }

}
