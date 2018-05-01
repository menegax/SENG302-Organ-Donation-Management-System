package gui_test.redoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the donor profile update screen
 */
public class GUIRedoDonorUpdateTest extends ApplicationTest{

    private Main main = new Main();
    private double undoX;
    private double undoY;
    private double redoX;
    private double redoY;
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

    private double radioY;
    private double maleRadioX;
    private double femaleRadioX;
    private double otherRadioX;

    /**
     * Launches the main application
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {

        // add dummy donor
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addDonor(new Donor("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));

        main.start(stage);
        interact(() -> {
            stage.setFullScreen(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#editDonorButton").queryAs(Button.class).fire();
        });
        undoX = lookup("#undoButton").queryAs(Button.class).getLayoutX() + 240;
        undoY = lookup("#undoButton").queryAs(Button.class).getLayoutY() + 28;
        redoX = lookup("#redoButton").queryAs(Button.class).getLayoutX() + 330;
        redoY = lookup("#redoButton").queryAs(Button.class).getLayoutY() + 28;
        radioY = lookup("#genderMaleRadio").queryAs(RadioButton.class).getLayoutY() + 180;
        maleRadioX = lookup("#genderMaleRadio").queryAs(RadioButton.class).getLayoutX() + 75;
        femaleRadioX = lookup("#genderFemaleRadio").queryAs(RadioButton.class).getLayoutX() + 110;
        otherRadioX = lookup("#genderOtherRadio").queryAs(RadioButton.class).getLayoutX() + 140;
    }

    /**
     * Gets the values of the widgets on the screen before testing
     */
    @Before
    public void getFields() {
        interact(() -> {
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
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));

        interact(() -> {
        lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName2");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#firstnameTxt").queryAs(TextField.class).getText().equals("FirstName2"));

        interact(() -> {
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName2");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#lastnameTxt").queryAs(TextField.class).getText().equals("LastName2"));

        interact(() -> {
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#middlenameTxt").queryAs(TextField.class).getText().equals("MiddleName2"));

        interact(() -> {
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test2 Street");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#street1Txt").queryAs(TextField.class).getText().equals("1 Test2 Street"));

        interact(() -> {
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test2 Street");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#street2Txt").queryAs(TextField.class).getText().equals("2 Test2 Street"));

        interact(() -> {
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb2");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#suburbTxt").queryAs(TextField.class).getText().equals("Suburb2"));

        interact(() -> {
            lookup("#zipTxt").queryAs(TextField.class).setText("0002");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#zipTxt").queryAs(TextField.class).getText().equals("0002"));

        interact(() -> {
            lookup("#weightTxt").queryAs(TextField.class).setText("52");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#weightTxt").queryAs(TextField.class).getText().equals("52"));

        interact(() -> {
            lookup("#heightTxt").queryAs(TextField.class).setText("2.2");
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertEquals("2.2", lookup("#heightTxt").queryAs(TextField.class).getText());

        // Check Ctrl Y next (with both ways of release)
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));

        interact(() -> {
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#firstnameTxt").queryAs(TextField.class).getText().equals("FirstName2"));

        interact(() -> {
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#lastnameTxt").queryAs(TextField.class).getText().equals("LastName2"));

        interact(() -> {
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#middlenameTxt").queryAs(TextField.class).getText().equals("MiddleName2"));

        interact(() -> {
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test2 Street");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#street1Txt").queryAs(TextField.class).getText().equals("1 Test2 Street"));

        interact(() -> {
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test2 Street");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#street2Txt").queryAs(TextField.class).getText().equals("2 Test2 Street"));

        interact(() -> {
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#suburbTxt").queryAs(TextField.class).getText().equals("Suburb2"));

        interact(() -> {
            lookup("#zipTxt").queryAs(TextField.class).setText("0002");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#zipTxt").queryAs(TextField.class).getText().equals("0002"));

        interact(() -> {
            lookup("#weightTxt").queryAs(TextField.class).setText("52");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#weightTxt").queryAs(TextField.class).getText().equals("52"));

        interact(() -> {
            lookup("#heightTxt").queryAs(TextField.class).setText("2.2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals("2.2", lookup("#heightTxt").queryAs(TextField.class).getText());
    }

    /**
     * Tests that all possible ChoiceBoxes can redo successfully
     */
    @Test
    public void redoChoiceBoxes() {
        // Check redo button first
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
            lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertTrue(lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);

        // Check Ctrl Y next
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        assertTrue(lookup("#regionDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
    }

    /**
     * Tests that all possible DatePickers can redo successfully
     */
    @Test
    public void redoDatePickers() {
        // Check redo button first
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
            lookup("#dateOfDeath").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });

        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(LocalDate.of(2004, 4, 4), lookup("#dateOfDeath").queryAs(DatePicker.class).getValue());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#dateOfDeath").queryAs(DatePicker.class).setValue(LocalDate.of(2005, 5, 5));
            press(CONTROL).press(Z).release(Z).release(CONTROL);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertEquals(LocalDate.of(2003, 3, 3), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(LocalDate.of(2005, 5, 5), lookup("#dateOfDeath").queryAs(DatePicker.class).getValue());
    }

    /**
     * Tests that all possible RadioButtons can redo successfully
     */
    @Test
    public void redoRadioButtons() {
        // Check redo button first
        interact(() -> {
            clickOn(maleRadioX, radioY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(femaleRadioX, radioY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(otherRadioX, radioY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());


        // Check Ctrl Y next
        interact(() -> {
            clickOn(maleRadioX, radioY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(femaleRadioX, radioY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(otherRadioX, radioY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected());
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
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 3);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertEquals(LocalDate.of(2004, 4, 4), lookup("#dobDate").queryAs(DatePicker.class).getValue());

        interact(() -> {
            clickOn(maleRadioX, radioY);
            clickOn(femaleRadioX, radioY);
            clickOn(maleRadioX, radioY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());

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
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 3);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2004, 4, 4), lookup("#dobDate").queryAs(DatePicker.class).getValue());

        interact(() -> {
            clickOn(maleRadioX, radioY);
            clickOn(femaleRadioX, radioY);
            clickOn(maleRadioX, radioY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
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
            clickOn(maleRadioX, radioY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(undoX, undoY);
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            clickOn(redoX, redoY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            clickOn(maleRadioX, radioY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(dobDateDefault, lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertEquals(genderMaleRadioDefault, lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#dobDate").queryAs(DatePicker.class).getValue());
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
    }
}
