package model_test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import controller.ScreenControl;
import model.Donor;
import model.DrugInteraction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

public class DrugInteractionTest {

    //NOTE: internet must be enabled when running these tests
    //TEST might also be flaky, dependant on 3rd party API

    private DrugInteraction drugInteraction;

    @Before
    public void setUp(){
        org.junit.Assume.assumeTrue(makeContactToAPI());
    }

    private boolean makeContactToAPI(){
        ScreenControl.setLoggedInDonor(new Donor("abc1239", "Bob", null, "Bobby", LocalDate.of(1990,9,11)));
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

    private void checkGenderInteractions(JsonArray genderInteractions, HashMap<String, Set<String>> allInteractions){

        for (JsonElement element : genderInteractions) {
            if (!interactionInHashMap(allInteractions, element.getAsString())) {
                Assert.fail(); //fail if gender interaction is not within the hashmap
            }
        }
    }

    @Test
    public void testDonorGenderNull(){
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.MALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkGenderInteractions(maleInteractions, allInteractions);
        checkGenderInteractions(femaleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderMale(){
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.MALE);
        JsonArray maleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.MALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkGenderInteractions(maleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderFemale(){
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.FEMALE);
        JsonArray femaleInteractions = drugInteraction.getGenderInteractionsHelper(GlobalEnums.Gender.FEMALE);
        HashMap<String, Set<String>> allInteractions = drugInteraction.getInteractionsWithDurations();
        checkGenderInteractions(femaleInteractions, allInteractions);
    }

    @Test
    public void testDonorGenderOther() {
        ScreenControl.getLoggedInDonor().setGender(GlobalEnums.Gender.OTHER);
        testDonorGenderNull();
    }

}
