package gui_test.undoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.scene.input.KeyCode.*;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the patient profile update screen
 */
@Ignore //Todo
public class GUIUndoPatientUpdateTest extends ApplicationTest{

    private Main main = new Main();
    private String nhiTxtDefault;
    private String firstnameTxtDefault;
    private String lastnameTxtDefault;
    private String middlenameTxtDefault;
    private String street1TxtDefault;
    private String street2TxtDefault;
    private String suburbTxtDefault;
    private String zipTxtDefault;
    private String weightTxtDefault;
    private String heightTxtDefault;

    private int bloodGroupDDDefault;
    private int regionDDDefault;

    private LocalDate dobDateDefault;
    private LocalDate dateOfDeathDefault;

    private boolean genderMaleRadioDefault;
    private boolean genderFemaleRadioDefault;
    private boolean genderOtherRadioDefault;

    //todo rework

    /**
     * Launches the main application
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#editPatientButton").queryAs(Button.class).fire();
        });
    }

    /**
     * Gets the values of the widgets on the screen before testing
     */
    @Before
    public void getFields() {
        interact(() -> {
            while (lookup("OK").queryAs(Button.class) != null) {
                lookup("OK").queryAs(Button.class).fire();
            }
            nhiTxtDefault = lookup("#nhiTxt").queryAs(TextField.class).getText();
            firstnameTxtDefault = lookup("#firstnameTxt").queryAs(TextField.class).getText();
            lastnameTxtDefault = lookup("#lastnameTxt").queryAs(TextField.class).getText();
            middlenameTxtDefault = lookup("#middlenameTxt").queryAs(TextField.class).getText();
            street1TxtDefault = lookup("#street1Txt").queryAs(TextField.class).getText();
            street2TxtDefault = lookup("#street2Txt").queryAs(TextField.class).getText();
            suburbTxtDefault = lookup("#suburbTxt").queryAs(TextField.class).getText();
            zipTxtDefault = lookup("#zipTxt").queryAs(TextField.class).getText();
            weightTxtDefault = lookup("#weightTxt").queryAs(TextField.class).getText();
            heightTxtDefault = lookup("#heightTxt").queryAs(TextField.class).getText();

            bloodGroupDDDefault = lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex();
            regionDDDefault = lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex();

            dobDateDefault = lookup("#dobDate").queryAs(DatePicker.class).getValue();
            dateOfDeathDefault = lookup("#dateOfDeath").queryAs(DatePicker.class).getValue();

            genderMaleRadioDefault = lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected();
            genderFemaleRadioDefault = lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected();
            genderOtherRadioDefault = lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected();
        });
    }

    /**
     * Waits for all events to finish before moving on
     */
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Tests that we have navigated to the right screen
     */
    @Test
    public void verifyScreen() {
        verifyThat("#patientUpdateAnchorPane", Node::isVisible);
    }

    /**
     * Tests that all possible TextFields can undo successfully
     */
    @Test
    public void undoTextFields() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName2");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName2");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test2 Street");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test2 Street");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb2");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#zipTxt").queryAs(TextField.class).setText("0002");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#weightTxt").queryAs(TextField.class).setText("52");
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#heightTxt").queryAs(TextField.class).setText("2.2");
            lookup("#undoButton").queryAs(Button.class).fire();

        });

        assertEquals(nhiTxtDefault, lookup("#nhiTxt").queryAs(TextField.class).getText());
        assertEquals(firstnameTxtDefault, lookup("#firstnameTxt").queryAs(TextField.class).getText());
        assertEquals(lastnameTxtDefault, lookup("#lastnameTxt").queryAs(TextField.class).getText());
        assertEquals(middlenameTxtDefault, lookup("#middlenameTxt").queryAs(TextField.class).getText());
        assertEquals(street1TxtDefault, lookup("#street1Txt").queryAs(TextField.class).getText());
        assertEquals(street2TxtDefault, lookup("#street2Txt").queryAs(TextField.class).getText());
        assertEquals(suburbTxtDefault, lookup("#suburbTxt").queryAs(TextField.class).getText());
        assertEquals(zipTxtDefault, lookup("#zipTxt").queryAs(TextField.class).getText());
        assertEquals(weightTxtDefault, lookup("#weightTxt").queryAs(TextField.class).getText());
        assertEquals(heightTxtDefault, lookup("#heightTxt").queryAs(TextField.class).getText());

        // Check Ctrl Z next (with both ways of release)
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC2222");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName3");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName3");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test3 Street");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test3 Street");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb3");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#zipTxt").queryAs(TextField.class).setText("0003");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#weightTxt").queryAs(TextField.class).setText("53");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#heightTxt").queryAs(TextField.class).setText("2.3");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(nhiTxtDefault, lookup("#nhiTxt").queryAs(TextField.class).getText());
        assertEquals(firstnameTxtDefault, lookup("#firstnameTxt").queryAs(TextField.class).getText());
        assertEquals(lastnameTxtDefault, lookup("#lastnameTxt").queryAs(TextField.class).getText());
        assertEquals(middlenameTxtDefault, lookup("#middlenameTxt").queryAs(TextField.class).getText());
        assertEquals(street1TxtDefault, lookup("#street1Txt").queryAs(TextField.class).getText());
        assertEquals(street2TxtDefault, lookup("#street2Txt").queryAs(TextField.class).getText());
        assertEquals(suburbTxtDefault, lookup("#suburbTxt").queryAs(TextField.class).getText());
        assertEquals(zipTxtDefault, lookup("#zipTxt").queryAs(TextField.class).getText());
        assertEquals(weightTxtDefault, lookup("#weightTxt").queryAs(TextField.class).getText());
        assertEquals(heightTxtDefault, lookup("#heightTxt").queryAs(TextField.class).getText());
    }

    /**
     * Tests that all possible ChoiceBoxes can undo successfully
     */
    @Test
    public void undoChoiceBoxes() {
        // Check undo button first
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#undoButton").queryAs(Button.class).fire();

            lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#undoButton").queryAs(Button.class).fire();
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == regionDDDefault);

        // Check Ctrl Z next
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == regionDDDefault);
    }

    /**
     * Tests that all possible DatePickers can undo successfully
     */
    @Test
    public void undoDatePickers() {
        // Check undo button first
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#dateOfDeath").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(dateOfDeathDefault, lookup("#dateOfDeath").queryAs(DatePicker.class).getValue());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#dateOfDeath").queryAs(DatePicker.class).setValue(LocalDate.of(2005, 5, 5));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(dateOfDeathDefault, lookup("#dateOfDeath").queryAs(DatePicker.class).getValue());
    }

    /**
     * Tests that all possible RadioButtons can undo successfully
     */
    @Test
    public void undoRadioButtons() {
        // Check undo button first
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#genderOtherRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderOtherRadio").queryAs(RadioButton.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        assertEquals(genderFemaleRadioDefault, lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        assertEquals(genderOtherRadioDefault, lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).fire();
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#genderOtherRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderOtherRadio").queryAs(RadioButton.class).fire();
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        assertEquals(genderFemaleRadioDefault, lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        assertEquals(genderOtherRadioDefault, lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());
    }

    /**
     * Tests that all possible widgets can undo multiple times in a row
     */
    @Test
    public void undoMultipleTimes() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class).setText("DDD4444");
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2003, 3, 3)));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2002, 2, 2)));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).fire();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class).setText("DDD4444");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2003, 3, 3)));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2002, 2, 2)));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).fire();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
    }

    /**
     * Tests that multiple different widgets can undo after each other
     */
    @Test
    public void undoOverMultipleWidgets() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();

        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class).requestFocus();
            lookup("#genderMaleRadio").queryAs(RadioButton.class).fire();

            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
    }
}
