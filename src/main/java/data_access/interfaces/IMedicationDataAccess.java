package data_access.interfaces;

import model.Medication;

import java.util.List;

public interface IMedicationDataAccess {

    /**
     * Updates the given medications for a given patient
     *
     * @param nhi         The nhi of the patient
     * @param medications The list of medications to save
     * @return The success code of the update
     */
    int updateMedication(String nhi, List<Medication> medications);

    /**
     * Returns the medications for a patient with the given nhi
     *
     * @param nhi The nhi of the patient
     * @return The list of medications fetched
     */
    List<Medication> getMedicationsByNhi(String nhi);

    /**
     * Deletes all medications from a given patient
     *
     * @param nhi The nhi of the patient
     */
    void deleteAllMedicationsByNhi(String nhi);


//    void addMedicationBatch(String nhi, List<Medication> medications);

}
