package utility;

import static utility.SystemLogger.systemLogger;

import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utility.GlobalEnums.UserTypes;

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

    public Searcher() {
    	try {
			indexWriter = initializeWriter();
			createFullIndex();
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
     * @throws IOException Error creating a new index writer.
     */
    private IndexWriter initializeWriter() throws IOException {
        return new IndexWriter(ramDirectory, config);
    }


    /**
     * Creates the index document for a patient.
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
        if (patient.getBirthGender() != null) {
            patientDoc.add(new StringField("birthGender", patient.getBirthGender().toString().toUpperCase(), Field.Store.NO));
        }
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
        Set<Patient> patients = Database.getPatients();
        for (Patient patient : patients) {
            addPatientIndex(patient);
        }
        Set<Clinician> clinicians = Database.getClinicians();
        for (Clinician clinician : clinicians) {
        	addClinicianIndex(clinician);
        }
        Set<Administrator> admins = Database.getAdministrators();
        for (Administrator admin : admins) {
        	addAdminIndex(admin);
        }
    }

    public void addIndex(Object object) {
    	if (object instanceof Patient) {
    		addPatientIndex((Patient)object);
    	} else if (object instanceof Clinician) {
    		addClinicianIndex((Clinician)object);
    	} else if (object instanceof Administrator) {
    		addAdminIndex((Administrator)object);
    	}
    }
    
    private void addClinicianIndex(Clinician clinician) {
        try {
            indexWriter.addDocument(createClinicianDocument(clinician));
            indexWriter.commit();
        } catch (IOException e) {
            UserActionHistory.userActions.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
        }
    }
    
    private void addAdminIndex(Administrator admin) {
        try {
            indexWriter.addDocument(createAdminDocument(admin));
            indexWriter.commit();
        } catch (IOException e) {
            UserActionHistory.userActions.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
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
        } catch (IOException e) {
            UserActionHistory.userActions.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
        }
    }


    public  void removeIndex(User user) {
    	Term toDel = null;
    	if (user instanceof Patient) {
    		toDel = new Term("nhi", ((Patient)user).getNhiNumber().toUpperCase());
    	} else if (user instanceof Clinician) {
    		toDel = new Term("staffid", String.valueOf(((Clinician)user).getStaffID()));
    	} else if (user instanceof Administrator) {
    		toDel = new Term("username", ((Administrator)user).getUsername().toUpperCase());
    	}
        
        try {
            indexWriter.deleteDocuments(toDel);
            UserActionHistory.userActions.log(Level.INFO, "Successfully removed user from the search index", "Attempted to remove user from the search index");
        } catch (IOException e) {
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to remove user index", "Attempted to remove user index");
        }
    }

    /**
     * Removes all indices of all patients via the index writer.
     */
    public  void clearIndex() {
        try {
            indexWriter.deleteAll();
            UserActionHistory.userActions.log(Level.INFO, "Successfully cleared patient search index", "Attempted to delete all patients search indices");
        } catch (IOException e) {
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to clear patient index", "Attempted to clear patient index");
        }
    }

    /**
     * Closes the index writer and ram directory freeing up the
     * memory back to the operating system
     * @throws IOException when the index or RAM memory cannot be accessed
     */
    public  void closeIndex() throws IOException {
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
     * Returns the default set of patient search results.
     * @return The default set of patient search results.
     */
    public List<User> getDefaultResults(UserTypes[] types) {
    	List<UserTypes> typesList = Arrays.asList(types);
    	List<User> defaultResults = new ArrayList<User>();
    	if (typesList.contains(UserTypes.PATIENT)) {
    		defaultResults.addAll(Database.getPatients());
    	}
    	if (typesList.contains(UserTypes.CLINICIAN)) {
    		defaultResults.addAll(Database.getClinicians());
    	}    	
    	if (typesList.contains(UserTypes.ADMIN)) {
    		defaultResults.addAll(Database.getAdministrators());
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
        return defaultResults;
    }

    /**
     * Gets the Patient from the database that the ScoreDoc belongs to.
     * @param doc The ScoreDoc of the Patient.
     * @return The Patient object.
     * @throws InvalidObjectException
     */
    private Patient fetchPatient(Document doc) throws InvalidObjectException {
        String nhi = doc.get("nhi");
        return Database.getPatientByNhi(nhi);
    }
    
    /**
     * Gets the Clinician from the database that the ScoreDoc belongs to.
     * @param doc The ScoreDoc of the Clinician.
     * @return The Clinician object.
     * @throws InvalidObjectException
     */
    private Clinician fetchClinician(Document doc) throws InvalidObjectException {
    	int staffID = Integer.valueOf(doc.get("staffid"));
    	return Database.getClinicianByID(staffID);
    }
    
    /**
     * Gets the Admin from the database that the ScoreDoc belongs to.
     * @param doc The ScoreDoc of the Admin.
     * @return The Administrator object.
     * @throws InvalidObjectException This does not occur.
     */
    private Administrator fetchAdmin(Document doc) throws InvalidObjectException {
    	String username = doc.get("username");
    	return Database.getAdministratorByUsername(username);
    }
    
    /**
     * Gets the User from the database that the ScoreDoc belongs to.
     * @param doc The ScoreDoc of the User.
     * @return The User object.
     */
    private User fetchUser(ScoreDoc doc) {
    	Document thisDoc;
		try {
			thisDoc = indexSearcher.doc(doc.doc);
	    	UserTypes type = UserTypes.getEnumFromString(thisDoc.get("type"));
	    	switch(type) {
	    	case PATIENT:
	    		return fetchPatient(thisDoc);
	    	case CLINICIAN:
	    		return fetchClinician(thisDoc);
	    	case ADMIN:
	    		return fetchAdmin(thisDoc);
	    	default:
	        	return null;
	    	}
		} catch (IOException e) {
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to query search index.", "Attempted to retrieve document from index.");
		}
		return null;
    }
    
    /**
     * Creates List of FuzzyQuery on the field with the distance, once for each item in criteria.
     * @param field The index field to search on.
     * @param criteria A String array of search items to search for.
     * @param distance The max character difference a criteria can be away.
     * @return List of FuzzyQuery based off the parameters.
     */
    private List<FuzzyQuery> createQueries(String field, String[] criteria, int distance) {
    	List<FuzzyQuery> queries = new ArrayList<FuzzyQuery>();
    	for (String param : criteria) {
    		queries.add(new FuzzyQuery(new Term(field, param.toUpperCase()), distance));
    	}
    	return queries;
    }
    
    /**
     * Gets a List of ScoreDoc from the search index from the queries that are within the entered UserTypes.
     * @param queries A List of FuzzyQuery objects to query the search index against.
     * @param types An array of the types of users the search is looking to return.
     * @return List of ScoreDoc returned from running the queries on the search index.
     */
    private List<ScoreDoc> getScoreDocs(List<FuzzyQuery> queries, UserTypes[] types) {
    	TopDocs docs;
        List<ScoreDoc> allDocs = new ArrayList<ScoreDoc>();
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
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to query search index.", "Attempted to search for users.");
        }
	    return allDocs;
    }
    
    /**
     * Sorts a List of ScoreDoc objects by their relative score. 
     * In the case of a tie they are ordered by concatenated names alphabetically.
     * @param allDocs The List of ScoreDoc objects.
     * @return The sorted List of ScoreDoc objects.
     */
    private List<ScoreDoc> sortScoreDocs(List<ScoreDoc> allDocs) {
        Collections.sort(allDocs, new Comparator<ScoreDoc>() {
            @Override
            public int compare(ScoreDoc o1, ScoreDoc o2) {
                int comparison = new Float(o2.score).compareTo(o1.score);
                if (comparison == 0) {
                    comparison = fetchUser(o1).getNameConcatenated()
                            .compareTo(fetchUser(o2).getNameConcatenated());
                }
                return comparison;
            }
        });
        return allDocs;
    }
    
    /**
     * Creates the User objects from their relevant ScoreDoc objects.
     * @param allDocs The List of ScoreDoc objects.
     * @param numResults The maximum number of search results to retrieve.
     * @return A List of User objects.
     */
    private List<User> createUsers(List<ScoreDoc> allDocs, int numResults) {
    	List<User> results = new ArrayList<User>();
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
     * Searches the search index for the input String.
     * @param input The String to search by.
     * @param types The types of users to be included in the search.
     * @param numResults The maximum number of search results to find.
     * @return The search results as a List of User objects. 
     */
    public List<User> search(String input, UserTypes[] types, int numResults) {
        List<User> results = new ArrayList<>();
    	if (input.isEmpty()) {
            return results;
        }

        String[] params = input.split(" ");
        List<FuzzyQuery> queries = new ArrayList<>();
        
        queries.addAll(createQueries("fName", params, 2));
        queries.addAll(createQueries("mName", params, 2));
        queries.addAll(createQueries("lName", params, 2));
        queries.addAll(createQueries("birthGender", params, 2));
        queries.addAll(createQueries("nhi", params, 0));
        queries.addAll(createQueries("staffid", params, 0));
        queries.addAll(createQueries("username", params, 0)); 

        List<ScoreDoc> allDocs = getScoreDocs(queries, types);
        allDocs = sortScoreDocs(allDocs);

        results = createUsers(allDocs, numResults);
       
        return results;
    }
}