package gui_test.undoScreenTests;

import controller.Main;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;

import static javafx.scene.input.KeyCode.*;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the donor profile update screen
 */
public class GUIUndoDonorUpdateTest extends ApplicationTest{

    FxRobot robot = new FxRobot();

    private Main main = new Main();
    private double undoX;
    private double undoY;
    private String nhiTxtDefault;
    private String firstnameTxtDefault;
    private String lastnameTxtDefault;
    private String middlenameTxtDefault;
    private String street1TxtDefault;
    private String street2TxtDefault;
    private String suburbTxtDefault;
    private String regionTxtDefault;
    private String zipTxtDefault;
    private String weightTxtDefault;
    private String heightTxtDefault;

    private int bloodGroupDDDefault;

    private LocalDate dobDateDefault;

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
        main.start(stage);
        interact(() -> {
            stage.setFullScreen(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABC1238");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#editButton").queryAs(Button.class).fire();
        });
        undoX = lookup("#undoButton").queryAs(Button.class).getLayoutX() + 250;
        undoY = lookup("#undoButton").queryAs(Button.class).getLayoutY() + 28;
        radioY = lookup("#genderMaleRadio").queryAs(RadioButton.class).getLayoutY() + 150;
        maleRadioX = lookup("#genderMaleRadio").queryAs(RadioButton.class).getLayoutX() + 165;
        femaleRadioX = lookup("#genderFemaleRadio").queryAs(RadioButton.class).getLayoutX() + 195;
        otherRadioX = lookup("#genderOtherRadio").queryAs(RadioButton.class).getLayoutX() + 225;
    }

    /**
     * Sets the widgets on the screen to known values before testing
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
            regionTxtDefault = lookup("#regionTxt").queryAs(TextField.class).getText();
            zipTxtDefault = lookup("#zipTxt").queryAs(TextField.class).getText();
            weightTxtDefault = lookup("#weightTxt").queryAs(TextField.class).getText();
            heightTxtDefault = lookup("#heightTxt").queryAs(TextField.class).getText();

            bloodGroupDDDefault = lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex();

            dobDateDefault = lookup("#dobDate").queryAs(DatePicker.class).getValue();

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
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#firstnameTxt").queryAs(TextField.class).setText("FirstName2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#lastnameTxt").queryAs(TextField.class).setText("LastName2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#middlenameTxt").queryAs(TextField.class).setText("MiddleName2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#street1Txt").queryAs(TextField.class).setText("1 Test2 Street");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#street2Txt").queryAs(TextField.class).setText("2 Test2 Street");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#suburbTxt").queryAs(TextField.class).setText("Suburb2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#regionTxt").queryAs(TextField.class).setText("Region2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#zipTxt").queryAs(TextField.class).setText("0002");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#weightTxt").queryAs(TextField.class).setText("52");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            lookup("#heightTxt").queryAs(TextField.class).setText("2.2");
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });

        assertEquals(nhiTxtDefault, lookup("#nhiTxt").queryAs(TextField.class).getText());
        assertEquals(firstnameTxtDefault, lookup("#firstnameTxt").queryAs(TextField.class).getText());
        assertEquals(lastnameTxtDefault, lookup("#lastnameTxt").queryAs(TextField.class).getText());
        assertEquals(middlenameTxtDefault, lookup("#middlenameTxt").queryAs(TextField.class).getText());
        assertEquals(street1TxtDefault, lookup("#street1Txt").queryAs(TextField.class).getText());
        assertEquals(street2TxtDefault, lookup("#street2Txt").queryAs(TextField.class).getText());
        assertEquals(suburbTxtDefault, lookup("#suburbTxt").queryAs(TextField.class).getText());
        assertEquals(regionTxtDefault, lookup("#regionTxt").queryAs(TextField.class).getText());
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
            lookup("#regionTxt").queryAs(TextField.class).setText("Region3");
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
        assertEquals(regionTxtDefault, lookup("#regionTxt").queryAs(TextField.class).getText());
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
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);

        // Check Ctrl Z next
        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
    }

    /**
     * Tests that all possible DatePickers can undo successfully
     */
    @Test
    public void undoDatePickers() {
        // Check undo button first
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
    }

    /**
     * Tests that all possible RadioButtons can undo successfully
     */
    @Test
    public void undoRadioButtons() {
        // Check undo button first
        interact(() -> {
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(femaleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(otherRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });

        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected() == genderFemaleRadioDefault);
        assertTrue(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected() == genderOtherRadioDefault);

        // Check Ctrl Z next
        interact(() -> {
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            robot.moveTo(femaleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            robot.moveTo(otherRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected() == genderFemaleRadioDefault);
        assertTrue(lookup("#genderOtherRadio").queryAs(RadioButton.class).isSelected() == genderOtherRadioDefault);
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
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));

        interact(() -> {
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(2);
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(3);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 2);
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);

        interact(() -> {
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2003, 3, 3)));
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2002, 2, 2)));
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));

        interact(() -> {
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(femaleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#genderFemaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected());
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
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
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(femaleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
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
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(LocalDate.of(2002, 2, 2)));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == 1);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            robot.moveTo(undoX, undoY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("BBB2222");
            lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().select(1);
            lookup("#dobDate").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            robot.moveTo(maleRadioX, radioY);
            robot.press(MouseButton.PRIMARY).release(MouseButton.PRIMARY);
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
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiTxt").queryAs(TextField.class).getText().equals(nhiTxtDefault));
        assertTrue(lookup("#bloodGroupDD").queryAs(ChoiceBox.class).getSelectionModel().getSelectedIndex() == bloodGroupDDDefault);
        assertTrue(lookup("#dobDate").queryAs(DatePicker.class).getValue().equals(dobDateDefault));
        assertTrue(lookup("#genderMaleRadio").queryAs(RadioButton.class).isSelected() == genderMaleRadioDefault);
    }
}
