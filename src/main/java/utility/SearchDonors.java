package utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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

import model.Donor;
import service.Database;

public class SearchDonors {

    private static Analyzer analyzer = new StandardAnalyzer();

    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private static RAMDirectory ramDirectory = new RAMDirectory();

    private static IndexWriter indexWriter = null;

    private static IndexSearcher indexSearcher = null;

    // TODO what is this? it just causes a warning on my machine.
    //@SuppressWarnings("FieldCanBeLocal")
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
     * Creates a full index of all donors currently loaded into the app.
     *
     */
    public static void createFullIndex() {
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
				// TODO add error for failure to initialize index writer
			}
        }
        try {
			indexWriter.commit();
			indexWriter.addDocument(createDocument(donor));
		} catch (IOException e) {
			// TODO add error for failure to write to index
		}
    }

    /**
     * Removes indices of donors via the index writer
     * @param donor donor to remove index for
     */
    public static void removeIndex(Donor donor) {
    	Term toDel = new Term("nhi", donor.getNhiNumber());
    	try {
			indexWriter.deleteDocuments(toDel);
		} catch (IOException e) {
			// TODO add error for failure to delete index
		}
    }
    
    /**
     * Removes all indices of all donors via the index writer
     */
    public static void clearIndex() {
    	try {
			indexWriter.deleteAll();
		} catch (IOException e) {
			// TODO add error for failure to delete all indices 
		}
    }
    
    /**
     * Creates the index document for a donor
     */
    private static Document createDocument(Donor donor) {
        Document donorDoc = new Document();
        donorDoc.add(new StringField("nhi", donor.getNhiNumber(), Field.Store.YES));
        donorDoc.add(new StringField("name", donor.getNameConcatenated(), Field.Store.YES));
        return donorDoc;
    }


    /**
     * Closes the index writer and ram directory freeing up the 
     * memory back to the operating system
     * @throws IOException
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
     * @param name The name you want to search for
     * @return ArrayList of the donors it found as a result of the search
     */
    public static ArrayList<Donor> searchByName(String name) {
        ArrayList<Donor> results = new ArrayList<>();
        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("name", name));
        TopDocs docs;
		try {
			docs = searchQuery(fuzzyQuery);
	        String nhi;
	        for (ScoreDoc doc : docs.scoreDocs) {
	            Document thisDoc = indexSearcher.doc(doc.doc);
	            nhi = thisDoc.get("nhi");
	            results.add(Database.getDonorByNhi(nhi));
	        }
		} catch (IOException e) {
			// TODO add error for unable to find or read from index
		}
        return results;
    }
}