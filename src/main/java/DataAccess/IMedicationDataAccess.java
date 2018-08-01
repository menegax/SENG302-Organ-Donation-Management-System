package DataAccess;

import model.Medication;
import utility.GlobalEnums.*;

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

}
