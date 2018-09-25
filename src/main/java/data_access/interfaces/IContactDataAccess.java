package data_access.interfaces;

import model.Patient;

import java.util.List;

public interface IContactDataAccess {

    /**
     * Updates the contact details for the given patient
     *
     * @param patient The globalPatients to update
     * @return The success code of the update
     */
    boolean updateContact(Patient patient);

    /**
     * Returns the contact details of the patient with the given nhi
     *
     * @param nhi The globalPatients nhi to fetch contact details from
     * @return The List of strings representing the contact details
     */
    List<String> getContactByNhi(String nhi);

    /**
     * Deletes contact details for the patient with the given nhi
     *
     * @param nhi The globalPatients nhi to delete contact details from
     */
    void deleteContactByNhi(String nhi);

    void addContactBatch(List<Patient> patients);
}
