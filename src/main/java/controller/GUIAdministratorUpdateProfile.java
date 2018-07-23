package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import model.Administrator;
import service.Database;
import utility.GlobalEnums;
import utility.undoRedo.StatesHistoryScreen;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

/**
 * Controller class to control GUI Clinician updating screen.
 */
public class GUIAdministratorUpdateProfile extends UndoableController {

    @FXML
    public GridPane administratorUpdateAnchorPane;

    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField firstnameTxt;
    @FXML
    private TextField lastnameTxt;
    @FXML
    private TextField middlenameTxt;
    @FXML
    private PasswordField passwordTxt;

    private Administrator target;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    
    private Database database = Database.getDatabase();
    		
    /**
     * Initializes the administrator editing screen.
     * Populates the Region drop down menu using region enums.
     * Calls to load the administrator profile and calls to set up undo/redo functionality
     */
    public void initialize() {
        // Registering a change event to clear the invalid class
        UserControl userControl = new UserControl();
        if (userControl.getTargetUser() instanceof Administrator) {
            target = (Administrator) userControl.getTargetUser();
        } else {
            target = (Administrator) userControl.getLoggedInUser();
        }
        loadProfile(target.getUsername());
        setUpStateHistory();
    }

    /**
     * Loads the currently logged in administrator from the Database and populates the tables using the logged
     * in administrator's attributes.
     *
     * @param username The administrators username
     */
    private void loadProfile(String username) {
        try {
            Administrator administrator = database.getAdministratorByUsername(username);
            populateForm(administrator);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
        }
    }

    /**
     * Creates a list of control elements using all editable nodes on the update screen and initializes
     * the StateHistoryScreen used to undo or redo actions using the control elements
     */
    private void setUpStateHistory() {
        controls = new ArrayList<Control>() {{
            add(firstnameTxt);
            add(lastnameTxt);
            add(middlenameTxt);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANPROFILEUPDATE);
    }

    /**
     * Populates the update screen using the current administrator attributes
     *
     * @param administrator logged in administrator
     */
    private void populateForm(Administrator administrator) {
        //adds last modified date only if clinician has been edited before
        if (target.getModified() != null)
            lastModifiedLbl.setText("Last Updated: " + administrator.getModified().toString());
        else lastModifiedLbl.setText("Last Updated: n/a");
        firstnameTxt.setText(administrator.getFirstName());
        lastnameTxt.setText(administrator.getLastName());
        for (String name : administrator.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
    }

    /**
     * Checks fields for validity before setting administrators's updated attributes and returning to profile.
     * Changes are saved for the session, but are only persistently saved by calling save from the home page
     */
    public void saveProfile() {
        Boolean valid = true;
        if (firstnameTxt.getText().length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", firstnameTxt.getText())) {
            valid = false;
            setInvalid(firstnameTxt);
        }
        if (lastnameTxt.getText().length() == 0 || !Pattern.matches("[a-z|A-Z]{1,20}", lastnameTxt.getText())) {
            valid = false;
            setInvalid(lastnameTxt);
        }
        if (!Pattern.matches("[a-z|A-Z ]{0,50}", middlenameTxt.getText())) {
            valid = false;
            setInvalid(middlenameTxt);
        }
        if (passwordTxt.getText().length() > 0 && passwordTxt.getText().length() < 6) {
            valid = false;
            setInvalid(passwordTxt);
        }
        // If all the fields are entered correctly
        if (valid) {
            target.setFirstName(firstnameTxt.getText());
            target.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText().split(" "));
            ArrayList middles = new ArrayList();
            middles.addAll(middlenames);
            target.setMiddleNames(middles);
            if (passwordTxt.getText().length() > 0) {
                target.setPassword(passwordTxt.getText());
            }

            target.userModified();
            userActions.log(Level.INFO, "Successfully updated admin profile", new String[]{"Attempted to update admin profile", target.getUsername()});
            new Alert(Alert.AlertType.INFORMATION, "Admin successfully updated", ButtonType.OK).showAndWait();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).show();
        }
    }

    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private void setInvalid(Control target) {
        target.getStyleClass().add("invalid");
    }

    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        if (target.getStyleClass().contains("invalid")) {
            target.getStyleClass().remove("invalid");
        }
    }

    /**
     * Checks if the keyevent target was a textfield. If so, if the target has the invalid class, it is removed.
     *
     * @param e The KeyEvent instance
     */
    public void onKeyReleased(KeyEvent e) {
        if (e.getTarget().getClass() == TextField.class) {
            TextField target = (TextField) e.getTarget();
            setValid(target);
        }
    }
}