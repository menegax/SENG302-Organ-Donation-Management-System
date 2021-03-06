package model_test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import model.Patient;
import model.DrugInteraction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums.BirthGender;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class DrugInteractionTest {

    //NOTE: internet must be enabled when running these tests
    //TEST might also be flaky, dependant on 3rd party API. Tests don't run without making successful contact

    private static DrugInteraction drugInteraction;
    private static Patient patient;

    @BeforeClass
    public static void setUp(){
        userActions.setLevel(Level.OFF);
        patient = new Patient("abc1239", "Bob", null, "Bobby", LocalDate.of(1990,9,11));
        DrugInteraction.setViewedPatient(patient);
        org.junit.Assume.assumeTrue(makeContactToAPI());
    }

    /**
     * Assumption for being able to get a response from the API
     * @return boolean true if response is received from the API
     */
    private static boolean makeContactToAPI(){
        try {
            drugInteraction = new DrugInteraction("Aspirin", "Alcohol", patient);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Test(expected = IOException.class)
    public void testInvalidDrugsObjectCreation() throws IOException{
       new DrugInteraction("NotADrug", "ClearlyNotAValidDrug", patient);
    }

    @Test
    public void testDonorGenderMale(){
        patient.setBirthGender(BirthGender.MALE);
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(BirthGender.MALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(maleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderFemale(){
        patient.setBirthGender(BirthGender.FEMALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(BirthGender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(femaleInteractions, allInteractions);
    }

    @Test
    public void testDonorAgeLessThanTen() {
        validateAgeInteractions(1);
    }

    @Test
    public void testDonorAgeEqualTen() {
        validateAgeInteractions(10);
    }

    @Test
    public void testDonorAgeEqualNineteen() {
        validateAgeInteractions(19);
    }

    @Test
    public void testDonorAgeEqualOneHundred() {
        validateAgeInteractions(100);
    }

    /**
     * Checks if the given interaction is found within the given calculated interactions
     * @param allInteractions - all interactions calculated
     * @param interaction - interaction to find
     * @return - boolean true if interaction is within the interaction hashmap
     */
    private boolean interactionInHashMap(HashMap<String, Set<String>> allInteractions, String interaction){
        for (String key : allInteractions.keySet()) {
            if (allInteractions.get(key).contains(interaction)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given interactions are with in the hashmap
     * @param interactions - interactions expected in the hashmap
     * @param allInteractions - calculated interactions
     */
    private void checkInteractions(JsonArray interactions, HashMap<String, Set<String>> allInteractions){
        for (JsonElement element : interactions) {
            if (!interactionInHashMap(allInteractions, element.getAsString())) {
                Assert.fail(); //fail if gender interaction is not within the hashmap
            }
        }
    }

    /**
     *  Tests that the patient with the given age has the correct interactions from the API
     * @param donorAge - age of the patient to test
     */
    private void validateAgeInteractions(int donorAge) {
        int yearOfBirth = LocalDate.now().getYear() - donorAge;
        DrugInteraction.setViewedPatient(new Patient("abc1211", "Bob", null, "Bobby",LocalDate.of(yearOfBirth,1,1)));
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        JsonArray ageInteractions = drugInteraction.getAgeInteractionsHelper(donorAge);
        checkInteractions(ageInteractions, allInteractions);
    }

}
