package DataAccess.interfaces;

import model.Patient;

import java.util.List;

public interface IContactDataAccess {

    /**
     *
     * @param patient -
     * @return -
     */
    boolean updateContact(Patient patient);

    /**
     *
     * @param nhi -
     * @return -
     */
    List<String> getContactByNhi(String nhi);

    /**
     *
     * @return -
     */
    public void deleteContactByNhi(String nhi);
}
