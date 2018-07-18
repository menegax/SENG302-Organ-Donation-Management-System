package utility;

import  utility.UserActionHistory;
import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import service.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utility.GlobalEnums.UserTypes;

import java.util.Set;
import java.util.logging.Level;

public class SearchPatients {

    private Analyzer analyzer = new StandardAnalyzer();

    private IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private RAMDirectory ramDirectory = new RAMDirectory();

    private IndexWriter indexWriter = null;

    private IndexSearcher indexSearcher = null;

    private int NUM_RESULTS = 30;
    
    private static SearchPatients instance = null;

    public SearchPatients() {
    	try {
			indexWriter = initializeWriter();
            UserActionHistory.userActions.log(Level.INFO, "Successfully initialized index writer.", "Attempted to initialize index writer.");
		} catch (IOException e) {
            UserActionHistory.userActions.log(Level.SEVERE, "Failure to initialize index writer.", "Attempted to initialize index writer.");
		}
    }
    
    public static SearchPatients getSearcher() {
    	if (instance == null) {
    		instance = new SearchPatients();
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
            patientDoc.add(new StringField("birthGender", patient.getBirthGender().toString().toUpperCase(), Field.Store.YES));
        }
        patientDoc.add(new StringField("type", UserTypes.PATIENT.getValue(), Field.Store.YES));
        return patientDoc;
    }

    private Document createClinicianDocument(Clinician clinician) {
    	Document clinicianDoc = new Document();
    	clinicianDoc.add(new StringField("staffid", String.valueOf(clinician.getStaffID()), Field.Store.YES));
    	clinicianDoc.add(new StringField("fname", clinician.getFirstName().toUpperCase(), Field.Store.NO));
        if (clinician.getMiddleNames() != null) {
            for (String mName : clinician.getMiddleNames()) {
                clinicianDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.NO));
            }
        }
        clinicianDoc.add(new StringField("lname", clinician.getLastName().toUpperCase(), Field.Store.NO));
        clinicianDoc.add(new StringField("type", UserTypes.CLINICIAN.getValue(), Field.Store.YES));
        return clinicianDoc;
    }

    private Document createAdminDocument(Administrator admin) {
    	Document adminDoc = new Document();
    	adminDoc.add(new StringField("username", admin.getUsername(), Field.Store.YES));
    	adminDoc.add(new StringField("fname", admin.getFirstName().toUpperCase(), Field.Store.NO));
        if (admin.getMiddleNames() != null) {
            for (String mName : admin.getMiddleNames()) {
                adminDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.NO));
            }
        }
        adminDoc.add(new StringField("lname", admin.getLastName().toUpperCase(), Field.Store.NO));
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

    /**
     * Removes indices of patients via the index writer.
     *
     * @param patient patient to remove index for.
     */
    public  void removeIndex(Patient patient) {
        Term toDel = new Term("nhi", patient.getNhiNumber().toUpperCase());
        try {
            indexWriter.deleteDocuments(toDel);
            UserActionHistory.userActions.log(Level.INFO, "Successfully removed patient: " + patient.getNhiNumber() + " from the search index", "Attempted to remove patient " + patient.getNhiNumber() + " from the search index");
        } catch (IOException e) {
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to remove patient index", "Attempted to remove patient index");
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
     * Returns the first num_results patients in alphabetical order.
     *
     * @return First num_results patients from a alphabetical ordering.
     */
    public ArrayList<Patient> getDefaultResults() {
        ArrayList<Patient> default_patients = new ArrayList<>(Database.getPatients());
        default_patients.sort((o1, o2) -> { // sort by concatenated name
            int comparison;
            comparison = o1.getNameConcatenated()
                    .compareTo(o2.getNameConcatenated());
            return comparison;
        });
        if (default_patients.size() > 30) {
            default_patients = new ArrayList<>(default_patients.subList(0, NUM_RESULTS)); // truncate into size num_results
        }
        return default_patients;
    }

    /**
     * Fetches a patient from the database from its respective score document and returns it.
     *
     * @param doc The patients score document.
     * @return The patient who the score document belongs to.
     * @throws IOException Occurs when index searcher cannot be found.
     */
    private Patient fetchPatient(Document doc) {
        String nhi = doc.get("nhi");
        return Database.getPatientByNhi(nhi);
    }
    
    private Clinician fetchClinician(Document doc) {
    	int staffID = Integer.valueOf(doc.get("staffid"));
    	return Database.getClinicianByID(staffID);
    }
    
    private Administrator fetchAdmin(Document doc) {
    	String username = doc.get("username");
    	return Database.getAdministratorByUsername(username);
    }
    
    private User fetchUser(ScoreDoc doc) {
    	Document thisDoc = indexSearcher.doc(doc.doc);
    	UserTypes type = UserTypes.getEnumFromString(thisDoc.get("type"));
    	switch(type) {
    	case PATIENT:
    		return fetchPatient(thisDoc);
    	case CLINICIAN:
    		return fetchClinician(thisDoc);
    	case ADMIN:
    		return fetchAdmin(thisDoc);
    	}
    }
    
    /**
     * Searches through the index for patients by full name.
     *
     * @param input The name you want to search for.
     * @return ArrayList of the patients it found as a result of the search.
     */
    public ArrayList<Patient> searchPatientByName(String input) {

        if (input.isEmpty()) {
            return getDefaultResults();
        }

        String[] names = input.split(" ");

        ArrayList<Patient> results = new ArrayList<>();
        ArrayList<FuzzyQuery> queries = new ArrayList<>();
        for (String name : names) {
        	
            queries.add(new FuzzyQuery(new Term("fName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("mName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("lName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("birthGender", name.toUpperCase()), 0));
        }

        TopDocs docs;
        ArrayList<ScoreDoc> allDocs = new ArrayList<ScoreDoc>();
        try {
            Patient patient;
            for (FuzzyQuery query : queries) {
                docs = searchQuery(query);
                for (ScoreDoc doc : docs.scoreDocs) {
                	if (indexSearcher.doc(doc.doc).get("type").equals(UserTypes.PATIENT.getValue())) {
                		allDocs.add(doc);
                	}       
                }
            }

            Collections.sort(allDocs, new Comparator<ScoreDoc>() {
                @Override
                public int compare(ScoreDoc o1, ScoreDoc o2) {
                    int comparison = new Float(o2.score).compareTo(o1.score);
                    if (comparison == 0) {
                        try {
                            comparison = ((Patient)fetchUser(o1)).getNameConcatenated()
                                    .compareTo(((Patient)fetchUser(o2)).getNameConcatenated());
                        } catch (IOException e) {
                        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to get patient from database", "Attempted to get patient from database");
                        }
                    }
                    return comparison;
                }
            });

            int docCount = 0;
            int patientCount = 0;
            while (docCount < allDocs.size() && patientCount < NUM_RESULTS) {
                patient = (Patient)fetchUser(allDocs.get(docCount));
                if (!results.contains(patient)) {
                    results.add(patient);
                    patientCount += 1;
                }
                docCount += 1;
            }
        } catch (IOException e) {
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to search patients by name", "Attempted to search patients by name");
        }
        return results;
    }
    
    private List<FuzzyQuery> createQueries(String field, String[] criteria, int distance) {
    	List<FuzzyQuery> queries = new ArrayList<FuzzyQuery>();
    	for (String param : criteria) {
    		queries.add(new FuzzyQuery(new Term(field, param), distance));
    	}
    	return queries;
    }
    
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
        	UserActionHistory.userActions.log(Level.SEVERE, "Unable to get patient from database", "Attempted to search for users.");
        }
	    return allDocs;
    }
    
    private List<ScoreDoc> sortScoreDocs(List<ScoreDoc> allDocs) {
        Collections.sort(allDocs, new Comparator<ScoreDoc>() {
            @Override
            public int compare(ScoreDoc o1, ScoreDoc o2) {
                int comparison = new Float(o2.score).compareTo(o1.score);
                if (comparison == 0) {
                    try {
                        comparison = fetchUser(o1).getNameConcatenated()
                                .compareTo(fetchUser(o2).getNameConcatenated());
                    } catch (IOException e) {
                    	UserActionHistory.userActions.log(Level.SEVERE, "Unable to get patient from database", "Attempted to get patient from database");
                    }
                }
                return comparison;
            }
        });
        return allDocs;
    }
    
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
     * Searches through the index for patients by full name.
     *
     * @param input The name you want to search for.
     * @return ArrayList of the patients it found as a result of the search.
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
        queries.addAll(createQueries("nhi", params, 0));
        queries.addAll(createQueries("staffid", params, 0));
        queries.addAll(createQueries("username", params, 0)); 

        List<ScoreDoc> allDocs = getScoreDocs(queries, types);
        allDocs = sortScoreDocs(allDocs);

        results = createUsers(allDocs, numResults);
       
        return results;
    }
}