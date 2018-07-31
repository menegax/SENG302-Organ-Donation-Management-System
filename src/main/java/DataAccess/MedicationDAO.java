package DataAccess;

import model.Medication;

import java.util.List;

public class MedicationDAO implements IMedicationDataAccess {
    @Override
    public boolean update() {
        return false;
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
