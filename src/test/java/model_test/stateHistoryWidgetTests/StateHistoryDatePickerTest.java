package model_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.DatePicker;
import utility.undoRedo.stateHistoryControls.StateHistoryDatePicker;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Junit test class for the StateHistoryDatePicker
 */
public class StateHistoryDatePickerTest {

    private static DatePicker datePicker;

    /**
     * Creates the DatePicker to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        PlatformImpl.startup(() -> {});
        datePicker = new DatePicker();
    }

    /**
     * Tests the constructor for a stateHistoryDatePicker
     */
    @Test
    public void testConstructor() {
        datePicker.setValue(LocalDate.of(2001, 1, 1));
        StateHistoryDatePicker stateHistoryDatePicker = new StateHistoryDatePicker(datePicker);
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(LocalDate.of(2001, 1, 1).toString());
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(0, stateHistoryDatePicker.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryDatePicker
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        datePicker.setValue(LocalDate.of(2001, 1, 1));
        StateHistoryDatePicker stateHistoryDatePicker = new StateHistoryDatePicker(datePicker);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryDatePicker.store();
        datePicker.setValue(LocalDate.of(2002, 2, 2));
        stateHistoryDatePicker.store();
        checkList.add(LocalDate.of(2001, 1, 1).toString());
        checkList.add(LocalDate.of(2001, 1, 1).toString());
        checkList.add(LocalDate.of(2002, 2, 2).toString());
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(2, stateHistoryDatePicker.getIndex());

        stateHistoryDatePicker.undo();
        stateHistoryDatePicker.undo();
        datePicker.setValue(LocalDate.of(2003, 3, 3));
        stateHistoryDatePicker.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add(LocalDate.of(2003, 3, 3).toString());
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(1, stateHistoryDatePicker.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryDatePicker
     */
    @Test
    public void testUndo() {
        datePicker.setValue(LocalDate.of(2001, 1, 1));
        StateHistoryDatePicker stateHistoryDatePicker = new StateHistoryDatePicker(datePicker);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryDatePicker.store();
        stateHistoryDatePicker.undo();
        checkList.add(LocalDate.of(2001, 1, 1).toString());
        checkList.add(LocalDate.of(2001, 1, 1).toString());
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(0, stateHistoryDatePicker.getIndex());

        stateHistoryDatePicker.undo();
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(0, stateHistoryDatePicker.getIndex());

        datePicker.setValue(LocalDate.of(2002, 2, 2));
        stateHistoryDatePicker.store();
        datePicker.setValue(LocalDate.of(2003, 3, 3));
        stateHistoryDatePicker.store();
        stateHistoryDatePicker.undo();
        checkList.remove(1);
        checkList.add(LocalDate.of(2002, 2, 2).toString());
        checkList.add(LocalDate.of(2003, 3, 3).toString());
        assertEquals(checkList, stateHistoryDatePicker.getStates());
        assertEquals(1, stateHistoryDatePicker.getIndex());
        assertEquals(LocalDate.of(2002, 2, 2), datePicker.getValue());
        stateHistoryDatePicker.undo();
        assertEquals(LocalDate.of(2001, 1, 1), datePicker.getValue());
    }
}
