package DataAccess.interfaces;

import model.Medication;
import utility.GlobalEnums.*;

import java.util.List;

public interface IMedicationDataAccess {


    int updateMedication(String nhi, Medication medication, MedicationStatus state);

    List<Medication> getMedicationsByNhi (String nhi);

}
