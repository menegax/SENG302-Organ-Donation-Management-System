package DataAccess.interfaces;

import service.OrganWaitlist;

public interface ITransplantWaitListDataAccess {

    public OrganWaitlist getWaitingList();

    public void updateWaitingList(OrganWaitlist organRequests);

    public void deleteWaitingList();

}
