package DataAccess.localDAO;

import DataAccess.interfaces.ITransplantWaitListDataAccess;
import service.OrganWaitlist;

public class TransplantWaitListLocalDAO implements ITransplantWaitListDataAccess {

    @Override
    public OrganWaitlist getWaitingList() {
        return null;
    }

    @Override
    public void updateWaitingList(OrganWaitlist organRequests) {

    }

    @Override
    public void deleteWaitingList() {

    }
}
