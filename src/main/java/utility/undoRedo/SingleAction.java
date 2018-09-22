package utility.undoRedo;

import data_access.factories.DAOFactory;
import data_access.interfaces.ITransplantWaitListDataAccess;
import data_access.interfaces.IUserDataAccess;
import controller.ScreenControl;
import data_access.mysqlDAO.TransplantWaitingListDAO;
import model.Patient;
import model.User;
import service.OrganWaitlist;
import utility.GlobalEnums;

import java.util.Iterator;

/**
 * Represents an action (edit, add, delete) performed on a user in the application
 */
public class SingleAction implements IAction {

    private User before;
    private User current;
    private User after;
    private boolean isExecuted;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);
    private IUserDataAccess dao = factory.getUserDataAccess();
    private ITransplantWaitListDataAccess waitingListDAO = factory.getTransplantWaitingListDataAccess();

    /**
     * Constructor for the action
     * @param current the object of the current user (before the action has occurred
     * @param after a user with the attributes of the user after the action has occurred
     */
    public SingleAction(User current, User after) {
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
    @Override
    public void execute() {
        if (after == null) {
            dao.deleteUser(current);
            current = null;
        } else if (before == null) {
            current = after.deepClone();
            dao.addUser(current);
        } else {
            current.setAttributes(after);
            dao.deleteUser(current);
            dao.addUser(current);
        }
        if (current instanceof  Patient) {
            createOrganRequests((Patient) current);
        }
        screenControl.setIsSaved(false);
        isExecuted = true;
    }

    /**
     * Unexecutes (undoes) the action (does nothing if the action has already been undone)
     */
    @Override
    public void unexecute() {
        if (after == null) {
            current = before.deepClone();
            dao.addUser(current);
        } else if (before == null) {
            dao.deleteUser(current);
            current = null;
        } else {
            current.setAttributes(before);
            dao.deleteUser(current);
            dao.addUser(current);
        }
        if (current instanceof Patient) {
            createOrganRequests((Patient) current);
        }
        screenControl.setIsSaved(false);
        isExecuted = false;
    }

    @Override
    public boolean isExecuted() {
        return isExecuted;
    }


    /**
     * Creates new organ requests for updated registered organs to receive, and adds them to the organ
     * waiting list.
     */
    private void createOrganRequests(Patient patient) {
        OrganWaitlist waitlist = waitingListDAO.getWaitingList();
        if (waitlist == null) {
            waitlist = new OrganWaitlist();
        }
        Iterator<OrganWaitlist.OrganRequest> iter = waitlist.iterator();
        while (iter.hasNext()) {
            OrganWaitlist.OrganRequest next = iter.next();
            if (next.getReceiverNhi().equals(patient.getNhiNumber())) {
                iter.remove();
            }
        }
        for (GlobalEnums.Organ organ : patient.getRequiredOrgans().keySet()) {
            waitlist.add(patient, organ);
        }
        waitingListDAO.updateWaitingList(waitlist);
    }
}
