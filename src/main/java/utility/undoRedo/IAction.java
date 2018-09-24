package utility.undoRedo;

public interface IAction {

    void execute();

    void unexecute();

    boolean isExecuted();
}
