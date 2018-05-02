package gui_test.undoScreenTests;

import controller.Main;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.scene.input.KeyCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the donor profile update screen
 */
public class GUIUndoPatientUpdateTest extends ApplicationTest {

    private Main main = new Main();


    /**
     * Launches the main application
     *
     * @param stage the stage to launch the app on
     * @exception Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {

        // add dummy donor
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatients(new Patient("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class)
                    .fire();
            lookup("#profileButton").queryAs(Button.class)
                    .fire();
            lookup("#editPatientButton").queryAs(Button.class)
                    .fire();
        });
    }


    /**
     * Sets the widgets on the screen to known values before testing
     */
    @Before
    public void setFields() {
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("AAA1111");
            lookup("#firstnameTxt").queryAs(TextField.class)
                    .setText("FirstName");
            lookup("#lastnameTxt").queryAs(TextField.class)
                    .setText("LastName");
            lookup("#middlenameTxt").queryAs(TextField.class)
                    .setText("MiddleName");
            lookup("#street1Txt").queryAs(TextField.class)
                    .setText("1 Test Street");
            lookup("#street2Txt").queryAs(TextField.class)
                    .setText("2 Test Street");
            lookup("#suburbTxt").queryAs(TextField.class)
                    .setText("Suburb");
            lookup("#regionDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#zipTxt").queryAs(TextField.class)
                    .setText("0001");
            lookup("#weightTxt").queryAs(TextField.class)
                    .setText("50");
            lookup("#heightTxt").queryAs(TextField.class)
                    .setText("2");

            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(0);

            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2001, 1, 1));

            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(false);
            lookup("#genderFemaleRadio").queryAs(RadioButton.class)
                    .setSelected(false);
            lookup("#genderOtherRadio").queryAs(RadioButton.class)
                    .setSelected(false);
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
        verifyThat("#donorUpdateAnchorPane", Node::isVisible);
    }


    /**
     * Tests that all possible TextFields can undo successfully
     */
    @Test
    public void undoTextFields() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#firstnameTxt").queryAs(TextField.class)
                    .setText("FirstName2");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#lastnameTxt").queryAs(TextField.class)
                    .setText("LastName2");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#middlenameTxt").queryAs(TextField.class)
                    .setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#street1Txt").queryAs(TextField.class)
                    .setText("1 Test2 Street");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#street2Txt").queryAs(TextField.class)
                    .setText("2 Test2 Street");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#suburbTxt").queryAs(TextField.class)
                    .setText("Suburb2");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#zipTxt").queryAs(TextField.class)
                    .setText("0002");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#weightTxt").queryAs(TextField.class)
                    .setText("52");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#heightTxt").queryAs(TextField.class)
                    .setText("2.2");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });

        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));
        assertThat(lookup("#firstnameTxt").queryAs(TextField.class)
                .getText()
                .equals("FirstName"));
        assertThat(lookup("#lastnameTxt").queryAs(TextField.class)
                .getText()
                .equals("LastName"));
        assertThat(lookup("#middlenameTxt").queryAs(TextField.class)
                .getText()
                .equals("MiddleName"));
        assertThat(lookup("#street1Txt").queryAs(TextField.class)
                .getText()
                .equals("1 Test Street"));
        assertThat(lookup("#street2Txt").queryAs(TextField.class)
                .getText()
                .equals("2 Test Street"));
        assertThat(lookup("#suburbTxt").queryAs(TextField.class)
                .getText()
                .equals("Suburb"));
        assertThat(lookup("#zipTxt").queryAs(TextField.class)
                .getText()
                .equals("0001"));
        assertThat(lookup("#weightTxt").queryAs(TextField.class)
                .getText()
                .equals("50"));
        assertThat(lookup("#heightTxt").queryAs(TextField.class)
                .getText()
                .equals("2"));

        // Check Ctrl Z next (with both ways of release)
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("CCC2222");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#firstnameTxt").queryAs(TextField.class)
                    .setText("FirstName3");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#lastnameTxt").queryAs(TextField.class)
                    .setText("LastName3");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#middlenameTxt").queryAs(TextField.class)
                    .setText("MiddleName2");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#street1Txt").queryAs(TextField.class)
                    .setText("1 Test3 Street");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#street2Txt").queryAs(TextField.class)
                    .setText("2 Test3 Street");
            press(CONTROL).press(Z)
                    .release(Z)
                    .release(CONTROL);
            lookup("#suburbTxt").queryAs(TextField.class)
                    .setText("Suburb3");
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            lookup("#zipTxt").queryAs(TextField.class)
                    .setText("0003");
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            lookup("#weightTxt").queryAs(TextField.class)
                    .setText("53");
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            lookup("#heightTxt").queryAs(TextField.class)
                    .setText("2.3");
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });

        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));
        assertThat(lookup("#firstnameTxt").queryAs(TextField.class)
                .getText()
                .equals("FirstName"));
        assertThat(lookup("#lastnameTxt").queryAs(TextField.class)
                .getText()
                .equals("LastName"));
        assertThat(lookup("#middlenameTxt").queryAs(TextField.class)
                .getText()
                .equals("MiddleName"));
        assertThat(lookup("#street1Txt").queryAs(TextField.class)
                .getText()
                .equals("1 Test Street"));
        assertThat(lookup("#street2Txt").queryAs(TextField.class)
                .getText()
                .equals("2 Test Street"));
        assertThat(lookup("#suburbTxt").queryAs(TextField.class)
                .getText()
                .equals("Suburb"));
        assertThat(lookup("#zipTxt").queryAs(TextField.class)
                .getText()
                .equals("0001"));
        assertThat(lookup("#weightTxt").queryAs(TextField.class)
                .getText()
                .equals("50"));
        assertThat(lookup("#heightTxt").queryAs(TextField.class)
                .getText()
                .equals("2"));
    }


    /**
     * Tests that all possible ChoiceBoxes can undo successfully
     */
    @Test
    public void undoChoiceBoxes() {
        // Check undo button first
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#regionDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(2);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });

        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#regionDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .isSelected(0));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(2);
            lookup("#regionDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(3);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });

        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#regionDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .isSelected(0));
    }


    /**
     * Tests that all possible DatePickers can undo successfully
     */
    @Test
    public void undoDatePickers() {
        // Check undo button first
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });

        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });

        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
    }


    /**
     * Tests that all possible RadioButtons can undo successfully
     */
    @Test
    public void undoRadioButtons() {
        // Check undo button first
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
            lookup("#genderOtherRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });

        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        assertThat(!lookup("#genderFemaleRadio").queryAs(RadioButton.class)
                .isSelected());
        assertThat(!lookup("#genderOtherRadio").queryAs(RadioButton.class)
                .isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            lookup("#genderFemaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
            lookup("#genderOtherRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });

        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        assertThat(!lookup("#genderFemaleRadio").queryAs(RadioButton.class)
                .isSelected());
        assertThat(!lookup("#genderOtherRadio").queryAs(RadioButton.class)
                .isSelected());
    }


    /**
     * Tests that all possible widgets can undo multiple times in a row
     */
    @Test
    public void undoMultipleTimes() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("DDD4444");
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("CCC3333"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(3);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 2);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(false);
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("DDD4444");
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(3);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 2);
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(false);
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
    }


    /**
     * Tests that multiple different widgets can undo after each other
     */
    @Test
    public void undoOverMultipleWidgets() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2002, 2, 2));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class)
                    .fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class)
                    .setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                    .getSelectionModel()
                    .select(1);
            lookup("#dobDate").queryAs(DatePicker.class)
                    .setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class)
                    .setSelected(true);
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2002, 2, 2));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
        interact(() -> {
            press(CONTROL).press(Z)
                    .release(CONTROL)
                    .release(Z);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class)
                .getText()
                .equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class)
                .getSelectionModel()
                .getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class)
                .getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class)
                .isSelected());
    }
}
