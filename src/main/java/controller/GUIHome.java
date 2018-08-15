package controller;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import service.AdministratorDataService;
import service.ClinicianDataService;
import service.PatientDataService;
import service.UserDataService;
import service.interfaces.IAdministratorDataService;
import utility.*;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static java.util.logging.Level.*;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class GUIHome extends TargetedController implements Observer, TouchscreenCapable {


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

    @FXML
    private Label paneTitle;

    private boolean loaded = false;

    private TouchPaneController homePaneTouchController;

    @FXML
    private ProgressBar importProgress;

    @FXML
    private Label importLbl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

    private IAdministratorDataService administratorDataService = new AdministratorDataService();


    private  enum TabName {
        PROFILE("Profile"), UPDATE("Update"), DONATIONS("Donations"), CONTACTDETAILS("Contact Details"),
        DISEASEHISTORY("View Disease History"), HISTORY("History"), PROCEDURES("Procedures"),
        TRANSPLANTWAITINGLIST("Transplant Waiting List"), SEARCHPATIENTS("Search Patients"),
        REQUESTEDDONATIONS("Requested Donations"), MEDICATIONS("Medications"), SEARCHPUSERS("Search Users"),
        USERREGISTER("User Register"), AVAILIBLEORGANS("Available Organs");

        private String value;

        TabName(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

    }

    private Stage homeStage;


    @FXML
    public void load() {
        if (!loaded) {  // stops listeners from being added twice
        StatusObservable statusObservable = StatusObservable.getInstance();
        Observer statusObserver = (o, arg) -> statusLbl.setText(arg.toString());
        statusObservable.addObserver(statusObserver);
        ImportObservable importObservable = ImportObservable.getInstance();
        Observer importObserver = (o, arg) -> {
            double progress = Double.valueOf(arg.toString());
            if (progress < 1.0) {
                importProgress.setProgress(progress);
                importProgress.setVisible(true);
                importLbl.setVisible(true);
            } else {
                importProgress.setVisible(false);
                importLbl.setVisible(false);
            }
        };
        importObservable.addObserver(importObserver);
        homePane.getProperties().put("focusArea", "true");
                if (screenControl.isTouch() ) {
            addPaneListener();
            } else {
                addStageListener();
            }
            loaded = true;
        }
    }


    /**
     * Detects the appropriate user and adds the tabs to the tab bar accordingly
     */
    private void addTabs() {
        try {
            // Patient viewing themself
            if (userControl.getLoggedInUser() instanceof Patient) {
                addTabsPatient();
                setUpColouredBar(userControl.getLoggedInUser());
            } else if (userControl.getLoggedInUser() instanceof Clinician) {
                // Clinician viewing a patient
                if (target instanceof Patient) {
                    addTabsForPatientClinician(); // if we are a clinician looking at a patient
                    setUpColouredBar(target);
                }
                // Clinician viewing themself
                else {
                    addTabsClinician();
                    setUpColouredBar(target);
                }
            } else if (userControl.getLoggedInUser() instanceof Administrator) {
                // admin viewing patient
                if (target instanceof Patient) {
                    addTabsForPatientClinician();
                    setUpColouredBar(target);
                }
                // admin viewing clinician
                else if (target instanceof Clinician) {
                    addTabsClinicianAdministrator();
                    setUpColouredBar(target);
                }
                // admin viewing admin
                else if (target instanceof Administrator && target != userControl.getLoggedInUser()) {
                    addTabsAdministratorAdministrator();
                    setUpColouredBar(target);
                } else {
                    addTabsAdministrator();
                    setUpColouredBar(userControl.getLoggedInUser());
                }
            }
            if(screenControl.isTouch()) {
                homePane.addEventHandler(Event.ANY, new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                    }
                });
                homePaneTouchController = new TouchPaneController(homePane);

                homePane.setOnTouchPressed(event -> {
                    homePane.toFront();
                });
                homePane.setOnZoom(this::zoomWindow);
                homePane.setOnRotate(this::rotateWindow);
                homePane.setOnScroll(this::scrollWindow);
            }
        } catch (IOException e) {
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
                                                setUpMenuBar(homeStage);
                                                screenControl.addTab(homeStage, horizontalTabPane);
                                                screenControl.getUndoableWrapper(homeStage).setChangingStates(true);
                                                addTabs();
                                                screenControl.getUndoableWrapper(homeStage).setChangingStates(false);
                                                setStageTitle();
                                            }
                                        });
                            } else {
                                homeStage = (Stage) newScene.getWindow();
                                // Methods to call after initialize
                                setUpMenuBar(homeStage);
                                screenControl.addTab(homeStage, horizontalTabPane);
                                screenControl.getUndoableWrapper(homeStage).setChangingStates(true);
                                addTabs();
                                screenControl.getUndoableWrapper(homeStage).setChangingStates(false);
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
                            setUpMenuBar(homeStage);
                            screenControl.addTab(homeStage, horizontalTabPane);
                            screenControl.getUndoableWrapper(homeStage).setChangingStates(true);
                            addTabs();
                            screenControl.getUndoableWrapper(homeStage).setChangingStates(false);
                            setStageTitle();
                        }
                    });
        } else {
            homeStage = (Stage) homePane.getScene()
                    .getWindow();
            // Methods to call after initialize
            setUpMenuBar(homeStage);
            screenControl.addTab(homeStage, horizontalTabPane);
            screenControl.getUndoableWrapper(homeStage).setChangingStates(true);
            addTabs();
            screenControl.getUndoableWrapper(homeStage).setChangingStates(false);
            setStageTitle();
        }
    }

    private void addPaneListener() {
        homePane.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                setUpMenuBar(homeStage);
                screenControl.addTab(homePane, horizontalTabPane);
                addTabs();
                setPaneTitle();
                homePane.parentProperty().removeListener(this);
            }
        });
    }

    /**
     * Sets the stage title of the stage this guiHome is on
     */
    private void setStageTitle() {
        homeStage.setTitle("Home");
        screenControl.getUndoableWrapper(homeStage).setGuiHome(this);
        // If clinician viewing patient
        if (userControl.getLoggedInUser() != target) {
            // viewing patient
            if (target instanceof Patient) {
                homeStage.setTitle("Patient " + ((Patient) target).getNhiNumber());
            }
            // viewing clinician
            else if (target instanceof Clinician) {
                homeStage.setTitle("Clinician " + ((Clinician) target).getStaffID());
            }
            // viewing admin
            else if (target instanceof Administrator) {
                homeStage.setTitle("Administrator " + ((Administrator) target).getUsername());
            }
        }
    }

    /**
     * Sets the title of the pane
     * Called when touch entry point is used
     */
    private void setPaneTitle() {
        paneTitle.setText("Home");
        // If clinician viewing patient
        if (userControl.getLoggedInUser() != target) {
            // viewing patient
            if (target instanceof Patient) {
                paneTitle.setText("Patient " + ((Patient) target).getNhiNumber());
            }
            // viewing clinician
            else if (target instanceof Clinician) {
                paneTitle.setText("Clinician " + ((Clinician) target).getStaffID());
            }
            // viewing admin
            else if (target instanceof Administrator) {
                paneTitle.setText("Administrator " + ((Administrator) target).getUsername());
            }
        }
    }


    /**
     * Sets to the coloured bar at top of GUI the user name
     *
     * @param user the currently logged in user, or observed patient
     */
    private void setUpColouredBar(User user) {
        //Sorry this is really ugly but its needed for the property change support to work correctly :(
        User updatedUser;
        if (user instanceof Patient) {
            updatedUser = new PatientDataService().getPatientByNhi(((Patient) user).getNhiNumber());
            new PatientDataService().save((Patient) updatedUser);
        } else if (user instanceof Clinician) {
            updatedUser = new ClinicianDataService().getClinician(((Clinician) user).getStaffID());
            new ClinicianDataService().save((Clinician) updatedUser);
        } else {
            updatedUser = administratorDataService.getAdministratorByUsername(((Administrator) user).getUsername());
            administratorDataService.save((Administrator) updatedUser);
        }

        updatedUser.addPropertyChangeListener(e -> userNameDisplay.setText(updatedUser.getNameConcatenated()));
        userNameDisplay.setText(updatedUser.getNameConcatenated());
    }

    /**
     * Creates and adds tab to the tab pane
     * @param title - title of the new tab
     * @param fxmlPath - path of the fxml to be loaded
     */
    private void createTab(TabName title, String fxmlPath) {
        Tab newTab = new Tab();
        newTab.setText(title.toString());
            newTab.selectedProperty().addListener(observable ->{
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                    newTab.setContent(fxmlLoader.load());
                    TargetedController targetedController = fxmlLoader.getController();
                    targetedController.setTarget(target);
                    if (screenControl.isTouch()) {
                        ((ScreenControlTouch) screenControl).setFonts();
                    }
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
        createTab(TabName.AVAILIBLEORGANS, "/scene/clinicianAvailableOrgans.fxml");
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
        createTab(TabName.AVAILIBLEORGANS, "/scene/clinicianAvailableOrgans.fxml");
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
     * Adds tabs for an administrator viewing a administrator
     * @throws IOException if fxml cannot be loaded
     */
    private void addTabsAdministratorAdministrator() throws IOException {
        createTab(TabName.PROFILE, "/scene/administratorProfile.fxml");
        createTab(TabName.UPDATE, "/scene/administratorProfileUpdate.fxml");
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
                new UserDataService().save();
                logOut();
            });
            alert.getDialogPane().lookupButton(ButtonType.NO).addEventFilter(ActionEvent.ACTION, event -> {
                systemLogger.log(FINE, "User trying to log out");
                logOut();
                new UserDataService().clear();
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
        screenControl.logOut();
        userControl.rmLoggedInUserCache();
        userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        new UserDataService().clear();
        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
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
        if(!screenControl.isTouch()) {
            menu1Item1.setAccelerator(screenControl.getLogOut());
        }
        menu1Item1.setOnAction(event -> {
            attemptLogOut();
        });
        menu1.getItems().addAll(menu1Item1);

        // FILE
        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        if(!screenControl.isTouch()) {
            menu2Item1.setAccelerator(screenControl.getSave());
        }
        menu2Item1.setOnAction(event -> {
            UserDataService userDataService = new UserDataService();
            userDataService.save();
            screenControl.setIsSaved(true);
            userActions.log(INFO, "Saved successfully", "Attempted to save");
        });

        if (userControl.getLoggedInUser() instanceof Administrator) {
            Menu subMenuImport = new Menu("Import"); // import submenu
            MenuItem menu2Item2 = new MenuItem("Import patients...");
            if(!screenControl.isTouch()) {
                menu2Item2.setAccelerator(screenControl.getImportt());
            }
            menu2Item2.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select CSV File");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (.csv)", "*.csv"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    administratorDataService.importRecords(file.getPath());
                    userActions.log(INFO, "Selected patient file for import", "Attempted to find a file for import");
                }
            });
            MenuItem menu2Item3 = new MenuItem("Import clinicians...");
            menu2Item3.setOnAction(event -> {
                File file = new FileChooser().showOpenDialog(stage);
                if (file != null) {
                  //  database.importFromDiskPatients(file.getAbsolutePath());
                    userActions.log(INFO, "Selected clinician file for import", "Attempted to find a file for import");
                }
            });
            subMenuImport.getItems().addAll(menu2Item2, menu2Item3);
            menu2.getItems().addAll(subMenuImport);
        }
        menu2.getItems().addAll(menu2Item1);
        MenuItem menu2item4 = new MenuItem("Close window");
        if(!screenControl.isTouch()) {
            menu2item4.setAccelerator(screenControl.getCloseWindow());
        }
        menu2item4.setOnAction(event -> {
            if(!(screenControl.closeWindow(homePane))) {
                setStatusLbl("Root window can not be closed. Please log out to exit.");
            }
        });
        menu2.getItems().addAll(menu2item4);

        Menu menu3 = new Menu("Edit");
        MenuItem menu3Item1 = new MenuItem("Undo");
        if(!screenControl.isTouch()) {
            menu3Item1.setAccelerator(screenControl.getUndo());
        }
        menu3Item1.setOnAction((event) -> {
            if (screenControl.isTouch()) {
                screenControl.getUndoableWrapper(homePane).undo();
            } else {
                screenControl.getUndoableWrapper(stage).undo();
            }
        });
        MenuItem menu3Item2 = new MenuItem("Redo");
        if(!screenControl.isTouch()) {
            menu3Item2.setAccelerator(screenControl.getRedo());
        }
        menu3Item2.setOnAction((event) -> {
            if (screenControl.isTouch()) {
                screenControl.getUndoableWrapper(homePane).redo();
            } else {
                screenControl.getUndoableWrapper(stage).redo();
            }
        });
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
                if (!screenControl.isTouch()) {
                    tk.setMenuBar(stage, bar);
                    systemLogger.log(FINER, "Set MacOS menu bar");
                }
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
        if(zoomEvent.getSource().equals(homePane)) {
            homePaneTouchController.zoomPane(zoomEvent);
            zoomEvent.consume();
        }
    }

    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        if(rotateEvent.getSource().equals(homePane)) {
            homePaneTouchController.rotatePane(rotateEvent);
            rotateEvent.consume();
        }
    }

    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        if(scrollEvent.getSource().equals(homePane)) {
            if (scrollEvent.isDirect()) {
                homePaneTouchController.scrollPane(scrollEvent);
            }
            scrollEvent.consume();
        }
    }

    /**
     * Removes the asterisk from the username display
     */
    void removeAsterisk() {
        userNameDisplay.setText(userNameDisplay.getText().replace("*", ""));
    }

}
