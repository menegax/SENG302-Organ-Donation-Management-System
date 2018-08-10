package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.User;
import service.Database;
import utility.StatusObservable;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIAdministratorProfile extends TargetedController{
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

    private UserControl userControl = UserControl.getUserControl();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void load() {
        if (((Administrator) target).getUsername().toLowerCase().equals("admin")) {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        } else {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
        loadProfile((Administrator) target);
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
        if (!((Administrator) target).getUsername().toLowerCase().equals("admin")) {
            userActions.log(Level.INFO, "Successfully deleted admin profile", new String[]{"Attempted to delete admin profile", ((Administrator) target).getUsername()});
            Database.deleteAdministrator((Administrator) target);
            if (!((Administrator) target).getUsername().equals(((Administrator) userControl.getLoggedInUser()).getUsername())) {
                ((Stage) adminProfilePane.getScene().getWindow()).close();
            }
        }
    }
}
