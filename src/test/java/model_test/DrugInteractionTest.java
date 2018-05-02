package model_test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import controller.ScreenControl;
import model.Donor;
import model.DrugInteraction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

public class DrugInteractionTest {

    //NOTE: internet must be enabled when running these tests
    //TEST might also be flaky, dependant on 3rd party API

    private static DrugInteraction drugInteraction;

    @BeforeClass
    public static void setUp(){
        org.junit.Assume.assumeTrue(makeContactToAPI());
    }
    @Before
    public void setLoggedInDonor() {
        DrugInteraction.setViewedDonor(new Donor("abc1239", "Bob", null, "Bobby", LocalDate.of(1990,9,11)));
    }

    /**
     * Assumption for being able to get a response from the API
     * @return boolean true if response is received from the API
     */
    private static boolean makeContactToAPI(){
        try {
            drugInteraction = new DrugInteraction("Aspirin", "Alcohol");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Test(expected = IOException.class)
    public void testInvalidDrugsObjectCreation() throws IOException{
       new DrugInteraction("NotADrug", "ClearlyNotAValidDrug");
    }

    @Test
    public void testDonorGenderNull(){
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.MALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(maleInteractions, allInteractions);
        checkInteractions(femaleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderMale(){
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.MALE);
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.MALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(maleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderFemale(){
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.FEMALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(femaleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderOther() {
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.OTHER);
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.MALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkInteractions(maleInteractions, allInteractions);
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
     *  Tests that the donor with the given age has the correct interactions from the API
     * @param donorAge - age of the donor to test
     */
    private void validateAgeInteractions(int donorAge) {
        int yearOfBirth = LocalDate.now().getYear() - donorAge;
        ScreenControl.setLoggedInDonor(new Donor("abc1211", "Bob", null, "Bobby",LocalDate.of(yearOfBirth,1,1)));
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        JsonArray ageInteractions = drugInteraction.getAgeInteractionsHelper(ScreenControl.getLoggedInDonor().getAge());
        checkInteractions(ageInteractions, allInteractions);
    }

}
