package data_access.localDAO;

import data_access.interfaces.ITransplantWaitListDataAccess;
import service.OrganWaitlist;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    @Override
    public void deleteRequestsByNhi(String nhi) {
        throw new NotImplementedException();
    }
}
