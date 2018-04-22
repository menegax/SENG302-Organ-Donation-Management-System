package model_test;

import controller.IUndoRedo;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import model.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Junit test class for the StatesHistoryScreen
 */
public class StatesHistoryScreenTest {

    private static TextField textField1;
    private static TextField textField2;
    private static CheckBox checkBox1;
    private static CheckBox checkBox2;
    private static ComboBox comboBox1;
    private static ComboBox comboBox2;
    private static RadioButton radioButton1;
    private static RadioButton radioButton2;
    private static ChoiceBox choiceBox1;
    private static ChoiceBox choiceBox2;
    private static StatesHistoryScreen statesHistoryScreen;

    /**
     * Creates the widgets to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        latch.await();
        textField1 = new TextField();
        textField2 = new TextField();
        checkBox1 = new CheckBox();
        checkBox2 = new CheckBox();
        comboBox1 = new ComboBox();
        comboBox2 = new ComboBox();
        radioButton1 = new RadioButton();
        radioButton2 = new RadioButton();
        choiceBox1 = new ChoiceBox();
        choiceBox2 = new ChoiceBox();
        ArrayList<String> items = new ArrayList<>();
        items.add("A");
        items.add("B");
        items.add("C");
        comboBox1.setItems(FXCollections.observableArrayList(items));
        comboBox2.setItems(FXCollections.observableArrayList(items));
        choiceBox1.setItems(FXCollections.observableArrayList(items));
        choiceBox2.setItems(FXCollections.observableArrayList(items));
    }

    /**
     * Initialises the StatesHistoryScreen to be tested before each test
     */
    @Before
    public void createStatesHistoryScreen() {
        textField1.setText("");
        textField1.setText("");
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
        comboBox1.getSelectionModel().select(0);
        comboBox2.getSelectionModel().select(0);
        radioButton1.selectedProperty().setValue(false);
        radioButton2.selectedProperty().setValue(false);
        choiceBox1.getSelectionModel().select(0);
        choiceBox2.getSelectionModel().select(0);
        ArrayList<TextField> entryList = new ArrayList<>();
        ArrayList<CheckBox> checkBoxList = new ArrayList<>();
        ArrayList<ComboBox> comboBoxList = new ArrayList<>();
        ArrayList<RadioButton> radioButtonList = new ArrayList<>();
        ArrayList<ChoiceBox> choiceBoxList = new ArrayList<>();
        entryList.add(textField1);
        entryList.add(textField2);
        checkBoxList.add(checkBox1);
        checkBoxList.add(checkBox2);
        comboBoxList.add(comboBox1);
        comboBoxList.add(comboBox2);
        radioButtonList.add(radioButton1);
        radioButtonList.add(radioButton2);
        choiceBoxList.add(choiceBox1);
        choiceBoxList.add(choiceBox2);
        statesHistoryScreen = new StatesHistoryScreen(entryList, comboBoxList, checkBoxList, radioButtonList, choiceBoxList);
    }

    /**
     * Tests the constructor for StatesHistoryScreen
     */
    @Test
    public void testConstructor() {
        checkWidgets();
        statesHistoryScreen = new StatesHistoryScreen(new ArrayList<TextField>(), new ArrayList<ComboBox<String>>(), new ArrayList<CheckBox>());
        assertEquals(statesHistoryScreen.getStateHistories().size(), 0);
        statesHistoryScreen = new StatesHistoryScreen();
        assertEquals(statesHistoryScreen.getStateHistories().size(), 0);
    }

    /**
     * Tests the store method of the StatesHistoryScreen
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        // Undo will fail if the following checkWidgets fail
        textField1.setText("A");
        statesHistoryScreen.store();
        checkWidgets();
        comboBox1.getSelectionModel().select(1);
        statesHistoryScreen.store();
        checkWidgets();
        checkBox1.setSelected(true);
        statesHistoryScreen.store();
        checkWidgets();
        radioButton1.selectedProperty().setValue(true);
        statesHistoryScreen.store();
        checkWidgets();
        choiceBox1.getSelectionModel().select(1);
        statesHistoryScreen.store();
        checkWidgets();

        // These asserts will fail if undo fails
        statesHistoryScreen.undo();
        textField2.setText("B");
        statesHistoryScreen.store();
        checkWidgets();
        statesHistoryScreen.undo();
        comboBox2.getSelectionModel().select(2);
        statesHistoryScreen.store();
        checkWidgets();
        statesHistoryScreen.undo();
        checkBox2.setSelected(true);
        statesHistoryScreen.store();
        checkWidgets();
        statesHistoryScreen.undo();
        radioButton2.selectedProperty().setValue(true);
        statesHistoryScreen.store();
        checkWidgets();
        statesHistoryScreen.undo();
        choiceBox2.getSelectionModel().select(2);
        statesHistoryScreen.store();
        checkWidgets();
    }

    /**
     * Tests the undo method of the StatesHistoryScreen
     */
    @Test
    public void testUndo() {
        textField1.setText("A");
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();
        comboBox1.getSelectionModel().select(1);
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();
        checkBox1.setSelected(true);
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();
        radioButton1.selectedProperty().setValue(true);
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();
        choiceBox1.getSelectionModel().select(1);
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();

        statesHistoryScreen.undo();
        checkWidgets();

        textField2.setText("B");
        statesHistoryScreen.store();
        comboBox2.getSelectionModel().select(2);
        statesHistoryScreen.store();
        statesHistoryScreen.undo();
        checkWidgets();
        statesHistoryScreen.undo();
        checkWidgets();
    }

    /**
     * Checks all that the current states stored in the StatesHistoryScreen correspond to the current states of the widgets
     */
    private void checkWidgets() {
        StateHistoryTextEntry stateHistoryTextEntry1 = (StateHistoryTextEntry) statesHistoryScreen.getStateHistories().get(0);
        StateHistoryTextEntry stateHistoryTextEntry2 = (StateHistoryTextEntry) statesHistoryScreen.getStateHistories().get(1);
        StateHistoryComboBox stateHistoryComboBox1 = (StateHistoryComboBox) statesHistoryScreen.getStateHistories().get(2);
        StateHistoryComboBox stateHistoryComboBox2 = (StateHistoryComboBox) statesHistoryScreen.getStateHistories().get(3);
        StateHistoryCheckBox stateHistoryCheckBox1 = (StateHistoryCheckBox) statesHistoryScreen.getStateHistories().get(4);
        StateHistoryCheckBox stateHistoryCheckBox2 = (StateHistoryCheckBox) statesHistoryScreen.getStateHistories().get(5);
        StateHistoryRadioButton stateHistoryRadioButton1 = (StateHistoryRadioButton) statesHistoryScreen.getStateHistories().get(6);
        StateHistoryRadioButton stateHistoryRadioButton2 = (StateHistoryRadioButton) statesHistoryScreen.getStateHistories().get(7);
        StateHistoryChoiceBox stateHistoryChoiceBox1 = (StateHistoryChoiceBox) statesHistoryScreen.getStateHistories().get(8);
        StateHistoryChoiceBox stateHistoryChoiceBox2 = (StateHistoryChoiceBox) statesHistoryScreen.getStateHistories().get(9);

        assertEquals(textField1.getText(), stateHistoryTextEntry1.getStates().get(stateHistoryTextEntry1.getIndex()));
        assertEquals(textField2.getText(), stateHistoryTextEntry2.getStates().get(stateHistoryTextEntry2.getIndex()));
        assertEquals(comboBox1.getSelectionModel().getSelectedItem(), stateHistoryComboBox1.getStates().get(stateHistoryComboBox1.getIndex()));
        assertEquals(comboBox2.getSelectionModel().getSelectedItem(), stateHistoryComboBox2.getStates().get(stateHistoryComboBox2.getIndex()));
        assertEquals(checkBox1.isSelected(), stateHistoryCheckBox1.getStates().get(stateHistoryCheckBox1.getIndex()));
        assertEquals(checkBox2.isSelected(), stateHistoryCheckBox2.getStates().get(stateHistoryCheckBox2.getIndex()));
        assertEquals(radioButton1.selectedProperty().get(), stateHistoryRadioButton1.getStates().get(stateHistoryRadioButton1.getIndex()));
        assertEquals(radioButton2.selectedProperty().get(), stateHistoryRadioButton2.getStates().get(stateHistoryRadioButton2.getIndex()));
        assertEquals(choiceBox1.getSelectionModel().getSelectedItem(), stateHistoryChoiceBox1.getStates().get(stateHistoryChoiceBox1.getIndex()));
        assertEquals(choiceBox2.getSelectionModel().getSelectedItem(), stateHistoryChoiceBox2.getStates().get(stateHistoryChoiceBox2.getIndex()));
    }
}
