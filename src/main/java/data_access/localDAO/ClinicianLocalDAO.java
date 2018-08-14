package data_access.localDAO;

import data_access.factories.LocalDatabaseFactory;
import data_access.interfaces.IClinicianDataAccess;
import model.Clinician;
import utility.Searcher;

import java.util.*;
import java.util.stream.Collectors;

public class ClinicianLocalDAO implements IClinicianDataAccess {

    private final LocalDB localDB;

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
        //Initialise results map
        Map<Integer, List<Clinician>> resultsMap = new HashMap<>();
        resultsMap.put(0, new ArrayList<>());
        resultsMap.put(1, new ArrayList<>());
        resultsMap.put(2, new ArrayList<>());
        resultsMap.put(3, new ArrayList<>());
        //Loop through clinicians and put them in the appropriate list based on which field matches
        if (!searchTerm.equals("")) {
            for (Clinician c : localDB.getClinicians()) {
                Set<String> mNames = c.getMiddleNames().stream().filter(s -> s.toLowerCase().startsWith(searchTerm.toLowerCase())).collect(Collectors.toSet());
                if (String.valueOf(c.getStaffID()).equals(searchTerm)) {
                    resultsMap.get(0).add(c);
                } else if (c.getFirstName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(1).add(c);
                } else if (mNames.size() != 0) {
                    resultsMap.get(2).add(c);
                } else if (c.getLastName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(3).add(c);
                }
            }
        } else { //Place them in the first list by default
            resultsMap.get(0).addAll(localDB.getClinicians());
        }
        return resultsMap;
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
