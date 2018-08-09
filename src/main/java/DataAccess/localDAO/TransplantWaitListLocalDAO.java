package DataAccess.localDAO;

import DataAccess.interfaces.ITransplantWaitListDataAccess;
import service.OrganWaitlist;

public class TransplantWaitListLocalDAO implements ITransplantWaitListDataAccess {

    private LocalDB localDB = LocalDB.getInstance();

    @Override
    public OrganWaitlist getWaitingList() {
        return localDB.getOrganWaitlist();
    }

    @Override
    public void updateWaitingList(OrganWaitlist organRequests) {
        localDB.setOrganWaitlist(organRequests);
    }

    @Override
    public void deleteWaitingList() {
        localDB.setOrganWaitlist(new OrganWaitlist());
    }
}
