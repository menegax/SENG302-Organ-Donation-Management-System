package controller_test;

import controller.Main;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;
import testfx.FXMedicationHelper;
import testfx.FXNavigation;
import testfx.General;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUIDrugInteractionsTest extends ApplicationTest {

    private Main main = new Main();
    private General general = new General();
    private FXMedicationHelper medicationScreen = new FXMedicationHelper();
    private FXNavigation navigation = new FXNavigation();

    @Override
    public void start( Stage stage ) throws Exception {
        main.start( stage );
    }

    @Before
    public void LoginAndNavigateToMedicationPanel() {
        // Log in to the app
        general.loginDonor("ABC1238");

        // Navigate to the profile panel (where the medication test button is currently found)
        navigation.toProfileFromHomeDonor();
        // Navigate to the medication panel via the temporary test medication button found in profile panel
        navigation.toMedicationsFromProfile();
    }

    @After
    public void waitForEvents() {
        general.waitForEvents();
    }


    @Test
    public void selectOneAndCompare(){
        //register medications
        medicationScreen.registerMedication("Aspirin");
        medicationScreen.registerMedication("Alcohol");
        //select one medication
        compareAndValidateInvalid(new ArrayList<String>(){{
            add("Aspirin");
        }});
    }

    @Test
    public void selectNoneAndCompare(){
        compareAndValidateInvalid(new ArrayList<>());
    }


    private void compareAndValidateInvalid(List<String> drugsToCompare){
        //compare
        medicationScreen.compareDrugs(drugsToCompare);
        //validate the dialog popup
        general.validateAlert("Warning", "Drug interactions not available. Please select 2 medications.");
    }


    @Test
    public void multiSelectCompare(){
        //register medications
        medicationScreen.registerMedication("Aspirin");
        medicationScreen.registerMedication("Alcohol");
        medicationScreen.registerMedication("Codeine");

        //select all medications
        medicationScreen.compareDrugs(new ArrayList<String>() {{
            add("Aspirin");
            add("Alcohol");
            add("Codeine");
        }});
        //validate the dialog popup
        general.validateAlert("Warning", "Drug interactions not available. Please select 2 medications.");
    }


//    @Test
//    public void successfulCompareNoAlert(){
//        //register medications
//        medicationScreen.registerMedication("Aspirin");
//        medicationScreen.registerMedication("Alcohol");
//        //compare
//        medicationScreen.compareDrugs(new ArrayList<String>() {{
//            add("Aspirin");
//            add("Alcohol");
//        }});
//        //no modal stage -> no popup
//        Assert.assertNull(medicationScreen.getTopModalStage());
//    }

    @Test
    public void invalidDrugsCompare(){
        //register medications
        medicationScreen.registerMedication("Notdrug");
        medicationScreen.registerMedication("Notdrug2");

        //compare drugs
        medicationScreen.compareDrugs(new ArrayList<String>() {{
            add("Notdrug");
            add("Notdrug2");
        }});
        //validate the dialog popup
        general.validateAlert("Warning", "Drug interactions not available, either this study has not been completed or" +
                " drugs provided don't exist.");
    }
}
