package DataAccess;

import model.Medication;
import utility.GlobalEnums;

import java.util.List;

public interface IMedicationDataAccess {

    int update(String nhi, Medication medication, GlobalEnums.MedicationStatus state);

    List<Medication> select(String nhi);

    boolean delete();

}
