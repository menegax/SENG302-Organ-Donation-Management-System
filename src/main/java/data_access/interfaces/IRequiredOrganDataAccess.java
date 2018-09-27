package data_access.interfaces;

import model.OrganReceival;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.Map;

public interface IRequiredOrganDataAccess {

    /**
     * Updates the patient required organs with the given nhi
     *
     * @param nhi     The patients nhi
     * @param requiredOrgan The organs that is being updated
     * @param date The new date for the date of the organ registration
     * @return The success code of the update
     */
    int updateRequiredOrgans(String nhi, GlobalEnums.Organ requiredOrgan, LocalDate date);

    /**
     * Returns a list of required organs for a certain patient (given by the nhi)
     *
     * @param nhi The nhi for the patient to fetch required organs for
     * @return The list of diseases
     */
    Map<GlobalEnums.Organ, OrganReceival> getRequiredOrganByNhi(String nhi);

    /**
     * Removes a required organ for a certain patient (given by the nhi)
     * @param organ the required organ
     * @param nhi The nhi for the patient to remove required organs from
     * @param organ The organ to delete
     */
    void deleteRequiredOrganByNhi(String nhi, GlobalEnums.Organ organ);

    /**
     * Removes all required organs of a patient
     *
     * @param nhi The nhi for the patient to remove all required organs from
     */
    void deleteAllRequiredOrgansByNhi(String nhi);
}
