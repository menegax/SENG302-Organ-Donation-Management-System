package controller;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.Stages;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScreenControl {

    //Todo remove all deprecated stuff

    private static Map<Stages, Stage> applicationStages;

    private static ScreenControl screenControl;

    @Deprecated
    private static HashMap<String, Pane> screenMap = new HashMap<>();

    @Deprecated
    private static HashMap<String, Stage> popMap = new HashMap<>();

    @Deprecated
    public static Map<String, Parent> scenes = new HashMap<>();

    @Deprecated
    private static Scene main;

    private static KeyCodeCombination logOut;
    private static KeyCodeCombination save;
    private static KeyCodeCombination importt;
    private static KeyCodeCombination undo;
    private static KeyCodeCombination redo;

    private static String appName = "Big Pharma";


    private ScreenControl() {
        applicationStages = new HashMap<>();
    }


    static ScreenControl getScreenControl() {
        if (screenControl == null) {
            screenControl = new ScreenControl();
        }
        return screenControl;
    }


    /**
     * @param key
     * @param stage
     */
    void addStage(Stages key, Stage stage) {
        applicationStages.put(key, stage);
    }


    /**
     * @param root
     */
    void show(Stages stageName, Parent root) {
        Stage stage = applicationStages.get(stageName);
        stage.setScene(new Scene(root));
        setUpMenuBar(stage);
        stage.show();
    }


    @Deprecated
    public static Scene getMain() {
        return main;
    }


    /**
     * set initial screen to display
     *
     * @param mainScene - main screen to display
     */
    @Deprecated
    static void setRootScene(Scene mainScene) {
        main = mainScene;
    }


    /**
     * Add screen to the hash map of screens
     *
     * @param name - name of screen to add
     * @param pane - Pane object from FXML
     */
    @Deprecated
    static void addTabToHome(String name, Pane pane) {
        //create tab
        // load pane into tab
        // add tab to existing tab pane
        //
        screenMap.put(name, pane);
    }
    //todo


    /**
     * Remove screen from hash map
     *
     * @param name - screen to remove from the hashmap of screens
     */
    @Deprecated
    static void removeScreen(String name) {
        screenMap.remove(name);
    }


    /**
     * Displays the given scene to the UI
     *
     * @param name - screen name to display
     */
    @Deprecated
    static void activate(String name) {
        main.setRoot(screenMap.get(name));
    }


    /**
     * Adds stage, name pair to a hashmap
     *
     * @param name  - name of the popup
     * @param stage - stage to display
     */
    @Deprecated
    static void addPopUp(String name, Stage stage) {
        popMap.put(name, stage);
    }


    /**
     * Switches panes within a popup window, while passing along the current viewed patient
     *
     * @param scene      The scene to load the new pane into
     * @param fxmlLoader The fxmlLoader for the new pane
     * @exception IOException If the pane fails to load
     */
    static void loadPopUpPane(Scene scene, FXMLLoader fxmlLoader) throws IOException {
        scene.setRoot(fxmlLoader.load());
    }


    /**
     * Displays a given popup
     *
     * @param name - name of the pop up to display
     */
    @Deprecated
    static void displayPopUp(String name) {
        popMap.get(name)
                .show();
    }


    /**
     * Closes a given popup
     *
     * @param name - name of the popup to hide
     */
    @Deprecated
    public static void closePopUp(String name) {
        popMap.get(name)
                .close();
    }



    /**
     * Sets keyboard shortcuts depending on the OS
     */
    private void setUpKeyCodeCombinations() {
        if (System.getProperty("os.name").startsWith("Mac")) { // MacOS
            logOut = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN);
            save = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);
            importt = new KeyCodeCombination(KeyCode.I, KeyCombination.META_DOWN);
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN);
            redo = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN);
        }
        else { // Windows or Linux
            logOut = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN);
            save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            importt = new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN);
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        }
    }


    /**
     * Creates a native-looking MacOS menu bar for the application
     */
    private void setUpMenuBar(Stage stage) {

        boolean isMacOs = System.getProperty("os.name").startsWith("Mac");

        setUpKeyCodeCombinations();


        // Get the toolkit
        MenuToolkit tk = MenuToolkit.toolkit(); //Todo THIS IS A MAC OS ONLY LIBRARY i think

        // Create a new menu bar
        MenuBar bar = new MenuBar();

        // Add the default application menu
        bar.getMenus().add(tk.createDefaultApplicationMenu(appName)); //Todo add ternary using isMacOs

        // Add some more Menus...
        Menu menu1 = new Menu("App");
        MenuItem menu1Item1 = new MenuItem("Log out");
        menu1Item1.setAccelerator(logOut);
        menu1Item1.setOnAction(event -> {
            new UserControl().rmLoggedInUserCache();
            userActions.log(INFO, "Successfully logged out the user ", "Attempted to log out");
        });
        menu1.getItems().addAll(menu1Item1);

        Menu menu2 = new Menu("File");
        MenuItem menu2Item1 = new MenuItem("Save");
        menu2Item1.setAccelerator(save);
        menu2Item1.setOnAction(event -> {
            Database.saveToDisk();
            userActions.log(INFO, "Successfully saved to disk", "Attempted to save to disk");
        });
        Menu subMenuImport = new Menu("Import"); // import submenu
        MenuItem menu2Item2 = new MenuItem("Import patients...");
        menu2Item2.setAccelerator(importt);
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
        menu3Item1.setAccelerator(undo);
        menu3Item1.setOnAction(event -> System.out.println("Undo clicked")); //Todo add functionality
        MenuItem menu3Item2 = new MenuItem("Redo");
        menu3Item2.setAccelerator(redo);
        menu3Item2.setOnAction(event -> System.out.println("Redo clicked")); //Todo add functionality
        menu3.getItems()
                .addAll(menu3Item1, menu3Item2);

        bar.getMenus()
                .addAll(menu1, menu2, menu3);

        // Use the menu bar for primary stage
        tk.setMenuBar(stage, bar); //Todo use ternary for menubar if isMacOs else windows/linux
    }

}
