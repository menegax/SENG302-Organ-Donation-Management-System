package utility.undoRedo;

import controller.ScreenControl;
import data_access.factories.DAOFactory;
import data_access.interfaces.IUserDataAccess;
import model.Patient;
import utility.GlobalEnums;

public class MultiAction implements IAction {
    private Patient patient1Before;
    private Patient patient1Current;
    private Patient patient1After;

    private Patient patient2Before;
    private Patient patient2Current;
    private Patient patient2After;

    private boolean isExecuted;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    private IUserDataAccess dao = factory.getUserDataAccess();

    public MultiAction(Patient patient1Current, Patient patient1After, Patient patient2Current, Patient patient2After) {
        this.patient1Before = (Patient) patient1Current.deepClone();
        this.patient1Current = patient1Current;
        this.patient1After = (Patient) patient1After.deepClone();
        this.patient2Before = (Patient) patient2Current.deepClone();
        this.patient2Current = patient2Current;
        this.patient2After = (Patient) patient2After.deepClone();
        execute();
        screenControl.setIsSaved(false);
    }

    @Override
    public void execute() {
        patient1Current.setAttributes(patient1After);
        patient2Current.setAttributes(patient2After);
        dao.deleteUser(patient1Current);
        dao.deleteUser(patient2Current);
        dao.addUser(patient1Current);
        dao.addUser(patient2Current);
        screenControl.setIsSaved(false);
        isExecuted = true;
    }

    @Override
    public void unexecute() {
        System.out.println("UNDOING");
        patient1Current.setAttributes(patient1Before);
        patient2Current.setAttributes(patient2Before);
        dao.deleteUser(patient1Current);
        dao.deleteUser(patient2Current);
        dao.addUser(patient1Current);
        dao.addUser(patient2Current);
        screenControl.setIsSaved(false);
        isExecuted = false;
    }

    @Override
    public boolean isExecuted() {
        return isExecuted;
    }


}
