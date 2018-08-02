package DataAccess.localDAO;

import DataAccess.LocalDB;
import DataAccess.interfaces.IClinicianDataAccess;
import model.Clinician;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ClinicianLocalDAO implements IClinicianDataAccess {

    @Override
    public int updateClinician(List<Clinician> clinician) {
        return 0;
    }

    @Override
    public boolean addClinician(Clinician clinician) {
        return false;
    }

    @Override
    public boolean deleteClinician(Clinician clinician) { return false; }

    @Override
    public Clinician getClinicianByStaffId(int id) {
        LocalDB localDB = LocalDB.getInstance();
        return localDB.getClinicianByStaffID(id);
    }

    @Override
    public List<Clinician> searchClinician(String searchTerm) {
        return null;
    }

    @Override
    public int nextStaffID() {throw new NotImplementedException();}
}
