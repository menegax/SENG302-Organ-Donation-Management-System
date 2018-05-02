package gui_test;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

import controller.Main;
import controller.ScreenControl;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Clinician;
import model.Donor;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class GUIClinicianSearchDonorsPopUpTest extends ApplicationTest {

    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        main.start( stage );

        ArrayList<String> mid = new ArrayList<>();
        mid.add("Middle");
        Database.addClinician(new Clinician(Database.getNextStaffID(), "initial", mid, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));


    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }


    /**
     * Tests logging in as a clinician and going to the search donors scene
     */
    @Test
    public void successfulLoginTestAndGoToSearchDonors() {
        //Check 'I am Clinician" checkbox to login as clinician
        interact( () -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("0");
        });
        verifyThat("#nhiLogin", TextInputControlMatchers.hasText( "0" ));
        interact( () -> {
            lookup( "#loginButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#clinicianHomePane", Node::isVisible ); // Verify that login has taken "user" to the clinician home panel
        interact( () -> {
            lookup( "#searchDonors" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        verifyThat( "#clinicianSearchDonorsPane", Node::isVisible ); // Verify that login has taken "user" to the clinician profile panel
    }

    /**
     * Tests the pop up for table row double-clicking
     */
    @Test
    public void successfulPopUpNavigation() {
        //Check 'I am Clinician" checkbox to login as clinician
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class)
                    .setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText("0");
        });
        verifyThat("#nhiLogin", TextInputControlMatchers.hasText("0"));
        interact(() -> {
            lookup("#loginButton").queryAs(Button.class)
                    .getOnAction()
                    .handle(new ActionEvent());
        });
        verifyThat("#clinicianHomePane", Node::isVisible); // Verify that login has taken "user" to the clinician home panel
        interact(() -> {
            lookup("#searchDonors").queryAs(Button.class)
                    .getOnAction()
                    .handle(new ActionEvent());
        });

        // double-click to get a pop up
        interact( () -> {
            lookup( "#donorDataTable" ).queryAs( TableView.class ).getSelectionModel().select(0);
            doubleClickOn( "#donorDataTable" ).doubleClickOn();
        });
        verifyThat("#donorProfilePane", Node::isVisible);
    }
}

