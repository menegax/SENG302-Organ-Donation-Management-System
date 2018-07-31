package DataAccess;

import model.Medication;

import java.util.List;

public interface IMedicationDataAccess {

    boolean update();

    boolean insert();

    List<Medication> select();

    boolean delete();

}
