package data_access.localDAO;

import data_access.factories.LocalDatabaseFactory;
import data_access.interfaces.IAdministratorDataAccess;
import model.Administrator;
import utility.Searcher;

import java.util.*;
import java.util.stream.Collectors;

public class AdministratorLocalDAO implements IAdministratorDataAccess {
    private final LocalDB localDB;

    public AdministratorLocalDAO() {
        localDB = LocalDatabaseFactory.getLocalDbInstance();
    }


    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        administrators.forEach(x -> {
            localDB.storeAdministrator(x);
            Searcher.getSearcher().updateIndex(x);
        });
    }

    @Override
    public void addAdministrator(Administrator administrator) {
        localDB.storeAdministrator(administrator);
        Searcher.getSearcher().updateIndex(administrator);
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator){
        Searcher.getSearcher().removeIndex(administrator);
        return localDB.deleteUser(administrator);
    }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        return localDB.getAdministratorByUsername(username);
    }

    @Override
    public Map<Integer, List<Administrator>> searchAdministrators(String searchTerm) {
        //Initialise results map
        Map<Integer, List<Administrator>> resultsMap = new HashMap<>();
        resultsMap.put(0, new ArrayList<>());
        resultsMap.put(1, new ArrayList<>());
        resultsMap.put(2, new ArrayList<>());
        resultsMap.put(3, new ArrayList<>());
        //Loop through Administrators and put them in the appropriate list based on which field matches
        if (!searchTerm.equals("")) {
            for (Administrator a : localDB.getAdministrators()) {
                Set<String> mNames = a.getMiddleNames().stream().filter(s -> s.toLowerCase().startsWith(searchTerm.toLowerCase())).collect(Collectors.toSet());
                if (a.getUsername().toLowerCase().equals(searchTerm.toLowerCase())) {
                    resultsMap.get(0).add(a);
                } else if (a.getFirstName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(1).add(a);
                } else if (mNames.size() != 0) {
                    resultsMap.get(2).add(a);
                } else if (a.getLastName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                    resultsMap.get(3).add(a);
                }
            }
        } else { //Place them in the first list by default
            resultsMap.get(0).addAll(localDB.getAdministrators());
        }
        return resultsMap;
    }

    @Override
    public Set<Administrator> getAdministrators() {
        return localDB.getAdministrators();
    }
}
