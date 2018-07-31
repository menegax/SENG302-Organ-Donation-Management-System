package DataAccess;

import model.Medication;
import utility.GlobalEnums.*;

import java.sql.Connection;
import java.util.List;

public interface IMedicationDataAccess {

    /**
     *
     * @param nhi -
     * @param medication -
     * @param state -
     * @return -
     */
    int update(String nhi, Medication medication, MedicationStatus state);


    /**
     *
     * @param nhi -
     * @return -
     */
    List<Medication> select(String nhi);

    List<Medication> select(Connection connection, String nhi);
}
