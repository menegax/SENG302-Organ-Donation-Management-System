package data_access.localDAO;

import data_access.factories.LocalDatabaseFactory;
import data_access.interfaces.IClinicianDataAccess;
import model.Clinician;
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
        localDB.storeClinician(clinician);
        Searcher.getSearcher().updateIndex(clinician);
        return true;
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

    /**
     * Returns the next staff ID for a new clinician account according to highest staff ID in the local database
     */
    @Override
    public int nextStaffID() {
        int highest = 0;
        for (Clinician c : getClinicians()) {
            if (c.getStaffID() > highest) {
                highest = c.getStaffID();
            }
        }
        return highest + 1;
    }
}
