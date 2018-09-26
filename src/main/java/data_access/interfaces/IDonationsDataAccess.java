package data_access.interfaces;

import utility.GlobalEnums;

import java.util.Map;

public interface IDonationsDataAccess {
    /**
     * Updates the donating organs of a patient with the given nhi
     * @param donorNhi The nhi of the donor
     * @param donatingOrgan The organ to update
     * @param receiverNhi The nhi of the receiver
     * @return The number of modified rows
     */
    int updateDonatingOrgans(String donorNhi, GlobalEnums.Organ donatingOrgan, String receiverNhi);

    /**
     * Returns a map of donating organs and the nhi of the patient that it is going to (given by the nhi)
     * @param donorNhi The nhi of the donor to get donating organs from
     * @return A map of donating organ to receiver nhi
     */
    Map<GlobalEnums.Organ, String> getDonatingOrgansByDonorNhi(String donorNhi);

    /**
     * Deletes all organs for a patient with a given nhi
     * @param donorNhi The nhi of the donor to delete all donating organs for
     */
    void deleteAllDonatingOrganByNhi(String donorNhi);
}



