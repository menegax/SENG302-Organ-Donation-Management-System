package DataAccess.localDAO;

import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import model.Clinician;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Set;

public class ClinicianLocalDAO implements IClinicianDataAccess {

    private LocalDB localDB;

    public ClinicianLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public int saveClinician(Set<Clinician> clinician) {
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
        return localDB.getClinicianByStaffID(id);
    }

    @Override
    public List<Clinician> searchClinician(String searchTerm) {
        return null;
    }

    @Override
    public Set<Clinician> getClinicians() {
        return localDB.getClinicians();
    }

    @Override
    public int nextStaffID() {throw new NotImplementedException();}
}
