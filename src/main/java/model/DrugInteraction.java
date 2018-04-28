package model;

import api.APIHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.ScreenControl;
import utility.GlobalEnums;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrugInteraction {

    private JsonObject response;

    /**
     * Makes a call to the API and records the response
     * @param drugOne - drug to compare with for interactions
     * @param drugTwo - drug to compare with for interactions
     * @throws IOException - bad gateway error thrown
     */
    public DrugInteraction (String drugOne, String drugTwo) throws IOException {
        APIHelper apiHelper = new APIHelper();
        response = apiHelper.getDrugInteractions(drugOne, drugTwo);
    }


    /**
     *  Get interactions based on age and gender of the donor
     * @return - set of interactions between the two drugs
     */
    private Set getInteractionsAgeGender(){
        Donor donor = ScreenControl.getLoggedInDonor();
        int donorAge = donor.getAge();
        GlobalEnums.Gender donorGender = donor.getGender();
        JsonElement ageInteraction =  response.get("age_interaction");
        Set<String> ageSets = ageInteraction.getAsJsonObject().keySet(); //get age sets
        JsonElement interactionsAgeGroup = null;
        for (String ageSet: ageSets) {
            if (inAgeGroup(donorAge, ageSet)){ //check what age group the donor is in
                interactionsAgeGroup = ageInteraction.getAsJsonObject().get(ageSet); //find which age group the current donor is in
                break;
            }
        }
        JsonArray interactions = new JsonArray();
        interactions.addAll(Objects.requireNonNull(interactionsAgeGroup != null ? interactionsAgeGroup.getAsJsonArray() : null));
        JsonArray genderInteractions = getGenderInteractionsHelper(donorGender);
        if (genderInteractions != null) {
            interactions.addAll(genderInteractions);
        }
        Set<String> interactionNoDuplicates = new HashSet<>();
        interactions.forEach((jsonElement -> interactionNoDuplicates.add(jsonElement.toString()))); //remove duplicates
        return interactionNoDuplicates;
    }


    /**
     *  Helper method to determine the gender interaction of the donor
     * @param donorGender - Gender of the donor
     * @return - interactions based on the gender of the donor
     */
    private JsonArray getGenderInteractionsHelper(GlobalEnums.Gender donorGender) {
        if (donorGender != null){//get the correct gender interaction
            JsonElement genderInteractions = donorGender != GlobalEnums.Gender.OTHER ?
                    response.get("gender_interaction").getAsJsonObject().get(donorGender.name().toLowerCase()): response.get("gender_interaction");
            JsonArray gender = new JsonArray();
            if (donorGender == GlobalEnums.Gender.OTHER) {
                gender.addAll(genderInteractions.getAsJsonObject().get("female").getAsJsonArray());
                gender.addAll(genderInteractions.getAsJsonObject().get("male").getAsJsonArray());
            } else {
                gender.addAll(genderInteractions.getAsJsonArray());
            }
            return gender;
        }
        return null;
    }

    /**
     * Checks if the donor is within a given age group
     * @param donorAge - Age of the donor
     * @param ageSet - Age set e.g. "10-19"
     * @return - boolean
     */
    private boolean inAgeGroup(int donorAge, String ageSet){
        Pattern pattern = Pattern.compile("\\d{2}");
        Matcher matcher = pattern.matcher(ageSet);
        ArrayList<Integer> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(Integer.parseInt(matcher.group()));
        }
        if (matches.size() == 1 && donorAge >= matches.get(0)) { //60+
            return true;
        } else return donorAge >= matches.get(0) && donorAge <= matches.get(1); //all other age groups
    }

    /**
     *  Gets the interactions and there duration of the symptom
     * @return - HashMap as duration : interaction pairs, interactions are a set
     */
    public HashMap<String, Set<String>> getInteractionsWithDurations() {
        Set interactions = getInteractionsAgeGender();
        HashMap<String, Set<String>> interactionWithDuration = new HashMap<>();
        JsonElement durationInteraction = response.get("duration_interaction").getAsJsonObject();
        Set<String> durationSets = durationInteraction.getAsJsonObject().keySet(); //get duration sets
        for (Object interaction : interactions) {
            boolean inHashMap = false; //keep flag to see if a interaction is within one of the duration categories
            for (String durationSet : durationSets) {
                for (JsonElement element : ((JsonObject) durationInteraction).get(durationSet).getAsJsonArray()) {
                    String interactUnderDuration = element.getAsString();
                    if (interactUnderDuration.equals(interaction.toString().replaceAll("\"", ""))) {
                        if (interactionWithDuration.get(durationSet) == null) { //if duration is not in the hash map already
                            interactionWithDuration.put(durationSet, new HashSet<String>(){{
                                add(interaction.toString());}});
                        } else {
                            interactionWithDuration.get(durationSet).add(interaction.toString());
                        }
                        inHashMap = true;
                    }
                }
            }
            placeUnderNotSpecified(interactionWithDuration, interaction, inHashMap);

        }
        return interactionWithDuration;
    }


    /**
     * Places given interaction under not specified duration if it does not fall under any other duration
     * @param interactionWithDuration - Duration interaction hashmap
     * @param interaction - interaction to place into the hashmap
     * @param inHashMap - flag to determine if the interaction is within the hashmap already
     */
    private void placeUnderNotSpecified(HashMap<String, Set<String>> interactionWithDuration,
                                              Object interaction, boolean inHashMap ){

        if (!inHashMap) { //put all interactions that do not have a duration as not specified
            if (interactionWithDuration.get("not specified") != null) {
                interactionWithDuration.get("not specified").add(interaction.toString());
            } else {
                interactionWithDuration.put("not specified", new HashSet<String>(){{
                    add(interaction.toString());}});
            }
        }
    }
}
