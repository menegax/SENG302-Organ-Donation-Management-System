package gui_test.redoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the donor profile update screen
 */
public class GUIRedoDonorUpdateTest extends ApplicationTest{

    private Main main = new Main();

    /**
     * Launches the main application
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABC1238");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#editButton").queryAs(Button.class).fire();
        });
    }

    /**
     * Sets the widgets on the screen to known values before testing
     */
    @Before
    public void setFields() {
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("AAA1111");
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName");
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName");
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName");
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test Street");
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test Street");
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb");
            lookup("#regionTxt").queryAs(TextField.class).setText("Region");
            lookup("#zipTxt").queryAs(TextField.class).setText("0001");
            lookup("#weightTxt").queryAs(TextField.class).setText("50");
            lookup("#heightTxt").queryAs(TextField.class).setText("2");

            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(0);

            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2001, 1, 1));

            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(false);
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).setSelected(false);
            lookup("#genderOtherRadio").queryAs(RadioButton.class).setSelected(false);
        });
    }

    /**
     * Waits for all events to finish before moving on
     */
    @After
    public void waitForEvents() {
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
     * Tests that all possible TextFields can redo successfully
     */
    @Test
    public void redoTextFields() {
        // Check redo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test2 Street");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test2 Street");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#regionTxt").queryAs(TextField.class).setText("Region2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#zipTxt").queryAs(TextField.class).setText("0002");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#weightTxt").queryAs(TextField.class).setText("52");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#heightTxt").queryAs(TextField.class).setText("2.2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameTxt").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#lastnameTxt").queryAs(TextField.class).getText().equals("LastName2"));
        assertThat(lookup("#middlenameTxt").queryAs(TextField.class).getText().equals("MiddleName2"));
        assertThat(lookup("#street1Txt").queryAs(TextField.class).getText().equals("1 Test2 Street"));
        assertThat(lookup("#street2Txt").queryAs(TextField.class).getText().equals("2 Test2 Street"));
        assertThat(lookup("#suburbTxt").queryAs(TextField.class).getText().equals("Suburb2"));
        assertThat(lookup("#regionTxt").queryAs(TextField.class).getText().equals("Region2"));
        assertThat(lookup("#zipTxt").queryAs(TextField.class).getText().equals("0002"));
        assertThat(lookup("#weightTxt").queryAs(TextField.class).getText().equals("52"));
        assertThat(lookup("#heightTxt").queryAs(TextField.class).getText().equals("2.2"));

        // Check Ctrl Y next (with both ways of release)
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC2222");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName3");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName3");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test3 Street");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test3 Street");
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(Y).release(CONTROL);
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb3");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#regionTxt").queryAs(TextField.class).setText("Region3");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#zipTxt").queryAs(TextField.class).setText("0003");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#weightTxt").queryAs(TextField.class).setText("53");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#heightTxt").queryAs(TextField.class).setText("2.3");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        assertThat(lookup("#firstnameTxt").queryAs(TextField.class).getText().equals("FirstName3"));
        assertThat(lookup("#lastnameTxt").queryAs(TextField.class).getText().equals("LastName3"));
        assertThat(lookup("#middlenameTxt").queryAs(TextField.class).getText().equals("MiddleName3"));
        assertThat(lookup("#street1Txt").queryAs(TextField.class).getText().equals("1 Test3 Street"));
        assertThat(lookup("#street2Txt").queryAs(TextField.class).getText().equals("2 Test3 Street"));
        assertThat(lookup("#suburbTxt").queryAs(TextField.class).getText().equals("Suburb3"));
        assertThat(lookup("#regionTxt").queryAs(TextField.class).getText().equals("Region3"));
        assertThat(lookup("#zipTxt").queryAs(TextField.class).getText().equals("0003"));
        assertThat(lookup("#weightTxt").queryAs(TextField.class).getText().equals("53"));
        assertThat(lookup("#heightTxt").queryAs(TextField.class).getText().equals("2.3"));
    }

    /**
     * Tests that all possible ChoiceBoxes can redo successfully
     */
    @Test
    public void redoChoiceBoxes() {
        // Check redo button first
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);

        // Check Ctrl Y next
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
    }

    /**
     * Tests that all possible DatePickers can redo successfully
     */
    @Test
    public void redoDatePickers() {
        // Check redo button first
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));

        // Check Ctrl Y next
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
    }

    /**
     * Tests that all possible RadioButtons can redo successfully
     */
    @Test
    public void redoRadioButtons() {
        // Check redo button first
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#genderOtherRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        assertThat(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        assertThat(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#genderFemaleRadio").queryAs(RadioButton.class).setSelected(true);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#genderOtherRadio").queryAs(RadioButton.class).setSelected(true);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        assertThat(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        assertThat(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());
    }

    /**
     * Tests that all possible widgets can redo multiple times in a row
     */
    @Test
    public void redoMultipleTimes() {
        // Check redo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class).setText("DDD4444");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 3);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2004, 4, 4));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(false);
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiTxt").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiTxt").queryAs(TextField.class).setText("DDD4444");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 3);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2004, 4, 4));

        interact(() -> {
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(false);
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
    }

    /**
     * Tests that multiple different widgets can redo after each other
     */
    @Test
    public void redoOverMultipleWidgets() {
        // Check redo button first
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#genderMaleRadio").queryAs(RadioButton.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 0);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        assertThat(!lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertThat(lookup("#dobDate").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        assertThat(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
    }
}
