package controller_test;

import com.sun.javafx.application.PlatformImpl;
import controller.ScreenControl;
import javafx.application.Platform;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.undoRedo.UndoableStage;

import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

public class ScreenControlTest {

    private UndoableStage testStage1;

    private UndoableStage testStage2;

    private UndoableStage nullStage;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Sets up the JavaFX environment so stages can be created
     */
    @BeforeClass
    public static void setup() {
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {});
    }

    /**
     * Tests the setIsSaved method of screen control
     * Does this by checking asterixs on stage titles
     */
    @Test
    public void testSetIsSaved() {
        Platform.runLater(() -> { // Means that stages are created on JavaFX thread not main thread
            givenTestTitleStagesAdded();
            givenNullTitleStageAdded();
            whenSetNotSaved();
            thenStagesHaveAsterixs();
            whenSetSaved();
            thenStagesDontHaveAsterixs();
            whenSetSaved();
            thenStagesDontHaveAsterixs();
            whenSetNotSaved();
            thenStagesHaveAsterixs();
            whenSetNotSaved();
            thenStagesHaveAsterixs();
        });
    }

    /**
     * Adds stages with title test1 and test2 to ScreenControl
     */
    private void givenTestTitleStagesAdded() {
        testStage1 = new UndoableStage();
        testStage2 = new UndoableStage();
        testStage1.setTitle("Test1");
        testStage2.setTitle("Test2");
        screenControl.addStage(testStage1.getUUID(), testStage1);
        screenControl.addStage(testStage2.getUUID(), testStage2);
    }

    /**
     * Adds a stage with a null title to ScreenControl
     */
    private void givenNullTitleStageAdded() {
        nullStage = new UndoableStage();
        screenControl.addStage(nullStage.getUUID(), nullStage);
    }

    /**
     * Sets ScreenControl to not saved
     */
    private void whenSetNotSaved() {
        screenControl.setIsSaved(false);
    }

    /**
     * Sets ScreenControl to saved
     */
    private void whenSetSaved() {
        screenControl.setIsSaved(true);
    }

    /**
     * Checks that the stages have an asterix when they have a title
     */
    private void thenStagesHaveAsterixs() {
        assertEquals("Test1*", testStage1.getTitle());
        assertEquals("Test2*", testStage2.getTitle());
        assertEquals(null, nullStage.getTitle());
    }

    /**
     * Checks that the stages don't have an asterix when they have a title
     */
    private void thenStagesDontHaveAsterixs() {
        assertEquals("Test1", testStage1.getTitle());
        assertEquals("Test2", testStage2.getTitle());
        assertEquals(null, nullStage.getTitle());
    }
}
