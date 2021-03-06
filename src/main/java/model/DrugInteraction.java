package model;

import service.APIHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import utility.GlobalEnums.BirthGender;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;

public class DrugInteraction {

    private JsonObject response;

    private static Patient viewedPatient;

    public static void setViewedPatient(Patient patient) { viewedPatient = patient; }

    /**
     * Makes a call to the API and records the response
     * @param drugOne - drug to compare with for interactions
     * @param drugTwo - drug to compare with for interactions
     * @param patient - patient taking drugs
     * @throws IOException - bad gateway error thrown
     */
    public DrugInteraction (String drugOne, String drugTwo, Patient patient) throws IOException {
        APIHelper apiHelper = new APIHelper();
        response = apiHelper.getDrugInteractions(drugOne, drugTwo);
        viewedPatient = patient;
    }

    /**
     * Get interactions based on age and gender of the patient
     * @return - set of interactions between the two drugs
     */
    private Set<String> getInteractionsAgeGender(){
        Set<String> allInteractions = new HashSet<>();
        Patient patient = viewedPatient;
        int donorAge = patient.getAge();
        BirthGender donorGender = patient.getBirthGender();
        JsonArray interactionsAgeGroup = getAgeInteractionsHelper(donorAge);
        JsonArray genderInteractions = getGenderInteractionsHelper(donorGender);
        if (genderInteractions != null) {
            genderInteractions.forEach((jsonElement -> allInteractions.add(jsonElement.getAsString())));
        }
        if (interactionsAgeGroup != null) {
           interactionsAgeGroup.forEach((
                   jsonElement -> allInteractions.add(jsonElement.getAsString())));
        }
        return allInteractions;
    }

    /**
     * Helper method to get the interactions based on the donors age
     * @param donorAge - age of the patient
     * @return - JSONArray of the interactions for the given age
     */
    public JsonArray getAgeInteractionsHelper(int donorAge){
        JsonElement ageInteraction =  response.get("age_interaction");
        if (ageInteraction != null) {
            Set<String> ageSets = ageInteraction.getAsJsonObject().keySet(); //get age sets
            JsonArray interactionsAgeGroup = new JsonArray();
            for (String ageSet : ageSets) {
                if (inAgeGroup(donorAge, ageSet)) { //check what age group the patient is in
                    interactionsAgeGroup = ageInteraction.getAsJsonObject().get(ageSet).getAsJsonArray(); //find which age group the current patient is in
                    break;
                }
            }
            return interactionsAgeGroup;
        } else {
            return null;
        }
    }

    /**
     *  Helper method to determine the gender interaction of the patient
     * @param donorGender - Gender of the patient
     * @return - interactions based on the gender of the patient
     */
    public JsonArray getGenderInteractionsHelper( BirthGender donorGender) {
        JsonElement genderInteractions;
        JsonArray gender = new JsonArray();
        if (donorGender == null) { //gender is not set
            gender.addAll(response.get("gender_interaction").getAsJsonObject().get("female").getAsJsonArray());
            gender.addAll(response.get("gender_interaction").getAsJsonObject().get("male").getAsJsonArray());
        } else {
            if (response.get("gender_interaction") != null) {
                genderInteractions = response.get("gender_interaction").getAsJsonObject().get(donorGender.name().toLowerCase());
                gender.addAll(genderInteractions.getAsJsonArray());
            }
        }
        return gender;
    }

    /**
     * Checks if the patient is within a given age group
     * @param donorAge - Age of the patient
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
        }
        if (matches.size() == 0) { // no match found for the patient age
            return false;
        } else {
            return (donorAge >= matches.get(0) && donorAge <= matches.get(1)); //all other age groups
        }
    }

    /**
     *  Gets the interactions and there duration of the symptom
     * @return - HashMap as duration : interaction pairs, interactions are a set
     */
    public HashMap<String, Set<String>> getInteractionsWithDurations() {
        if (!response.toString().equals("{}")) {
            Set<String> interactions = getInteractionsAgeGender();
            HashMap<String, Set<String>> interactionWithDuration = new HashMap<>();
            JsonElement durationInteraction = response.get("duration_interaction").getAsJsonObject();
            Set<String> durationSets = durationInteraction.getAsJsonObject().keySet(); //get duration sets
            for (Object interaction : interactions) {
                String druInteraction = interaction.toString().replaceAll("\"", "");
                boolean inHashMap = false; //keep flag to see if a interaction is within one of the duration categories
                for (String durationSet : durationSets) {
                    for (JsonElement element : ((JsonObject) durationInteraction).get(durationSet).getAsJsonArray()) {
                        String interactUnderDuration = element.getAsString();
                        if (interactUnderDuration.equals(druInteraction)) {
                            if (interactionWithDuration.get(durationSet) == null) { //if duration is not in the hash map already
                                interactionWithDuration.put(durationSet, new HashSet<String>() {{
                                    add(druInteraction);
                                }});
                            } else {
                                interactionWithDuration.get(durationSet).add(druInteraction);
                            }
                            inHashMap = true;
                        }
                    }
                }
                placeUnderNotSpecified(interactionWithDuration, druInteraction, inHashMap);

            }
            return interactionWithDuration;
        } else {
            return null;
        }
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
