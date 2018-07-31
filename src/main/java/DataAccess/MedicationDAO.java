package DataAccess;

import model.Medication;
import model.Patient;

import java.util.List;

public class MedicationDAO implements IMedicationDataAccess {

    @Override
    public boolean update(Patient patient)
    {
        patient.getMedicationHistory().forEach(x -> {

        });
        patient.getCurrentMedications().forEach(x ->{

        });
    }

    @Override
    public boolean insert() {
        return false;
    }

    @Override
    public List<Medication> select() {
        return null;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
