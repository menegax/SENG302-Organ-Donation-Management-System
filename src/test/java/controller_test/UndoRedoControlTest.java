package controller_test;

import com.sun.javafx.application.PlatformImpl;
import controller.UndoRedoControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.junit.Before;
import org.junit.Test;
import utility.undoRedo.StatesHistoryScreen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class to test the public methods of the UndoRedoControl class
 */
public class UndoRedoControlTest {

    private List<Control> controls;
    private CheckBox checkBox;
    private ChoiceBox choiceBox;
    private ComboBox comboBox;
    private DatePicker datePicker;
    private RadioButton radioButton;
    private TextField textField;

    /**
     * Tests that the controls are set to the states of the statesHistoryScreen if and only if setStates is called
     */
    @Test
    public void testSetStates() {
        resetControls();
        StatesHistoryScreen statesHistoryScreen = new StatesHistoryScreen(controls, null);
        checkBox.setSelected(true);
        choiceBox.getSelectionModel().select(0);
        comboBox.getSelectionModel().select(0);
        datePicker.setValue(LocalDate.of(1, 1, 1));
        radioButton.requestFocus();
        radioButton.setSelected(true);
        textField.setText("A");
        resetControls();
        //Controls in StatesHistoryScreen should still be in the above states
        checkBox.setSelected(false);
        choiceBox.getSelectionModel().select(1);
        comboBox.getSelectionModel().select(1);
        datePicker.setValue(LocalDate.of(2, 2, 2));
        radioButton.requestFocus();
        radioButton.setSelected(false);
        textField.setText("B");
        assertFalse(testControlStates(statesHistoryScreen));
        UndoRedoControl.setStates(statesHistoryScreen, controls);
        assertTrue(testControlStates(statesHistoryScreen));
    }

    /**
     * Resets the control attributes to new objects
     */
    private void resetControls() {
        PlatformImpl.startup(() -> {});
        checkBox = new CheckBox();
        choiceBox = new ChoiceBox();
        comboBox = new ComboBox();
        datePicker = new DatePicker();
        radioButton = new RadioButton();
        textField = new TextField();
        ObservableList<String> testChoices = FXCollections.observableArrayList(new ArrayList<String>(){{
            add("A");
            add("B");
        }});
        // Following calls are safe
        choiceBox.setItems(testChoices);
        comboBox.setItems(testChoices);
        controls = new ArrayList<Control>(){{
            add(checkBox);
            add(choiceBox);
            add(comboBox);
            add(datePicker);
            add(radioButton);
            add(textField);
        }};
    }

    /**
     * Checks whether the states in the statesHistoryScreen match the current states of the controls
     * @param statesHistoryScreen the statesHistory screen to check against
     * @return whether the statesHistoryScreen and the current control states are equivalent
     */
    private boolean testControlStates(StatesHistoryScreen statesHistoryScreen) {
        return (statesHistoryScreen.getStateOfControl(0).equals(checkBox.isSelected()) && 
                statesHistoryScreen.getStateOfControl(1).equals(choiceBox.getSelectionModel().getSelectedItem()) &&
                statesHistoryScreen.getStateOfControl(2).equals(comboBox.getSelectionModel().getSelectedItem()) &&
                statesHistoryScreen.getStateOfControl(3).equals(datePicker.getValue().toString()) &&
                statesHistoryScreen.getStateOfControl(4).equals(radioButton.isSelected()) &&
                statesHistoryScreen.getStateOfControl(5).equals(textField.getText())
        );
    }
}
