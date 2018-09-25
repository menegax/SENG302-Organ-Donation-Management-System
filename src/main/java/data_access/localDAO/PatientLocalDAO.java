package data_access.localDAO;

import static utility.GlobalEnums.NONE_ID;

import data_access.factories.LocalDatabaseFactory;
import data_access.interfaces.IPatientDataAccess;
import model.Patient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.GlobalEnums;
import utility.GlobalEnums.FilterOption;
import utility.Searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class PatientLocalDAO implements IPatientDataAccess {

    private LocalDB localDB;

    public PatientLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public void savePatients(Set<Patient> patient) {
        patient.forEach(x -> {
            localDB.storePatient(x);
            Searcher.getSearcher().updateIndex(x);
        });
    }

    @Override
    public boolean addPatientsBatch(List<Patient> patient) {
        for (Patient p : patient) {
            localDB.storePatient(p);
            Searcher.getSearcher().updateIndex(p);
        }
        return true;
    }

    @Override
    public Set<Patient> getPatients() {
        return localDB.getPatients();
    }

    @Override
    public int getPatientCount() {
        return localDB.getPatients().size();
    }

    @Override
    public Patient getPatientByNhi(String nhi) {
        return localDB.getPatientByNHI(nhi);
    }

    @Override
    public boolean deletePatient(Patient patient) {
        Searcher.getSearcher().removeIndex(patient);
        return localDB.deleteUser(patient);
    }

    @Override
    public Map<Integer, List<Patient>> searchPatients(String searchTerm, Map<FilterOption, String> filters, int numResults) {
        //Collect all globalPatients and filter them out by filters
        Set<Patient> matchedPatients = new HashSet<>();
        for (Patient p : localDB.getPatients()) {
            if (matchesFilter(p, filters)) {
                matchedPatients.add(p);
            }
        }
        //Initialise results map
        Map<Integer, List<Patient>> resultsMap = new HashMap<>();
        resultsMap.put(0, new ArrayList<>());
        resultsMap.put(1, new ArrayList<>());
        resultsMap.put(2, new ArrayList<>());
        resultsMap.put(3, new ArrayList<>());
        //Loop through filtered globalPatients and put them in the appropriate list based on which field matches
        if (!searchTerm.equals("")) {
            for (Patient p : matchedPatients) {
                Set<String> mNames = p.getMiddleNames().stream().filter(s -> s.toLowerCase().startsWith(searchTerm.toLowerCase())).collect(Collectors.toSet());
                if (p.getNhiNumber().toLowerCase().equals(searchTerm.toLowerCase())) {
                    resultsMap.get(0).add(p);
                } else if (p.getFirstName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(1).add(p);
                } else if (mNames.size() != 0) {
                    resultsMap.get(2).add(p);
                } else if (p.getLastName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(3).add(p);
                }
            }
        } else { //Place them in the first list by default
            resultsMap.get(0).addAll(matchedPatients);
        }
        return resultsMap;
    }

    @Override
    public void deletePatientByNhi(String nhi) {
        throw new NotImplementedException();
    }

    /**
     * Check if a patient matches the filter criteria
     *
     * @param patient - patient to check filter against
     * @param filter  - filter to use
     * @return - bool if a match
     */
    public boolean matchesFilter(Patient patient, Map<GlobalEnums.FilterOption, String> filter) {
        if (filter == null) {
            return false;
        }
        for (FilterOption option : filter.keySet()) {
            if (!filter.get(option).equals(NONE_ID)) { //check each fiter entry to see if its been selected
                switch (option) {
                    case REGION: {
                        GlobalEnums.Region region = GlobalEnums.Region.getEnumFromString(filter.get(option));
                        if (patient.getRegion() == null || !patient.getRegion().equals(region)) {
                            return false;
                        }
                        break;
                    }
                    case DONATIONS: {
                        GlobalEnums.Organ donations = GlobalEnums.Organ.getEnumFromString(filter.get(option));
                        if (patient.getDonations() == null || !patient.getDonations().keySet().contains(donations)) {
                            return false;
                        }
                        break;
                    }
                    case REQUESTEDDONATIONS: {
                        GlobalEnums.Organ requestedOrgans = GlobalEnums.Organ.getEnumFromString(filter.get(option));
                        if (patient.getRequiredOrgans() == null || !patient.getRequiredOrgans().containsKey(requestedOrgans)) {
                            return false;
                        }
                        break;
                    }
                    case BIRTHGENDER: {
                        GlobalEnums.BirthGender birthGender = GlobalEnums.BirthGender.getEnumFromString(filter.get(option));
                        if (patient.getBirthGender() == null || !patient.getBirthGender().equals(birthGender)) {
                            return false;
                        }
                        break;
                    }
                    case DONOR: {
                        if (Boolean.valueOf(filter.get(option)).equals(true) && patient.getDonations().size() == 0) {
                            return false;
                        }
                        break;
                    }
                    case RECIEVER: {
                        if (Boolean.valueOf(filter.get(option)).equals(true) && patient.getRequiredOrgans().size() == 0) {
                            return false;
                        }
                        break;
                    }
                    case AGEUPPER:
                    case AGELOWER: {
                        if (patient.getAge() > Double.valueOf(filter.get(FilterOption.AGEUPPER)).intValue()
                                || patient.getAge() < Double.valueOf(filter.get(FilterOption.AGELOWER)).intValue()) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<Patient> getDeadDonors() {
        List<Patient> patients = new ArrayList<>();
        for (Patient patient : localDB.getPatients()) {
            if (patient.getDeathDate() != null && patient.getDonations().size() > 0) {
                patients.add(patient);
            }
        }
        return patients;
    }

}
