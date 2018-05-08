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
import java.util.HashSet;
import java.util.logging.Level;

public class SearchPatients {

    private static Analyzer analyzer = new StandardAnalyzer();

    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private static RAMDirectory ramDirectory = new RAMDirectory();

    private static IndexWriter indexWriter = null;

    private static IndexSearcher indexSearcher = null;

    private static int NUM_RESULTS = 20;


    /**
     * Initializes the index writer in RAM.
     *
     * @exception IOException Error creating a new index writer.
     */
    private static void initializeWriter() throws IOException {
        indexWriter = new IndexWriter(ramDirectory, config);
    }


    /**
     * Creates a full index of all patients currently loaded into the app.
     *
     */
    public static void createFullIndex() {
    	if (indexWriter != null) {
    		SearchPatients.clearIndex();
    	}
        HashSet<Patient> patients = Database.getPatients();
        for (Patient patient : patients) {
        	addIndex(patient);
        }
    }


    /**
     * Add indices of patients via the index writer
     * @param patient patient to be indexed
     */
    public static void addIndex(Patient patient) {
        if (indexWriter == null) {
            try {
				initializeWriter();
			} catch (IOException e) {
                userActions.log(Level.SEVERE, "Unable to add patient index", "Attempted to add patient index");
            }
        }
        try {
			indexWriter.commit();
			indexWriter.addDocument(createDocument(patient));
		} catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to add patient index", "Attempted to add patient index");
		}
    }

    /**
     * Removes indices of patients via the index writer
     * @param patient patient to remove index for
     */
    public static void removeIndex(Patient patient) {
    	Term toDel = new Term("nhi", patient.getNhiNumber().toUpperCase());
    	try {
			indexWriter.deleteDocuments(toDel);
		} catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to remove patient index", "Attempted to remove patient index");
        }
    }
    
    /**
     * Removes all indices of all patients via the index writer
     */
    public static void clearIndex() {
    	try {
			indexWriter.deleteAll();
		} catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to clear patient index", "Attempted to clear patient index");
		}
    }
    
    /**
     * Creates the index document for a patient
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
     * memory back to the operating system
     * @throws IOException when the index or RAM memory cannot be accessed
     */
    public static void closeIndex() throws IOException {
        ramDirectory.close();
        indexWriter.close();
    }

    /**
     * Searches through the index for any results of the given query
     * @param query FuzzyQuery object of the query to search for
     * @return The top 20/value of NUM_RESULTS documents in the index 
     * @throws IOException If there is an error reading from the index
     */
    private static TopDocs searchQuery(FuzzyQuery query) throws IOException {
        IndexReader indexReader = DirectoryReader.open(ramDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher.search(query, NUM_RESULTS);
    }

    /**
     * Searches through the index for patients by full name
     * @param input The name you want to search for
     * @return ArrayList of the patients it found as a result of the search
     */
    public static ArrayList<Patient> searchByName(String input) {
    	String[] names = input.split(" ");
        ArrayList<Patient> results = new ArrayList<>();
        ArrayList<FuzzyQuery> queries = new ArrayList<>();
        for (String name : names) {
            queries.add(new FuzzyQuery(new Term("fName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("mName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("lName", name.toUpperCase()), 2));
        }

        TopDocs docs;
		try {
			String nhi;
			Patient patient;
			for (FuzzyQuery query : queries) {
				docs = searchQuery(query);
	        
				for (ScoreDoc doc : docs.scoreDocs) {
	            	Document thisDoc = indexSearcher.doc(doc.doc);
	            	nhi = thisDoc.get("nhi");
	            	patient = Database.getPatientByNhi(nhi);
	            	if (!results.contains(patient)) {
	            		results.add(patient);
	            	}
	        	}
			}
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Unable to search patients by name", "Attempted to search patients by name");
		}
        return results;
    }

}