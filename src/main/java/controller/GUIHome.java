package controller;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import utility.CachedThreadPool;
import utility.ImportObservable;
import utility.MultiTouchHandler;
import utility.Searcher;
import utility.StatusObservable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

public class GUIHome extends TargetedController implements Observer, IWindowObserver {


    @FXML
    public BorderPane homePane;

    @FXML
    public BorderPane topMenu;

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

    @FXML
    private ProgressBar importProgress;

    @FXML
    private Label importLbl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

    private IAdministratorDataService administratorDataService = new AdministratorDataService();

    private Stage homeStage;
    private MultiTouchHandler touchHandler;

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


    @FXML
    public void loadController() {
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
            if (screenControl.isTouch()) {
            	((ScreenControlTouch) screenControl).setCSS();
            }
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
                touchHandler = new MultiTouchHandler();
                touchHandler.initialiseHandler(homePane);
            }
        } catch (IOException e) {
            new Alert(ERROR, "Unable to loadController home").show();
            systemLogger.log(SEVERE, "Failed to loadController home scene and its fxmls " + e.getMessage());
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
            homeStage = (Stage) homePane.getScene().getWindow();
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
                    CachedThreadPool.getCachedThreadPool().getThreadService().shutdown(); //hot fix for now
                    targetedController.setTarget(target);
                    if (screenControl.isTouch()) {
                        ((ScreenControlTouch) screenControl).setCSS();
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
            if(screenControl.isTouch()) {
                screenControl.setMapOpen(false);
            }
            logOut();
        }
    }

    /**
     * Called when the map shown on login is closed
     */
    public void windowClosed() {
        screenControl.setMapOpen(false);
    }

    /**
     * Logs the user out of the application
     */
    private void logOut() {
        screenControl.logOut();
        userControl.rmLoggedInUserCache();
        userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        new UserDataService().clear();
        Searcher.getSearcher().createFullIndex(); // index globalPatients for search, needs to be after importing or adding any globalPatients
    }

    /**
     * Creates a native-looking MacOS menu bar for the application
     */
    private void setUpMenuBar(Stage stage) {
        // Create a new menu bar
        MenuBar bar = new MenuBar();

        Menu menu4 = null;

        /* Build the menu bar with new menus and menu items */

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
            MenuItem menu2Item2 = new MenuItem("Import globalPatients...");
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

        if(!screenControl.isTouch()) {
        	MenuItem menu2item4 = new MenuItem("Close window");
            menu2item4.setAccelerator(screenControl.getCloseWindow());
            menu2item4.setOnAction(event -> {
                if(!(screenControl.closeWindow(homePane))) {
                    setStatusLbl("Root window can not be closed. Please log out to exit.");
                }
            });

            menu2.getItems().addAll(menu2item4);
        }
        MenuItem menu2item4 = new MenuItem("Refresh");
        menu2item4.setOnAction(event -> refresh());
        if(!screenControl.isTouch()) {
        	menu2item4.setAccelerator(screenControl.getRefresh());
        }
        menu2.getItems().addAll(menu2item4);
        MenuItem logOut = new MenuItem("Log out");
        if(!screenControl.isTouch()) {
            logOut.setAccelerator(screenControl.getLogOut());
        }
        logOut.setOnAction(event -> attemptLogOut());
        menu2.getItems().addAll(logOut);


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

        bar.getMenus().addAll(menu2, menu3);

        //WINDOW
        menu4 = new Menu("Window");
        if (isUserClinicianOrAdmin()) {
            MenuItem menu4Item1 = new MenuItem("Open Map");
            menu4Item1.setOnAction(event -> {
                screenControl.setIsCustomSetMap(false);
                openMap();
            });
            menu4.getItems().addAll(menu4Item1);
            if(screenControl.isTouch()) {
                MenuItem menu4itemCentre = new MenuItem("Re-center Panes");
                menu4itemCentre.setOnAction(event -> screenControl.centerPanes());
                menu4.getItems().addAll(menu4itemCentre);
            }
        }
        bar.getMenus().addAll(menu4);

        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> {
            if(!(screenControl.closeWindow(homePane))) {
                setStatusLbl("Root window can not be closed. Please log out to exit.");
            }
        });
        closeButton.setId("EXIT");

        boolean headless = System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless").equals("true");
        // Use the menu bar for primary stage
        if (!headless) { // make sure it isn't testing
            if (screenControl.isMacOs()) {
                // Get the toolkit THIS IS MAC OS ONLY
                MenuToolkit tk = MenuToolkit.toolkit();

                menuBar.getMenus().clear();

                // Add the default application menu
                bar.getMenus()
                        .add(0, tk.createDefaultApplicationMenu(screenControl.getAppName())); // set leftmost MacOS system menu
                if (!screenControl.isTouch()) {
                    tk.setMenuBar(stage, bar);
                    systemLogger.log(FINER, "Set MacOS menu bar");
                }
            } else {// if windows
                menuBar.getMenus().clear();
                menuBar.getMenus().addAll(menu2, menu3, menu4);
                if (screenControl.isTouch()) {
                    topMenu.setRight(closeButton);
                }
                systemLogger.log(FINER, "Set non-MacOS menu bar");
                setMenuStyles();

            }
        }
    }

    /**
     * Increases the size of the MenuBar to match the exit button
     */
    private void setMenuStyles() {
        menuBar.setPrefHeight(35);
        for (Menu menu : menuBar.getMenus()) {
            menu.setStyle("-fx-font-size: 15px;");
            for (MenuItem menuItem : menu.getItems()) {
                menuItem.setStyle("-fx-font-size: 15px;");
            }
        }
    }

    private boolean isUserClinicianOrAdmin() {
        if (UserControl.getUserControl().getLoggedInUser() instanceof Clinician || UserControl.getUserControl().getLoggedInUser() instanceof Administrator)
        {
            return true;
        }
        return false;
    }

    /**
     * Opens new map instance if a map is not visible
     */
    void openMap() {
        if (!screenControl.getMapOpen()) {
            GUIMap controller = (GUIMap) screenControl.show("/scene/map.fxml", true, this, userControl.getLoggedInUser(), null);
            screenControl.setMapOpen(true);
            controller.setPatients(new ArrayList<>());
        }
        if(screenControl.isTouch()) {
            homePane.toFront();
        }
    }

    /**
     * Refreshes the current tab shown by switching to the first tab then switching back
     * to the current tab. If the current tab is the first tab then it switches to the 
     * second.
     */
    private void refresh() {
        int selectedIndex = horizontalTabPane.getSelectionModel().getSelectedIndex();
        if (horizontalTabPane.getSelectionModel().getSelectedIndex() != 0) {
            horizontalTabPane.getSelectionModel().select(0);
            horizontalTabPane.getSelectionModel().select(selectedIndex);
        } else {
            horizontalTabPane.getSelectionModel().select(1);
            horizontalTabPane.getSelectionModel().select(selectedIndex);
        }
        userActions.log(Level.INFO, "Refreshed current tab", "Attempted to refresh current tab");
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
