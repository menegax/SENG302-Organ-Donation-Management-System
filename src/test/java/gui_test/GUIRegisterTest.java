package gui_test;

import controller.Main;
import javafx.event.ActionEvent;
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

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIRegisterTest extends ApplicationTest {

    private Main main = new Main();
    private LocalDate d = LocalDate.of(1957,6,21);

    @Before
    public void setup() {
        Database.resetDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
        interact(() ->  lookup("#registerHyperlink").queryAs(Hyperlink.class).fire());

    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void verify_screen() {
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void should_successfully_register_with_middle_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB9922");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#middlenameRegister").queryAs(TextField.class).setText("Wil");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB9922"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("William"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("Wil"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("Williamson"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == d);

        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            //clickOn(900,450);
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_successfully_register_without_middle_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB9782");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("Willis");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Brucie");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB9752"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("Willis"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("Brucie"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == d);

        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_no_input() {
        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).fire();
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat(lookup("#donorRegisterAnchorPane"), Node::isVisible);
    }

    @Test
    public void unsuccessful_register_no_nhi() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_invalid_nhi() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_no_first_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_no_last_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_no_birth_date() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_duplicate_nhi() throws InvalidObjectException {

        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatients(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("TFX9999");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

}
