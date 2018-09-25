package data_access.interfaces;

import utility.GlobalEnums;

import java.util.Map;

public interface IDonationsDataAccess {
    /**
     * Updates the donating organs of a patient with the given nhi
     * @param donorNhi the donor nhi
     * @param donatingOrgan the donating organ
     * @param receiverNhi the nhi of the receiver
     * @return
     */
    int updateDonatingOrgans(String donorNhi, GlobalEnums.Organ donatingOrgan, String receiverNhi);

    /**
     * Returns a map of donating organs and the nhi of the patient that it is going to (given by the nhi)
     * @param donorNhi the nhi of the donor
     * @return the collection or organs and assigned nhis
     */
    Map<GlobalEnums.Organ, String> getDonatingOrgansByDonorNhi(String donorNhi);

    /**
     * Deletes all organs for a patient with a given nhi
     * @param donorNhi the nhi of the donor
     */
    void deleteAllDonatingOrganByNhi(String donorNhi);
}



