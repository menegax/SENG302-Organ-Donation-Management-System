package data_access.interfaces;

import service.OrganWaitlist;

public interface ITransplantWaitListDataAccess {

    /**
     * Gets the transplant waiting list
     *
     * @return The OrganWaitList object representing the waiting list
     */
    OrganWaitlist getWaitingList();

    /**
     * Updates the organ waiting list with the given OrganWaitList object
     *
     * @param organRequests The new state of the OrganWaitList
     */
    void updateWaitingList(OrganWaitlist organRequests);

    /**
     * Deletes the organ waiting list
     */
    void deleteWaitingList();


    /**
     * Deletes all organ requests for the given nhi
     */
    void deleteRequestsByNhi(String nhi);

}
