package utility;

import static utility.UserActionHistory.userActions;

import model.Patient;
import utility.GlobalEnums.*;

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
import java.util.*;

import java.util.logging.Level;

public class SearchPatients {

    private static Analyzer analyzer = new StandardAnalyzer();

    private static IndexWriterConfig config = new IndexWriterConfig(analyzer);

    private static RAMDirectory ramDirectory = new RAMDirectory();

    private static IndexWriter indexWriter = null;

    private static IndexSearcher indexSearcher = null;

    private static int NUM_RESULTS = 30;


    /**
     * Initializes the index writer in RAM.
     *
     * @throws IOException Error creating a new index writer.
     */
    private static void initializeWriter() throws IOException {
        indexWriter = new IndexWriter(ramDirectory, config);
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
     *
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
     *
     * @param patient patient to remove index for.
     */
    public static void removeIndex(Patient patient) {
        Term toDel = new Term("nhi", patient.getNhiNumber().toUpperCase());
        try {
            indexWriter.deleteDocuments(toDel);
            userActions.log(Level.INFO, "Successfully removed patient: " + patient.getNhiNumber() + " from the search index", "Attempted to remove patient " + patient.getNhiNumber() + " from the search index");
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
            userActions.log(Level.INFO, "Successfully cleared patient search index", "Attempted to delete all patients search indices");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to clear patient index", "Attempted to clear patient index");
        }
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
     * Searches through the index for any results of the given query.
     *
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
     * Returns the first num_results patients in alphabetical order.
     *
     * @return First num_results patients from a alphabetical ordering.
     */
    public static ArrayList<Patient> getDefaultResults() {
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
    private static Patient fetchPatient(ScoreDoc doc) throws IOException {
        Document thisDoc = indexSearcher.doc(doc.doc);
        String nhi = thisDoc.get("nhi");
        return Database.getPatientByNhi(nhi);
    }

    /**
     * Searches through the index for patients by full name.
     *
     * @param inputName The name you want to search for.
     * @return ArrayList of the patients it found as a result of the search.
     */
    public static ArrayList<Patient> searchByName(String inputName, Map<FilterOption, String> filter) {
        ArrayList<Patient> results = new ArrayList<>();
        if (inputName.isEmpty()) {
            for (Patient patient : getDefaultResults()) {
                if (matchesFilter(patient, filter)) {
                    results.add(patient);
                } else if (noFilterSelected(filter)) {
                    results.add(patient);
                }
            }
            return results;
        }

        String[] names = inputName.split(" ");

        ArrayList<FuzzyQuery> queries = new ArrayList<>();
        for (String name : names) {
            queries.add(new FuzzyQuery(new Term("fName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("mName", name.toUpperCase()), 2));
            queries.add(new FuzzyQuery(new Term("lName", name.toUpperCase()), 2));
        }

        TopDocs docs;
        ArrayList<ScoreDoc> allDocs = new ArrayList<>();
        try {
            Patient patient;
            for (FuzzyQuery query : queries) {
                docs = searchQuery(query);
                allDocs.addAll(Arrays.asList(docs.scoreDocs));
            }

            allDocs.sort((o1, o2) -> {
                int comparison = Float.compare(o2.score, o1.score);
                if (comparison == 0) {
                    try {
                        comparison = fetchPatient(o1).getNameConcatenated().compareTo(fetchPatient(o2).getNameConcatenated());
                    } catch (IOException e) {
                        userActions.log(Level.SEVERE, "Unable to get patient from database", "Attempted to get patient from database");
                    }
                }
                return comparison;
            });

            int docCount = 0;
            int patientCount = 0;
            while (docCount < allDocs.size() && patientCount < NUM_RESULTS) {
                patient = fetchPatient(allDocs.get(docCount));
                if (!results.contains(patient)) {
                    if (matchesFilter(patient, filter)) {
                        results.add(patient);
                        patientCount += 1;
                    } else if (noFilterSelected(filter)) {
                        results.add(patient);
                        patientCount += 1;
                    }
                }
                docCount += 1;
            }
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to search patients by name", "Attempted to search patients by name");
        }
        return results;
    }


    private static boolean matchesFilter(Patient patient, Map<FilterOption, String> filter) {
        if (filter == null) {
            return false;
        }
        if (!filter.get(GlobalEnums.FilterOption.REGION).equals(GlobalEnums.NONE_ID)) {
            Region region = Region.getEnumFromString(filter.get(FilterOption.REGION));
            if (patient.getRegion() == null || !patient.getRegion().equals(region)) {
                return false;
            }
        }
        if (!filter.get(GlobalEnums.FilterOption.DONATIONS).equals(GlobalEnums.NONE_ID)) {
            Organ donatingOrgan = Organ.getEnumFromString(filter.get(FilterOption.DONATIONS));
            if (patient.getDonations() == null || !patient.getDonations().contains(donatingOrgan)) return false;
        }
        if (!filter.get(GlobalEnums.FilterOption.REQUESTEDDONATIONS).equals(GlobalEnums.NONE_ID)) {
            Organ requestedOrgans = Organ.getEnumFromString(filter.get(FilterOption.REQUESTEDDONATIONS));
            if (patient.getRequiredOrgans() == null || !patient.getRequiredOrgans().contains(requestedOrgans))
                return false;
        }
        if (!filter.get(FilterOption.BIRTHGENDER).equals(GlobalEnums.NONE_ID)) {
            BirthGender birthGender = BirthGender.getEnumFromString(filter.get(FilterOption.BIRTHGENDER));
            if (patient.getBirthGender() == null || !patient.getBirthGender().equals(birthGender)) return false;
        }
        if (Boolean.valueOf(filter.get(FilterOption.RECIEVER)).equals(true)
                && patient.getRequiredOrgans().size() == 0) {
            return false;
        }
        if (Boolean.valueOf(filter.get(FilterOption.DONOR)).equals(true)
                && patient.getDonations().size() == 0) {
            return false;
        }


            if (patient.getAge() > Integer.parseInt(filter.get(FilterOption.AGEUPPER))
                    || patient.getAge() < Integer.parseInt(filter.get(FilterOption.AGELOWER))) {
                return false;
            }

        return true;
    }

    private static boolean noFilterSelected(Map filter){
        if (filter == null) {
            return false;
        }
        for (Object value : filter.values()) {
            if (value != null && !value.toString().equals(GlobalEnums.NONE_ID) && !value.equals("0") && !value.equals("100")) {
                return false;
            }
        }
        return true;
    }
}