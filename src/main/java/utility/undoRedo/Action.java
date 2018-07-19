package utility.undoRedo;

import model.User;

public class Action {

    private User before;
    private User current;
    private User after;

    public Action(User current, User after) {
        before = current.deepClone();
        current = current;
        after = after.deepClone();
        execute();
    }

    public void execute() {
        current.setAttributes(after);
    }

    public void unexecute() {
        current.setAttributes(before);
    }
}
