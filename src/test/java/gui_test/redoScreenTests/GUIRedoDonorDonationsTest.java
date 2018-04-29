package gui_test.redoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the donor donations update screen
 */
public class GUIRedoDonorDonationsTest extends ApplicationTest{

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
     * Tests that all possible CheckBoxes can redo successfully
     */
    @Test
    public void redoCheckBoxes() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that all possible widgets can redo multiple times in a row
     */
    @Test
    public void redoMultipleTimes() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that multiple different widgets can redo after each other
     */
    @Test
    public void redoOverMultipleWidgets() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(!lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
    }
}
