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

public class Search {

    private static Analyzer analyzer = new StandardAnalyzer();
    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);
    private static RAMDirectory ramDirectory = new RAMDirectory();
    private static IndexWriter indexWriter = null;
    private static IndexSearcher indexSearcher = null;
    
    private static int NUM_RESULTS = 20;
    
    /**
     * Initializes the index writer in RAM.
     * @throws IOException Error creating a new index writer. 
     */
    private static void initializeWriter() throws IOException {
    	indexWriter = new IndexWriter(ramDirectory, config); 
    }
    
    /**
     * Creates a full index of all donors currently loaded into the app.
     * @throws IOException
     */
    public static void createFullIndex() throws IOException {
    	HashSet<Donor> donors = Database.getDonors();
    	for (Donor donor: donors) {
    		addIndex(donor);
    	}
    }
	
    public static void addIndex(Donor donor) throws IOException {
    	if (indexWriter == null) {
    		initializeWriter();
    	}
    	indexWriter.commit();
    	indexWriter.addDocument(createDocument(donor));
    }
    
    private static Document createDocument(Donor donor) throws IOException {
        Document donorDoc = new Document();
        donorDoc.add(new StringField("nhi", donor.getNhiNumber(), Field.Store.YES));
        donorDoc.add(new StringField("name", donor.getNameConcatenated(), Field.Store.YES));
        return donorDoc;
    }
    
    public static void closeIndex() throws IOException {
    	ramDirectory.close();
    	indexWriter.close();
    }
    
    private static TopDocs searchQuery(FuzzyQuery query) throws IOException {
        IndexReader indexReader = DirectoryReader.open(ramDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher.search(query, NUM_RESULTS);	
    }
    
    public static ArrayList<Donor> searchByName(String name) throws IOException {
    	ArrayList<Donor> results = new ArrayList<Donor>();
        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("name", name));
        TopDocs docs = searchQuery(fuzzyQuery);
        String nhi = null;
        for (ScoreDoc doc : docs.scoreDocs) {
        	Document thisDoc = indexSearcher.doc(doc.doc);
        	nhi = thisDoc.get("nhi");
        	results.add(Database.getDonorByNhi(nhi));
        }
    	return results;
    }
}