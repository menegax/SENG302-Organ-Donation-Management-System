package utility;

import static utility.UserActionHistory.userActions;

import model.Patient;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Set;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class SearchPatients {

    private static Analyzer analyzer = new StandardAnalyzer();

    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private static RAMDirectory ramDirectory = new RAMDirectory();

    private static IndexWriter indexWriter = null;

    private static IndexSearcher indexSearcher = null;

    private static int NUM_RESULTS = 30;


    /**
     * Initializes the index writer in RAM.
     * @exception IOException Error creating a new index writer.
     */
    private static void initializeWriter() throws IOException {
        indexWriter = new IndexWriter(ramDirectory, config);
    }


    /**
     * Creates a full index of all patients currently loaded into the app.
     */
    public static void createFullIndex() {
    	if (indexWriter != null) {
    		SearchPatients.clearIndex();
    	}
        Set<Patient> patients = Database.getPatients();
        for (Patient patient : patients) {
        	addIndex(patient);
        }
    }


    /**
     * Add indices of patients via the index writer.
     * @param patient patient to be indexed.
     */
    public static void addIndex(Patient patient) {
        if (indexWriter == null) {
            try {
				initializeWriter();
			} catch (IOException e) {
				userActions.log(Level.SEVERE, "Failure to initialize index writer", "Attempted to intialize the index writer");
			}
        }
        try {
			indexWriter.addDocument(createDocument(patient));
			indexWriter.commit();
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Failure to write index", "Attempted to write patient to search index");
		}
    }

    /**
     * Removes indices of patients via the index writer.
     * @param patient patient to remove index for.
     */
    public static void removeIndex(Patient patient) {
    	Term toDel = new Term("nhi", patient.getNhiNumber().toUpperCase());
    	try {
			indexWriter.deleteDocuments(toDel);
            userActions.log(Level.INFO,"Successfully removed patient: " + patient.getNhiNumber() + " from the search index", "Attempted to remove patient " + patient.getNhiNumber() + " from the search index");
		} catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to remove patient index", "Attempted to remove patient index");
        }
    }
    
    /**
     * Removes all indices of all patients via the index writer.
     */
    public static void clearIndex() {
    	try {
			indexWriter.deleteAll();
            userActions.log(Level.INFO,"Successfully cleared patient search index", "Attempted to delete all patients search indices");
		} catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to clear patient index", "Attempted to clear patient index");
		}
    }
    
    /**
     * Creates the index document for a patient.
     */
    private static Document createDocument(Patient patient) {
        Document patientDoc = new Document();
        patientDoc.add(new StringField("nhi", patient.getNhiNumber().toUpperCase(), Field.Store.YES));
        patientDoc.add(new StringField("fName", patient.getFirstName().toUpperCase(), Field.Store.YES));
        if (patient.getMiddleNames() != null) {
        	for (String mName : patient.getMiddleNames()) {
        		patientDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.YES));
        	}
        }
        patientDoc.add(new StringField("lName", patient.getLastName().toUpperCase(), Field.Store.YES));
        return patientDoc;
    }


    /**
     * Closes the index writer and ram directory freeing up the 
     * memory back to the operating system.
     * @throws IOException when the index or RAM memory cannot be accessed.
     */
    public static void closeIndex() throws IOException {
        ramDirectory.close();
        indexWriter.close();
    }

    /**
     * Searches through the index for any results of the given query.
     * @param query FuzzyQuery object of the query to search for.
     * @return The top 20/value of NUM_RESULTS documents in the index.
     * @throws IOException If there is an error reading from the index.
     */
    private static TopDocs searchQuery(FuzzyQuery query) throws IOException {
        IndexReader indexReader = DirectoryReader.open(ramDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher.search(query, NUM_RESULTS);
    }

    /**
     * Returns the first 30 patients in alphabetical order.
     * @return First 30 patients from a alphabetical ordering.
     */
    public static ArrayList<Patient> getDefaultResults() {   	
    	ArrayList<Patient> array = new ArrayList<Patient>();
    	array.addAll(Database.getPatients());
		Collections.sort(array, new Comparator<Patient>() {
            @Override
            public int compare(Patient o1, Patient o2) {
            	int comparison = 0;
                comparison = o1.getNameConcatenated().compareTo(o2.getNameConcatenated());
                return comparison;
            }
        });
    	array = new ArrayList<Patient>(array.subList(0, NUM_RESULTS));
    	return array;
    }
    
    /**
     * Fetches a patient from the database from its respective score document and returns it.
     * @param doc The patients score document.
     * @return The patient who the score document belongs to.
     * @throws IOException Occurs when index searcher cannot be found.
     */
    private static Patient fetchPatient(ScoreDoc doc) throws IOException {
		Document thisDoc = indexSearcher.doc(doc.doc);
		String nhi = thisDoc.get("nhi");
		return Database.getPatientByNhi(nhi);
    }
    
    /**
     * Searches through the index for patients by full name.
     * @param input The name you want to search for.
     * @return ArrayList of the patients it found as a result of the search.
     */
    public static ArrayList<Patient> searchByName(String input) {

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
        }

        TopDocs docs;
        ArrayList<ScoreDoc> allDocs = new ArrayList<ScoreDoc>();
		try {
			Patient patient;
			for (FuzzyQuery query : queries) {
				docs = searchQuery(query);
				for (ScoreDoc doc : docs.scoreDocs) {
					allDocs.add(doc);
	        	}
			}
			
			Collections.sort(allDocs, new Comparator<ScoreDoc>() {
	            @Override
	            public int compare(ScoreDoc o1, ScoreDoc o2) {
	                int comparison = new Float(o2.score).compareTo(o1.score);
	                if (comparison == 0) {
	                	try {
							comparison = fetchPatient(o1).getNameConcatenated()
									.compareTo(fetchPatient(o2).getNameConcatenated());
						} catch (IOException e) {
				            userActions.log(Level.SEVERE, "Unable to get patient from database", "Attempted to get patient from database");
						}
	                }
	                return comparison;
	            }
	        });
			
			int docCount = 0;
			int patientCount = 0;
			while (docCount < allDocs.size() && patientCount < NUM_RESULTS) {
				patient = fetchPatient(allDocs.get(docCount));
				if (!results.contains(patient)) {
					results.add(patient);
					patientCount += 1;
				}
				docCount += 1;
			}
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Unable to search patients by name", "Attempted to search patients by name");
		}
        return results;
    }
}