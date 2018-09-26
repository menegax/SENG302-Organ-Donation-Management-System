package utility;

import static utility.GlobalEnums.BirthGender;
import static utility.GlobalEnums.FilterOption;
import static utility.GlobalEnums.NONE_ID;
import static utility.GlobalEnums.Organ;
import static utility.GlobalEnums.Region;
import static utility.GlobalEnums.UserTypes;
import static utility.SystemLogger.systemLogger;

import data_access.localDAO.LocalDB;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Searcher {

    private Analyzer analyzer = new StandardAnalyzer();

    private IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private RAMDirectory ramDirectory = new RAMDirectory();

    private IndexWriter indexWriter = null;

    private IndexSearcher indexSearcher = null;

    private int NUM_RESULTS = 30;

    private static Searcher instance = null;

    private LocalDB database;

    private Searcher() {
        try {
            indexWriter = initializeWriter();
            database = LocalDB.getInstance();
            systemLogger.log(Level.INFO, "Successfully initialized index writer.");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Failed to initialize index writer.");
        }
    }

    public static Searcher getSearcher() {
        if (instance == null) {
            instance = new Searcher();
        }
        return instance;
    }

    /**
     * Initializes the index writer in RAM.
     *
     * @return The new Index Writer
     * @throws IOException Error creating a new index writer.
     */
    private IndexWriter initializeWriter() throws IOException {
        return new IndexWriter(ramDirectory, config);
    }


    /**
     * Creates the index document for a patient.
     *
     * @param patient The patient to create the document for
     * @return The Patient Document
     */
    private Document createPatientDocument(Patient patient) {
        Document patientDoc = new Document();
        patientDoc.add(new StringField("nhi", patient.getNhiNumber().toUpperCase(), Field.Store.YES));
        patientDoc.add(new StringField("fName", patient.getFirstName().toUpperCase(), Field.Store.NO));
        if (patient.getMiddleNames() != null) {
            for (String mName : patient.getMiddleNames()) {
                patientDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.NO));
            }
        }
        patientDoc.add(new StringField("lName", patient.getLastName().toUpperCase(), Field.Store.NO));
        patientDoc.add(new StringField("type", UserTypes.PATIENT.getValue(), Field.Store.YES));
        return patientDoc;
    }

    private Document createClinicianDocument(Clinician clinician) {
        Document clinicianDoc = new Document();
        clinicianDoc.add(new StringField("staffid", String.valueOf(clinician.getStaffID()), Field.Store.YES));
        clinicianDoc.add(new StringField("fName", clinician.getFirstName().toUpperCase(), Field.Store.NO));
        if (clinician.getMiddleNames() != null) {
            for (String mName : clinician.getMiddleNames()) {
                clinicianDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.NO));
            }
        }
        clinicianDoc.add(new StringField("lName", clinician.getLastName().toUpperCase(), Field.Store.NO));
        clinicianDoc.add(new StringField("type", UserTypes.CLINICIAN.getValue(), Field.Store.YES));
        return clinicianDoc;
    }

    private Document createAdminDocument(Administrator admin) {
        Document adminDoc = new Document();
        adminDoc.add(new StringField("username", admin.getUsername().toUpperCase(), Field.Store.YES));
        adminDoc.add(new StringField("fName", admin.getFirstName().toUpperCase(), Field.Store.NO));
        if (admin.getMiddleNames() != null) {
            for (String mName : admin.getMiddleNames()) {
                adminDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.NO));
            }
        }
        adminDoc.add(new StringField("lName", admin.getLastName().toUpperCase(), Field.Store.NO));
        adminDoc.add(new StringField("type", UserTypes.ADMIN.getValue(), Field.Store.YES));
        return adminDoc;
    }

    /**
     * Creates a full index of all patients currently loaded into the app.
     */
    public void createFullIndex() {
        Set<Patient> patients = database.getPatients();
        for (Patient patient : patients) {
            addPatientIndex(patient);
        }
        Set<Clinician> clinicians = database.getClinicians();
        for (Clinician clinician : clinicians) {
            addClinicianIndex(clinician);
        }
        Set<Administrator> admins = database.getAdministrators();
        for (Administrator admin : admins) {
            addAdminIndex(admin);
        }
    }

    public void addIndex(Object object) {
        if (object instanceof Patient) {
            addPatientIndex((Patient) object);
        } else if (object instanceof Clinician) {
            addClinicianIndex((Clinician) object);
        } else if (object instanceof Administrator) {
            addAdminIndex((Administrator) object);
        }
    }

    private void addClinicianIndex(Clinician clinician) {
        try {
            indexWriter.addDocument(createClinicianDocument(clinician));
            indexWriter.commit();
            systemLogger.log(Level.FINER, "Added a clinician to the search index");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
        }
    }

    private void addAdminIndex(Administrator admin) {
        try {
            indexWriter.addDocument(createAdminDocument(admin));
            indexWriter.commit();
            systemLogger.log(Level.FINER, "Added an admin to the search index");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
        }
    }

    /**
     * Add indices of patients via the index writer.
     *
     * @param patient patient to be indexed.
     */
    private void addPatientIndex(Patient patient) {
        try {
            indexWriter.addDocument(createPatientDocument(patient));
            indexWriter.commit();
            systemLogger.log(Level.FINER, "Added a patient to the search index");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
        }
    }

    public void updateIndex(User user) {
        removeIndex(user);
        addIndex(user);
    }

    public void removeIndex(User user) {
        Term toDel = null;
        if (user instanceof Patient) {
            toDel = new Term("nhi", ((Patient) user).getNhiNumber().toUpperCase());
        } else if (user instanceof Clinician) {
            toDel = new Term("staffid", String.valueOf(((Clinician) user).getStaffID()));
        } else if (user instanceof Administrator) {
            toDel = new Term("username", ((Administrator) user).getUsername().toUpperCase());
        }

        try {
            indexWriter.deleteDocuments(toDel);
            indexWriter.commit();
            systemLogger.log(Level.FINER, "Successfully removed user from the search index", "Attempted to remove user from the search index");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Unable to remove user index", "Attempted to remove user index");
        }
    }

    /**
     * Removes all indices of all patients via the index writer.
     */
    public void clearIndex() {
        try {
            indexWriter.deleteAll();
            systemLogger.log(Level.INFO, "Successfully cleared patient search index", "Attempted to delete all patients search indices");
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Unable to clear patient index", "Attempted to clear patient index");
        }
    }

    /**
     * Closes the index writer and ram directory freeing up the
     * memory back to the operating system
     *
     * @throws IOException when the index or RAM memory cannot be accessed
     */
    public void closeIndex() throws IOException {
        ramDirectory.close();
        indexWriter.close();
    }

    /**
     * Searches through the index for any results of the given query.
     *
     * @param query FuzzyQuery object of the query to search for.
     * @return The top 20/value of NUM_RESULTS documents in the index.
     * @throws IOException If there is an error reading from the index.
     */
    private TopDocs searchQuery(FuzzyQuery query) throws IOException {
        IndexReader indexReader = DirectoryReader.open(ramDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher.search(query, NUM_RESULTS);
    }

    /**
     * Returns the default list of patient search results.
     *
     * @param filter The filters to match default results on
     * @param types  The user types to select
     * @return The default list of patient search results.
     */
    private Map<Integer, List<User>> getDefaultResults(UserTypes[] types, Map<FilterOption, String> filter) {
        Map<Integer, List<User>> results = new HashMap<>();
        List<UserTypes> typesList = Arrays.asList(types);
        List<User> defaultResults = new ArrayList<>();
        if (typesList.contains(UserTypes.PATIENT)) {
            for (Patient patient : database.getPatients()) {
                if (filter == null) defaultResults.add(patient);
                else if (matchesFilter(patient, filter)) {
                    defaultResults.add(patient);
                }
            }
        }
        if (typesList.contains(UserTypes.CLINICIAN)) {
            defaultResults.addAll(database.getClinicians());
        }
        if (typesList.contains(UserTypes.ADMIN)) {
            defaultResults.addAll(database.getAdministrators());
        }
        defaultResults.sort((o1, o2) -> { // sort by concatenated name
            int comparison;
            comparison = o1.getNameConcatenated()
                    .compareTo(o2.getNameConcatenated());
            return comparison;
        });
        if (defaultResults.size() > 30) {
            defaultResults = new ArrayList<>(defaultResults.subList(0, NUM_RESULTS)); // truncate into size num_results
        }
        results.put(0, defaultResults);
        results.put(1, new ArrayList<>());
        results.put(2, new ArrayList<>());
        return results;
    }

    /**
     * Gets the Patient from the database that the ScoreDoc belongs to.
     *
     * @param doc The ScoreDoc of the Patient.
     * @return The Patient object.
     */
    private Patient fetchPatient(Document doc) {
        String nhi = doc.get("nhi");
        return database.getPatientByNHI(nhi);
    }

    /**
     * Gets the Clinician from the database that the ScoreDoc belongs to.
     *
     * @param doc The ScoreDoc of the Clinician.
     * @return The Clinician object.
     */
    private Clinician fetchClinician(Document doc) {
        int staffID = Integer.valueOf(doc.get("staffid"));
        return database.getClinicianByStaffID(staffID);
    }

    /**
     * Gets the Admin from the database that the ScoreDoc belongs to.
     *
     * @param doc The ScoreDoc of the Admin.
     * @return The Administrator object.
     */
    private Administrator fetchAdmin(Document doc) {
        String username = doc.get("username");
        return database.getAdministratorByUsername(username);
    }

    /**
     * Gets the User from the database that the ScoreDoc belongs to.
     *
     * @param doc The ScoreDoc of the User.
     * @return The User object.
     */
    private User fetchUser(ScoreDoc doc) {
        Document thisDoc;
        try {
            thisDoc = indexSearcher.doc(doc.doc);
            UserTypes type = UserTypes.getEnumFromString(thisDoc.get("type"));
            switch (type) {
                case PATIENT:
                    return fetchPatient(thisDoc);
                case CLINICIAN:
                    return fetchClinician(thisDoc);
                case ADMIN:
                    return fetchAdmin(thisDoc);
            }
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Unable to query search index.", "Attempted to retrieve document from index.");
        }
        return null;
    }

    /**
     * Creates List of FuzzyQuery on the field with the distance, once for each item in criteria.
     *
     * @param field    The index field to search on.
     * @param criteria A String array of search items to search for.
     * @param distance The max character difference a criteria can be away.
     * @return List of FuzzyQuery based off the parameters.
     */
    private List<FuzzyQuery> createQueries(String field, String[] criteria, int distance) {
        List<FuzzyQuery> queries = new ArrayList<>();
        for (String param : criteria) {
            queries.add(new FuzzyQuery(new Term(field, param.toUpperCase()), distance));
        }
        return queries;
    }

    /**
     * Gets a List of ScoreDoc from the search index from the queries that are within the entered UserTypes.
     *
     * @param queries A List of FuzzyQuery objects to query the search index against.
     * @param types   An array of the types of users the search is looking to return.
     * @return List of ScoreDoc returned from running the queries on the search index.
     */
    private List<ScoreDoc> getScoreDocs(List<FuzzyQuery> queries, UserTypes[] types) {
        TopDocs docs;
        List<ScoreDoc> allDocs = new ArrayList<>();
        String typeString;
        UserTypes type;
        try {
            for (FuzzyQuery query : queries) {
                docs = searchQuery(query);
                for (ScoreDoc doc : docs.scoreDocs) {
                    typeString = indexSearcher.doc(doc.doc).get("type");
                    type = UserTypes.getEnumFromString(typeString);
                    if (Arrays.asList(types).contains(type)) {
                        allDocs.add(doc);
                    }
                }
            }
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Unable to query search index.", "Attempted to search for users.");
        }
        return allDocs;
    }

    /**
     * Sorts a List of ScoreDoc objects by their relative score.
     * In the case of a tie they are ordered by concatenated names alphabetically.
     *
     * @param allDocs The List of ScoreDoc objects.
     * @return The sorted List of ScoreDoc objects.
     */
    private List<ScoreDoc> sortScoreDocs(List<ScoreDoc> allDocs) {
        allDocs.sort((o1, o2) -> {
            int comparison = Float.compare(o2.score, o1.score);
            if (comparison == 0) {
                comparison = fetchUser(o1).getNameConcatenated().compareTo(fetchUser(o2).getNameConcatenated());
            }
            return comparison;
        });
        return allDocs;
    }

    /**
     * Creates the users from the ScoreDocs, adds them to a List and returns it.
     *
     * @param allDocs    List of ScoreDocs from the search results.
     * @param numResults The maximum number of results wanted.
     * @return List of the Users created from the ScoreDocs.
     */
    private List<User> createUsers(List<ScoreDoc> allDocs, int numResults) {
        List<User> users = new ArrayList<>();
        User user;
        int docCount = 0;
        int userCount = 0;
        while (docCount < allDocs.size() && userCount < numResults) {
            user = fetchUser(allDocs.get(docCount));
            if (!users.contains(user)) {
                if (users.add(user)) {
                    userCount += 1;
                }
            }
            docCount += 1;
        }
        return users;
    }

    /**
     * Creates the User objects from their relevant ScoreDoc objects.
     *
     * @param allDocs    The List of ScoreDoc objects.
     * @param numResults The maximum number of search results to retrieve.
     * @return A List of User objects.
     */
    @Deprecated
    private List<User> oldCreateUsers(List<ScoreDoc> allDocs, int numResults) {
        List<User> results = new ArrayList<>();
        User user;
        int docCount = 0;
        int userCount = 0;
        while (docCount < allDocs.size() && userCount < numResults) {
            user = fetchUser(allDocs.get(docCount));
            if (!results.contains(user)) {
                results.add(user);
                userCount += 1;
            }
            docCount += 1;
        }
        return results;
    }

    /**
     * New search method for use with the DAO database.
     *
     * @param searchTerm The input user search.
     * @param types      The types of user for the search to find.
     * @param numResults The maximum number of results of the search.
     * @param filter     A optional filter to apply to the search.
     * @return A Map with the distance of the results as the key and a SortedSet of the results, as User objects, as the value.
     */
    public Map<Integer, List<User>> search(String searchTerm, UserTypes[] types, int numResults, Map<FilterOption, String> filter) {
        Map<Integer, List<User>> results = new HashMap<>();

        String[] terms = searchTerm.split(" ");
        List<FuzzyQuery> queries = new ArrayList<>();

        queries.addAll(createQueries("nhi", terms, 0));
        queries.addAll(createQueries("staffid", terms, 0));
        queries.addAll(createQueries("username", terms, 0));

        List<ScoreDoc> allDocs;
        List<User> users;

        int distance = 0;
        while (distance <= 2) {
            queries.addAll(createQueries("fName", terms, distance));
            queries.addAll(createQueries("mName", terms, distance));
            queries.addAll(createQueries("lName", terms, distance));

            allDocs = getScoreDocs(queries, types);
            allDocs = sortScoreDocs(allDocs);
            users = createUsers(allDocs, numResults);
            if (distance > 0) {
                for (int dist = distance - 1; dist >= 0; dist--) {
                    users.removeAll(results.get(dist));
                }
            }
            if (filter != null) {
                users = filterUsers(users, filter);
            }
            results.put(distance, users);
            distance += 1;
        }
        return results;
    }

    /**
     * Searches the search index for the input String.
     *
     * @param input      The String to search by.
     * @param types      The types of users to be included in the search.
     * @param numResults The maximum number of search results to find.
     * @param filter     The filters to match results on
     * @return The search results as a List of User objects.
     */
    @Deprecated
    public List<User> oldSearch(String input, UserTypes[] types, int numResults, Map<FilterOption, String> filter) {
        List<User> results = new ArrayList<>();
        if (input.isEmpty()) {
            if (filter != null) {
                if (numResults < getDefaultResults(types, filter).size()) {
                    return getDefaultResults(types, filter).get(0);
                } else {
                    return getDefaultResults(types, filter).get(0);
                }
            }
            return results;
        }

        String[] params = input.split(" ");
        List<FuzzyQuery> queries = new ArrayList<>();

        queries.addAll(createQueries("fName", params, 2));
        queries.addAll(createQueries("mName", params, 2));
        queries.addAll(createQueries("lName", params, 2));
        queries.addAll(createQueries("nhi", params, 0));
        queries.addAll(createQueries("staffid", params, 0));
        queries.addAll(createQueries("username", params, 0));

        List<ScoreDoc> allDocs = getScoreDocs(queries, types);
        allDocs = sortScoreDocs(allDocs);

        results = oldCreateUsers(allDocs, numResults);

        List<User> filteredResults = new ArrayList<>();
        if (filter != null) {
            for (User result : results) {
                if (matchesFilter((Patient) result, filter)) {
                    filteredResults.add(result);
                }
            }
        } else {
            return results;
        }
        return filteredResults;
    }

    /**
     * Filters a List of Users by set filters.
     *
     * @param users  SortedSet of User objects.
     * @param filter Map of the filter to use.
     * @return List of Users after the filter has been applied.
     */
    private List<User> filterUsers(List<User> users, Map<FilterOption, String> filter) {
        List<User> filtered = new ArrayList<>();
        for (User user : users) {
            if (matchesFilter((Patient) user, filter)) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    /**
     * Check if a patient matches the filter criteria
     *
     * @param patient - patient to check filter against
     * @param filter  - filter to use
     * @return - bool if a match
     */
    private boolean matchesFilter(Patient patient, Map<GlobalEnums.FilterOption, String> filter) {
        if (filter == null) {
            return false;
        }
        for (FilterOption option : filter.keySet()) {
            if (!filter.get(option).equals(NONE_ID)) { //check each fiter entry to see if its been selected
                switch (option) {
                    case REGION: {
                        Region region = Region.getEnumFromString(filter.get(option));
                        if (patient.getRegion() == null || !patient.getRegion().equals(region)) {
                            return false;
                        }
                        break;
                    }
                    case DONATIONS: {
                        Organ donations = Organ.getEnumFromString(filter.get(option));
                        if (patient.getDonations() == null || !patient.getDonations().keySet().contains(donations)) {
                            return false;
                        }
                        break;
                    }
                    case REQUESTEDDONATIONS: {
                        Organ requestedOrgans = Organ.getEnumFromString(filter.get(option));
                        if (patient.getRequiredOrgans() == null || !patient.getRequiredOrgans().containsKey(requestedOrgans)) {
                            return false;
                        }
                        break;
                    }
                    case BIRTHGENDER: {
                        BirthGender birthGender = BirthGender.getEnumFromString(filter.get(option));
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
}