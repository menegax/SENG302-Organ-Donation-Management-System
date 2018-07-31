package DataAccess;

import model.Medication;
import model.Patient;

import java.util.List;

public interface IMedicationDataAccess {

    boolean update(Patient patient);

    boolean insert();

    List<Medication> select();

    boolean delete();

}
