package gui_test.undoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
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
import static javafx.scene.input.KeyCode.Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the donor donations update screen
 */
public class GUIUndoDonorDonationsTest extends ApplicationTest{

    private Main main = new Main();

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
        Database.addPatients(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#donationsButton").queryAs(Button.class).fire();
        });
    }

    /**
     * Sets the widgets on the screen to known values before testing
     */
    @Before
    public void setFields() {
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(false);
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
        verifyThat("#donorDonationsAnchorPane", Node::isVisible);
    }

    /**
     * Tests that all possible CheckBoxes can undo successfully
     */
    @Test
    public void undoCheckBoxes() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });

        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that all possible widgets can undo multiple times in a row
     */
    @Test
    public void undoMultipleTimes() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that multiple different widgets can undo after each other
     */
    @Test
    public void undoOverMultipleWidgets() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
    }
}
