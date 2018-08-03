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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.Database;
import utility.Searcher;
import utility.StatusObservable;
import utility.TouchPaneController;
import utility.TouchscreenCapable;
import utility.undoRedo.UndoableStage;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class GUIHome implements Observer, TouchscreenCapable {

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

    private TouchPaneController homeTouchPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    private  enum TabName {
        PROFILE("Profile"), UPDATE("Update"), DONATIONS("Donations"), CONTACTDETAILS("Contact Details"),
        DISEASEHISTORY("View Disease History"), HISTORY("History"), PROCEDURES("Procedures"),
        TRANSPLANTWAITINGLIST("Transplant Waiting List"), SEARCHPATIENTS("Search Patients"),
        REQUESTEDDONATIONS("Requested Donations"), MEDICATIONS("Medications"), SEARCHPUSERS("Search Users"),
        USERREGISTER("User Register");

        private String value;

        TabName(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

    }

    private Stage homeStage;

    /**
     * The user that the home controller is viewing. If it is a clinician viewing a patient it is the patient
     */
    private User homeTarget;


    @FXML
    public void initialize() {
        StatusObservable statusObservable = StatusObservable.getInstance();
        statusObservable.addObserver(this);
        homePane.getProperties().put("focusArea", "true");
        horizontalTabPane.sceneProperty().addListener((observable, oldScene, newScene) -> newScene.windowProperty()
                .addListener((observable1, oldStage, newStage) -> {
            setUpMenuBar((UndoableStage) newStage);
            addTabs((UndoableStage) newStage);
        }));
    }

    /**
     * Detects the appropriate user and adds the tabs to the tab bar accordingly
     * @param stage the stage the horizontal tab is on
     */
    private void addTabs(UndoableStage stage) {
        UserControl userControl = new UserControl();
//        stage.setChangingStates(true);
        try {
            // Patient viewing themself
            if (userControl.getLoggedInUser() instanceof Patient) {
                homeTarget = userControl.getLoggedInUser();
                addTabsPatient();
                setUpColouredBar(userControl.getLoggedInUser());
            } else if (userControl.getLoggedInUser() instanceof Clinician) {
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
            } else if (userControl.getLoggedInUser() instanceof Administrator) {
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
                } else {
                    addTabsAdministrator();
                    setUpColouredBar(userControl.getLoggedInUser());
                }
            }
            homeTouchPane = new TouchPaneController(homePane);
            horizontalTabPane.sceneProperty().addListener((observable, oldScene, newScene) -> newScene.windowProperty().addListener((observable1, oldStage, newStage) -> setUpMenuBar((Stage) newStage)));
            homePane.setOnZoom(this::zoomWindow);
            homePane.setOnRotate(this::rotateWindow);
            homePane.setOnScroll(this::scrollWindow);
            addStageListener();
            horizontalTabPane.sceneProperty()
                    .addListener((observable, oldScene, newScene) -> newScene.windowProperty()
                            .addListener((observable1, oldStage, newStage) -> setUpMenuBar((Stage) newStage)));
        } catch (IOException e) {
            new Alert(ERROR, "Unable to load home").show();
            systemLogger.log(SEVERE, "Failed to load home scene and its fxmls " + e.getMessage());
        }
        stage.setChangingStates(false);
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
                            } else {
                                homeStage = (Stage) newScene.getWindow();
                                // Methods to call after initialize
                                setStageTitle();
                            }
                        }
                    });
        } else if (homePane.getScene()
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
        } else {
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
     * @param title - title of the new tab
     * @param fxmlPath - path of the fxml to be loaded
     */
    private void createTab(TabName title, String fxmlPath) throws IOException {
        Tab newTab = new Tab();
        newTab.setText(title.toString());
            newTab.setOnSelectionChanged(event -> {
                try {
                    newTab.setContent(FXMLLoader.load(getClass().getResource(fxmlPath)));
                } catch (IOException e) {
                    systemLogger.log(SEVERE, "Failed to create tab", e);
                }
            });
        horizontalTabPane.getTabs().add(newTab);
    }

    /**
     * Adds tabs to the home tab pane for a patient logged in
     *
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
     *
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsForPatientClinician() throws IOException{
        createTab(TabName.PROFILE, "/scene/patientProfile.fxml");
        createTab(TabName.UPDATE, "/scene/patientUpdateProfile.fxml");
        createTab(TabName.MEDICATIONS, "/scene/patientMedications.fxml");
        createTab(TabName.DONATIONS, "/scene/patientUpdateDonations.fxml");
        createTab(TabName.CONTACTDETAILS, "/scene/patientUpdateContacts.fxml");
        createTab(TabName.REQUESTEDDONATIONS, "/scene/patientUpdateRequirements.fxml");
        createTab(TabName.DISEASEHISTORY, "/scene/clinicianDiagnosis.fxml");
        createTab(TabName.PROCEDURES, "/scene/patientProcedures.fxml");
    }


    /**
     * Adds tabs for a logged in clinician
     *
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
     *
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsAdministrator() throws IOException {
        createTab(TabName.PROFILE, "/scene/administratorProfile.fxml");
        createTab(TabName.UPDATE, "/scene/administratorProfileUpdate.fxml");
        createTab(TabName.USERREGISTER, "/scene/administratorUserRegister.fxml");
        createTab(TabName.SEARCHPUSERS, "/scene/administratorSearchUsers.fxml");
        createTab(TabName.TRANSPLANTWAITINGLIST, "/scene/clinicianWaitingList.fxml");
        createTab(TabName.HISTORY, "/scene/adminHistory.fxml");
    }

    /**
     * Adds tabs for an administrator viewing a clinician
     *
     * @throws IOException- if fxml cannot be located
     */
    private void addTabsClinicianAdministrator() throws IOException {
        createTab(TabName.PROFILE, "/scene/clinicianProfile.fxml");
        createTab(TabName.UPDATE, "/scene/clinicianProfileUpdate.fxml");
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
        screenControl.setIsSaved(true);
        userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");

        // Resets all local changes
        Database.resetDatabase();
        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        Database.importFromDiskWaitlist("./waitlist.json");
        Database.importFromDiskAdministrators("./administrator.json");

        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
    }

    /**
     * Creates a native-looking MacOS menu bar for the application
     */
    private void setUpMenuBar(Stage stage) {
        screenControl.addStageTab(stage, horizontalTabPane);

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

        Menu menu3 = new Menu("Edit");
        MenuItem menu3Item1 = new MenuItem("Undo");
        menu3Item1.setAccelerator(screenControl.getUndo());
        menu3Item1.setOnAction(event -> ((UndoableStage) stage).undo());
        MenuItem menu3Item2 = new MenuItem("Redo");
        menu3Item2.setAccelerator(screenControl.getRedo());
        menu3Item2.setOnAction(event -> ((UndoableStage) stage).redo());
        menu3.getItems().addAll(menu3Item1, menu3Item2);

        //WINDOW
        Menu menu4 = new Menu("Window");
        MenuItem menu4Item1 = new MenuItem("Open Keyboard");
        menu4Item1.setOnAction(event -> openKeyboard());
        menu4.getItems().addAll(menu4Item1);


        bar.getMenus().addAll(menu1, menu2, menu3, menu4);

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
            } else {// if windows
                menuBar.getMenus()
                        .clear();
                menuBar.getMenus()
                        .addAll(menu1, menu2, menu3, menu4);
                systemLogger.log(FINER, "Set non-MacOS menu bar");
            }
        }

    }

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

    @Override
    public void zoomWindow(ZoomEvent zoomEvent) {
        homeTouchPane.zoomPane(zoomEvent);
        zoomEvent.consume();
    }

    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        homeTouchPane.rotatePane(rotateEvent);
        rotateEvent.consume();
    }

    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        if (scrollEvent.isDirect()) {
            homeTouchPane.scrollPane(scrollEvent);
        }
        scrollEvent.consume();
    }

    /**
     * Removes the asterisk from the username display
     */
    void removeAsterisk() {
        userNameDisplay.setText(userNameDisplay.getText().replace("*", ""));
    }

}
