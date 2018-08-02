package controller;

import DataAccess.factories.DAOFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.User;
import service.Database;
import utility.GlobalEnums;
import utility.StatusObservable;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIAdministratorProfile {
    @FXML
    private GridPane adminProfilePane;

    @FXML
    private Label usernameTxt;

    @FXML
    private Label nameTxt;

    @FXML
    private Label modifiedLbl;

    @FXML
    private Button deleteButton;

    private Administrator target;

    private UserControl userControl = new UserControl();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        User loggedIn = userControl.getLoggedInUser();
        if (userControl.getTargetUser() instanceof Administrator) {
            target = (Administrator) userControl.getTargetUser();
        } else {
            target = (Administrator) loggedIn;
        }
        if (target.getUsername().toLowerCase().equals("admin")) {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
//        if (target.getUsername().equals(((Administrator) loggedIn).getUsername())) {
//            deleteButton.setVisible(false);
//            deleteButton.setDisable(true);
//        }
        loadProfile(target);
    }

    /**
     * Loads administrator attributes to display in the administrator profile screen
     *
     * @param administrator clinician logged in
     */
    private void loadProfile(Administrator administrator) {
        usernameTxt.setText(administrator.getUsername());
        nameTxt.setText(administrator.getNameConcatenated());
        modifiedLbl.setText(administrator.getModified() == null ? "--" : administrator.getModified().toString());
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        if (!target.getUsername().toLowerCase().equals("admin")) {
            Administrator administrator = (Administrator) userControl.getTargetUser();
            Action action = new Action(administrator, null);
            for (Stage stage : screenControl.getUsersStages(userControl.getLoggedInUser())) {
                if (stage instanceof UndoableStage) {
                    for (StatesHistoryScreen statesHistoryScreen : ((UndoableStage) stage).getStatesHistoryScreens()) {
                        if (statesHistoryScreen.getUndoableScreen().equals(GlobalEnums.UndoableScreen.ADMINISTRATORSEARCHUSERS)) {
                            statesHistoryScreen.addAction(action);
                        }
                    }
                }
            }
            userActions.log(Level.INFO, "Successfully deleted admin profile", new String[]{"Attempted to delete admin profile", target.getUsername()});
            if (!target.getUsername().equals(((Administrator) userControl.getLoggedInUser()).getUsername())) {
                ((Stage) adminProfilePane.getScene().getWindow()).close();
            }
        }
    }
}
