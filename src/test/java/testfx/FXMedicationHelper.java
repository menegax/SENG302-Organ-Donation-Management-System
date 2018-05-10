package testfx;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.util.List;

import static org.testfx.api.FxAssert.verifyThat;

public class FXMedicationHelper extends General {

    /**
     * Helper method to enter a new medication into the medications field
     */
    public void registerMedication(String query){
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( query ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( query ) ) );
        clickButton("#registerMed");
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel after registration
    }

    /**
     * Removes a medication from current to past on the medication screen
     * @param medication - medication to remove
     */
    public void removeMedication(String medication){
        clickMatchingText(medication);
        clickButton("#removeMed");
    }


    /**
     * Adds a medication from past to current on the medication screen
     * @param medication - medication to add
     */
    public void addMedication(String medication) {
        clickMatchingText(medication);
        clickButton("#addMed");
    }

    /**
     * Deletes a medication
     * @param medication - medication to delete
     */
    public void deleteMedication(String medication){
        clickMatchingText(medication);
        clickButton("#deleteMed");
    }


    /**
     *  Delete all deleted medications
     * @param medications - List containing medication strings to delete
     */
    public void deleteMedicationMultiSelect(List<String> medications){
        multiSelectListView(medications);
        clickButton("#deleteMed");
    }

    /**
     * Save the medications
     */
    public void saveMedication(){
        clickButton("#saveMed");
    }


    /**
     *
     */
    public void compareDrugs(List<String> drugs){
        multiSelectListView(drugs);
        clickButton("#compareMeds");
    }


}
