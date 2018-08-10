package utility.undoRedo;

import DataAccess.factories.DAOFactory;
import DataAccess.interfaces.IUserDataAccess;
import controller.ScreenControl;
import model.User;
import utility.GlobalEnums;

/**
 * Represents an action (edit, add, delete) performed on a user in the application
 */
public class Action {

    private User before;
    private User current;
    private User after;
    private boolean isExecuted;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    private IUserDataAccess dao = factory.getUserDataAccess();

    /**
     * Constructor for the action
     * @param current the object of the current user (before the action has occurred
     * @param after a user with the attributes of the user after the action has occurred
     */
    public Action(User current, User after) {
        if (current == null) { // New User added
            this.before = null;
            this.current = null;
            this.after = after.deepClone();
        } else if (after == null) { // User deleted
            this.before = current.deepClone();
            this.current = current;
            this.after = null;
        } else { // User updated
            this.before = current.deepClone();
            this.current = current;
            this.after = after.deepClone();
        }
        execute();
        screenControl.setIsSaved(false);
    }

    /**
     * Executes the action (does nothing if action has occurred)
     */
    public void execute() {
        if (after == null) {
            dao.deleteUser(current);
            current = null;
        } else if (before == null) {
            current = after.deepClone();
            dao.addUser(current);
        } else {
            current.setAttributes(after);
        }
        screenControl.setIsSaved(false);
        isExecuted = true;
    }

    /**
     * Unexecutes (undoes) the action (does nothing if the action has already been undone)
     */
    public void unexecute() {
        if (after == null) {
            current = before.deepClone();
            dao.addUser(current);
        } else if (before == null) {
            dao.deleteUser(current);
            current = null;
        } else {
            current.setAttributes(before);
        }
        screenControl.setIsSaved(false);
        isExecuted = false;
    }

    public boolean isExecuted() {
        return isExecuted;
    }
}
