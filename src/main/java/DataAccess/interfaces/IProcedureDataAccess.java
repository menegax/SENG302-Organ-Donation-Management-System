package DataAccess.interfaces;

import model.Procedure;

import java.util.List;

public interface IProcedureDataAccess {

    /**
     * Updates the given procedure for the patient with the given nhi
     *
     * @param nhi       The patients nhi
     * @param procedure The procedure that is being updated
     * @return The success code of the update
     */
    int updateProcedure(String nhi, Procedure procedure);

    /**
     * Returns a list of procedures for a certain patient (given by the nhi)
     *
     * @param nhi The nhi for the patient to fetch procedures for
     * @return The list of procedures
     */
    List<Procedure> getProceduresByNhi(String nhi);

    /**
     * Removes all procedures for a certain patient (given by the nhi)
     *
     * @param nhi The nhi for the patient to remove procedures from
     */
    void deleteAllProceduresByNhi(String nhi);
}
