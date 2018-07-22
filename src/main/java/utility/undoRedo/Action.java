package utility.undoRedo;

import model.User;

/**
 * Represents an action (edit, add, delete) performed on a user in the application
 */
public class Action {

    private User before;
    private User current;
    private User after;

    /**
     * Constructor for the action
     * @param current the object of the current user (before the action has occurred
     * @param after a user with the attributes of the user after the action has occurred
     */
    public Action(User current, User after) {
        this.before = current.deepClone();
        this.current = current;
        this.after = after.deepClone();
        execute();
    }

    /**
     * Executes the action (does nothing if action has occurred)
     */
    public void execute() {
        current.setAttributes(after);
    }

    /**
     * Unexecutes (undoes) the action (does nothing if the action has already been undone)
     */
    public void unexecute() {
        current.setAttributes(before);
    }
}
