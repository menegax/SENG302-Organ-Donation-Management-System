package DataAccess.localDAO;

import DataAccess.factories.LocalDatabaseFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import model.Clinician;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.Searcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClinicianLocalDAO implements IClinicianDataAccess {

    private LocalDB localDB;

    public ClinicianLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public void saveClinician(Set<Clinician> clinician) {
        clinician.forEach(x -> {
            localDB.storeClinician(x);
            Searcher.getSearcher().updateIndex(x);
        });
    }

    @Override
    public boolean addClinician(Clinician clinician) {
        return false;
    }

    @Override
    public boolean deleteClinician(Clinician clinician) {
        Searcher.getSearcher().removeIndex(clinician);
        return localDB.deleteUser(clinician);
    }

    @Override
    public Clinician getClinicianByStaffId(int id) {
        return localDB.getClinicianByStaffID(id);
    }

    @Override
    public Map<Integer,List<Clinician>> searchClinicians(String searchTerm) {
        return null;
    }

    @Override
    public Set<Clinician> getClinicians() {
        return localDB.getClinicians();
    }

    @Override
    public int nextStaffID() {throw new NotImplementedException();}
}
