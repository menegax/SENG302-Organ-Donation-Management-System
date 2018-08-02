package DataAccess.interfaces;

import model.Medication;
import utility.GlobalEnums.*;

import java.util.List;

public interface IMedicationDataAccess {


    public int updateMedication(String nhi, Medication medication, MedicationStatus state);

    public List<Medication> getMedicationsByNhi (String nhi);

    public void deleteAllMedicationsByNhi(String nhi);

}
