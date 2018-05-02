package utility;

import model.Donor;
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
import service.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class SearchDonors {

    private static Analyzer analyzer = new StandardAnalyzer();

    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private static RAMDirectory ramDirectory = new RAMDirectory();

    private static IndexWriter indexWriter = null;

    private static IndexSearcher indexSearcher = null;

    private static int NUM_RESULTS = 30;


    /**
     * Initializes the index writer in RAM.
     *
     * @exception IOException Error creating a new index writer.
     */
    private static void initializeWriter() throws IOException {
        indexWriter = new IndexWriter(ramDirectory, config);
    }


    /**
     * Creates a full index of all donors currently loaded into the app.
     *
     */
    public static void createFullIndex() {
    	if (indexWriter != null) {
    		SearchDonors.clearIndex();
    	}
        HashSet<Donor> donors = Database.getDonors();
        for (Donor donor : donors) {
        	addIndex(donor);
        }
    }


    /**
     * Add indices of donors via the index writer
     * @param donor donor to be indexed
     */
    public static void addIndex(Donor donor) {
        if (indexWriter == null) {
            try {
				initializeWriter();
			} catch (IOException e) {
				userActions.log(Level.SEVERE, "Failure to initialize index writer", "Attempted to intialize the index writer");
			}
        }
        try {
			indexWriter.addDocument(createDocument(donor));
			indexWriter.commit();
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Failure to write index", "Attempted to write donor to search index");
		}
    }

    /**
     * Removes indices of donors via the index writer
     * @param donor donor to remove index for
     */
    public static void removeIndex(Donor donor) {
    	Term toDel = new Term("nhi", donor.getNhiNumber().toUpperCase());
    	try {
			indexWriter.deleteDocuments(toDel);
            userActions.log(Level.INFO,"Successfully removed donor: " + donor.getNhiNumber() + " from the search index", "Attempted to remove donor " + donor.getNhiNumber() + " from the search index");
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Failure to delete donor: " + donor.getNhiNumber() + " from the search index", "Attempted to remove donor " + donor.getNhiNumber() + " from the search index");
		}
    }
    
    /**
     * Removes all indices of all donors via the index writer
     */
    public static void clearIndex() {
    	try {
			indexWriter.deleteAll();
            userActions.log(Level.INFO,"Successfully cleared donor search index", "Attempted to delete all donors search indices");
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Failure to delete all search indices", "Attempted to delete all donors search indices"); 
		}
    }
    
    /**
     * Creates the index document for a donor
     */
    private static Document createDocument(Donor donor) {
        Document donorDoc = new Document();
        donorDoc.add(new StringField("nhi", donor.getNhiNumber().toUpperCase(), Field.Store.YES));
        donorDoc.add(new StringField("fName", donor.getFirstName().toUpperCase(), Field.Store.YES));
        if (donor.getMiddleNames() != null) {
        	for (String mName : donor.getMiddleNames()) {
        		donorDoc.add(new StringField("mName", mName.toUpperCase(), Field.Store.YES));
        	}
        }
        donorDoc.add(new StringField("lName", donor.getLastName().toUpperCase(), Field.Store.YES));
        return donorDoc;
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
     * Searches through the index for donors by full name
     * @param input The name you want to search for
     * @return ArrayList of the donors it found as a result of the search
     */
    public static ArrayList<Donor> searchByName(String input) {
    	String[] names = input.split(" ");
        ArrayList<Donor> results = new ArrayList<>();
        ArrayList<FuzzyQuery> queries = new ArrayList<>();
        for (String name : names) {
            queries.add(new FuzzyQuery(new Term("fName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("mName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("lName", name.toUpperCase()), 2));
        }

        TopDocs docs;
        Set<ScoreDoc> allDocs = new HashSet<ScoreDoc>();
		try {
			String nhi;
			Donor donor;
			for (FuzzyQuery query : queries) {
				docs = searchQuery(query);
				for (ScoreDoc doc : docs.scoreDocs) {
					allDocs.add(doc);
	        	}
			}
			SortedSet<ScoreDoc> sortedDocs = new TreeSet<ScoreDoc>(new Comparator<ScoreDoc>() {
	            @Override
	            public int compare(ScoreDoc o1, ScoreDoc o2) {
	                return new Float(o2.score).compareTo(o1.score);
	            }
	        });
	        sortedDocs.addAll(allDocs);
			
			for (ScoreDoc doc : sortedDocs) {
				Document thisDoc = indexSearcher.doc(doc.doc);
				nhi = thisDoc.get("nhi");
				donor = Database.getDonorByNhi(nhi);
				if (!results.contains(donor)) {
					results.add(donor);
				}
        	}
            userActions.log(Level.INFO,"Successfully searched for donors with input " + input, "Attempted to search for donors");
		} catch (IOException e) {
			userActions.log(Level.SEVERE, "Failure to find or read from index", "Attempted to search for donors");
		}
        return results;
    }
}