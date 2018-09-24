package data_access.interfaces;

import utility.GlobalEnums;

import java.util.Map;

public interface IDonationsDataAccess {
    /**
     * Updates the donating organs of a patient with the given nhi
     * @param donorNhi
     * @param donatingOrgan
     * @param receiverNhi
     * @return
     */
    int updateDonatingOrgans(String donorNhi, GlobalEnums.Organ donatingOrgan, String receiverNhi);

    /**
     * Returns a map of donating organs and the nhi of the patient that it is going to (given by the nhi)
     * @param donorNhi
     * @return
     */
    Map<GlobalEnums.Organ, String> getDonatingOrgansByDonorNhi(String donorNhi);

    /**
     * Deletes all organs for a patient with a given nhi
     * @param donorNhi
     */
    void deleteAllDonatingOrganByNhi(String donorNhi);
}



