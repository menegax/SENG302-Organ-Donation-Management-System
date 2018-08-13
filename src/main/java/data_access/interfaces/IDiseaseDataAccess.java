package data_access.interfaces;

import model.Disease;

import java.util.List;

public interface IDiseaseDataAccess {

    /**
     * Updates the given disease for the patient with the given nhi
     *
     * @param nhi     The patients nhi
     * @param disease The disease that is being updated
     * @return The success code of the update
     */
    int updateDisease(String nhi, Disease disease);

    /**
     * Returns a list of diseases for a certain patient (given by the nhi)
     *
     * @param nhi The nhi for the patient to fetch diseases for
     * @return The list of diseases
     */
    List<Disease> getDiseaseByNhi(String nhi);

    /**
     * Removes all diseases for a certain patient (given by the nhi)
     *
     * @param nhi The nhi for the patient to remove diseases from
     */
    void deleteAllDiseasesByNhi(String nhi);

}
