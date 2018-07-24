package controller;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.event.ActionEvent;
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
import utility.StatusObservable;
import utility.undoRedo.UndoableStage;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class GUIHome implements Observer {

    @FXML
    public BorderPane homePane;

    @FXML
    private TabPane horizontalTabPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label userNameDisplay;

    @FXML
    private Label statusLbl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    private Stage homeStage;

    /**
     * The user that the home controller is viewing. If it is a clinician viewing a patient it is the patient
     */
    private User homeTarget;


    @FXML
    public void initialize() {
        StatusObservable statusObservable = StatusObservable.getInstance();
        statusObservable.addObserver(this);
        try {
            // Patient viewing themself
            if (userControl.getLoggedInUser() instanceof Patient) {
                homeTarget = userControl.getLoggedInUser();
                addTabsPatient();
                setUpColouredBar(userControl.getLoggedInUser());
            }
            else if (userControl.getLoggedInUser() instanceof Clinician) {
                // Clinician viewing a patient
                if (userControl.getTargetUser() != null) {
                    homeTarget = userControl.getTargetUser();
                    addTabsForPatientClinician(); // if we are a clinician looking at a patient
                    setUpColouredBar(userControl.getTargetUser());
                }
                // Clinician viewing themself
                else {
                    homeTarget = userControl.getLoggedInUser();
                    addTabsClinician();
                    setUpColouredBar(userControl.getLoggedInUser());
                }
            }
            else if (userControl.getLoggedInUser() instanceof Administrator) {
                // admin viewing patient
                if (userControl.getTargetUser() instanceof Patient) {
                    homeTarget = userControl.getTargetUser();
                    addTabsForPatientClinician();
                    setUpColouredBar(userControl.getTargetUser());
                }
                // admin viewing clinician
                else if (userControl.getTargetUser() instanceof Clinician) {
                    homeTarget = userControl.getTargetUser();
                    addTabsClinicianAdministrator();
                    setUpColouredBar(userControl.getTargetUser());
                }
                // admin viewing admin
                else if (userControl.getTargetUser() instanceof Administrator) {
                    homeTarget = userControl.getTargetUser();
                    addTabsAdministrator();
                    setUpColouredBar(userControl.getTargetUser());
                }
                else {
                    addTabsAdministrator();
                    setUpColouredBar(userControl.getLoggedInUser());
                }
            }
            addStageListener();
            horizontalTabPane.sceneProperty()
                    .addListener((observable, oldScene, newScene) -> newScene.windowProperty()
                            .addListener((observable1, oldStage, newStage) -> setUpMenuBar((Stage) newStage)));
        }
        catch (IOException e) {
            new Alert(ERROR, "Unable to load home").show();
            systemLogger.log(SEVERE, "Failed to load home scene and its fxmls " + e.getMessage());
        }
    }


    private void addStageListener() {

        // The following code waits for the stage to be loaded
        if (homePane.getScene() == null) {
            homePane.sceneProperty()
                    .addListener((observable, oldScene, newScene) -> {
                        if (newScene != null) {
                            if (newScene.getWindow() == null) {
                                newScene.windowProperty()
                                        .addListener((observable2, oldStage, newStage) -> {
                                            if (newStage != null) {
                                                homeStage = (Stage) newStage;
                                                // Methods to call after initialize
                                                setStageTitle();
                                            }
                                        });
                            }
                            else {
                                homeStage = (Stage) newScene.getWindow();
                                // Methods to call after initialize
                                setStageTitle();
                            }
                        }
                    });
        }
        else if (homePane.getScene()
                .getWindow() == null) {
            homePane.getScene()
                    .windowProperty()
                    .addListener((observable2, oldStage, newStage) -> {
                        if (newStage != null) {
                            homeStage = (Stage) newStage;
                            // Methods to call after initialize
                            setStageTitle();
                        }
                    });
        }
        else {
            homeStage = (Stage) homePane.getScene()
                    .getWindow();
            // Methods to call after initialize
            setStageTitle();
        }
    }

    /**
     * Sets the stage title of the stage this guiHome is on
     */
    private void setStageTitle() {
        homeStage.setTitle("Home");
        ((UndoableStage) homeStage).setGuiHome(this);
        // If clinician viewing patient
        if (userControl.getTargetUser() != null) {
            // viewing patient
            if (userControl.getTargetUser() instanceof Patient) {
                homeStage.setTitle("Patient " + ((Patient) homeTarget).getNhiNumber());
            }
            // viewing clinician
            else if (userControl.getTargetUser() instanceof Clinician) {
                homeStage.setTitle("Clinician " + ((Clinician) homeTarget).getStaffID());
            }
            // viewing admin
            else if (userControl.getTargetUser() instanceof Administrator) {
                homeStage.setTitle("Administrator " + ((Administrator) homeTarget).getUsername());
            }
        }
    }


    /**
     * Sets to the coloured bar at top of GUI the user name
     *
     * @param user the currently logged in user, or observed patient
     */
    private void setUpColouredBar(User user) {
        user.addPropertyChangeListener(e -> userNameDisplay.setText(user.getNameConcatenated()));
        userNameDisplay.setText(user.getNameConcatenated());
    }


    /**
     * Creates and adds tab to the tab pane
     *
     * @param title    - title of the new tab
     * @param fxmlPath - path of the fxml to be loaded
     */
    private void createTab(String title, String fxmlPath) throws IOException {
        Tab newTab = new Tab();
        newTab.setText(title);
        newTab.setOnSelectionChanged(event -> {
            try {
                newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
            }
            catch (IOException e) {
                systemLogger.log(SEVERE, "Failed to create tab", e);
            }
        });
        newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
        horizontalTabPane.getTabs()
                .add(newTab);
    }


    /**
     * Adds tabs to the home tab pane for a patient logged in
     *
     * @exception IOException - if fxml cannot be located
     */
    private void addTabsPatient() throws IOException {
        createTab("Profile", "/scene/patientProfile.fxml");
        createTab("Update", "/scene/patientUpdateProfile.fxml");
        createTab("Donations", "/scene/patientUpdateDonations.fxml");
        createTab("Contact Details", "/scene/patientUpdateContacts.fxml");
        createTab("View Disease History", "/scene/clinicianDiagnosis.fxml");
        createTab("History", "/scene/patientHistory.fxml");
        createTab("Procedures", "/scene/patientProcedures.fxml");
    }


    /**
     * Adds tabs for a clinician viewing a patient
     *
     * @exception IOException- if fxml cannot be located
     */
    private void addTabsForPatientClinician() throws IOException {
        createTab("Profile", "/scene/patientProfile.fxml");
        createTab("Update", "/scene/patientUpdateProfile.fxml");
        createTab("Medications", "/scene/patientMedications.fxml");
        createTab("Donations", "/scene/patientUpdateDonations.fxml");
        createTab("Contact Details", "/scene/patientUpdateContacts.fxml");
        createTab("Requested Donations", "/scene/patientUpdateRequirements.fxml");
        createTab("View Diseases", "/scene/clinicianDiagnosis.fxml");
        createTab("Procedures", "/scene/patientProcedures.fxml");
    }


    /**
     * Adds tabs for a logged in clinician
     *
     * @exception IOException- if fxml cannot be located
     */
    private void addTabsClinician() throws IOException {
        createTab("Profile", "/scene/clinicianProfile.fxml");
        createTab("Update", "/scene/clinicianProfileUpdate.fxml");
        createTab("Search Patients", "/scene/clinicianSearchPatients.fxml");
        createTab("Transplant Waiting List", "/scene/clinicianWaitingList.fxml");
        createTab("History", "/scene/clinicianHistory.fxml");
    }


    /**
     * Adds tabs for a logged in administrator
     *
     * @exception IOException- if fxml cannot be located
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
     *
     * @exception IOException- if fxml cannot be located
     */
    private void addTabsClinicianAdministrator() throws IOException {
        createTab("Profile", "/scene/clinicianProfile.fxml");
        createTab("Update", "/scene/clinicianProfileUpdate.fxml");
    }

    /**
     * Called when logout button is pressed by user
     * Checks for unsaved changes before logging out
     */
    private void attemptLogOut() {
        if (!screenControl.getIsSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save all unsaved changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.setTitle("Unsaved changes");
            alert.getDialogPane().lookupButton(ButtonType.YES).addEventFilter(ActionEvent.ACTION, event -> {
                systemLogger.log(FINE, "User trying to log out");
                Database.saveToDisk();
                logOut();
            });
            alert.getDialogPane().lookupButton(ButtonType.NO).addEventFilter(ActionEvent.ACTION, event -> {
                systemLogger.log(FINE, "User trying to log out");
                logOut();
            });
            alert.getDialogPane().lookupButton(ButtonType.CANCEL).addEventFilter(ActionEvent.ACTION, event -> {
                System.out.println("Have a nice day"); //tod rm
            });
            alert.showAndWait();
        } else {
            logOut();
        }
    }

    /**
     * Logs the user out of the application
     */
    private void logOut() {
        screenControl.closeAllUserStages(new UserControl().getLoggedInUser());
        new UserControl().rmLoggedInUserCache();
        screenControl.setUpNewLogin(); // ONLY FOR SINGLE USER SUPPORT. REMOVE WHEN MULTI USER SUPPORT
        userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");

        // Resets all local changes
        Database.resetDatabase();
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        Database.importFromDiskWaitlist("./waitlist.json");
        Database.importFromDiskAdministrators("./administrator.json");
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
            attemptLogOut();
        });
        menu1.getItems()
                .addAll(menu1Item1);

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
            subMenuImport.getItems()
                    .addAll(menu2Item2, menu2Item3);
            menu2.getItems()
                    .addAll(subMenuImport);
        }
        menu2.getItems()
                .addAll(menu2Item1);

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


    /**
     * Sets the text of the status label
     *
     * @param text - The new text
     */
    private void setStatusLbl(String text) {
        statusLbl.setText(text);
    }


    /**
     * Called when the status text of the StatusObservable is set
     *
     * @param o   The StatusObservable instance
     * @param arg The new status text
     */
    @Override
    public void update(Observable o, Object arg) {
        setStatusLbl(arg.toString());
    }

    /**
     * Adds an asterisk from the username display
     */
    void addAsterisk() {
        if (!userNameDisplay.getText().contains("*")) {
            userNameDisplay.setText(userNameDisplay.getText() + "*");
        }
    }

    /**
     * Removes the asterisk from the username display
     */
    void removeAsterisk() {
        userNameDisplay.setText(userNameDisplay.getText().replace("*", ""));
    }
}
