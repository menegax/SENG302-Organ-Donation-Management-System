package gui_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIRegisterTest extends ApplicationTest {

    private Main main = new Main();
    private LocalDate d = LocalDate.of(1957,6,21);

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml"));
//        Scene rootScene = new Scene(root, 600, 400);
//        stage.setScene(rootScene); //set scene on primary stage
//        stage.show();
        main.start(stage);
        interact(() ->  lookup("#registerLabel").queryAs(Hyperlink.class).fire());
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void verify_screen() {
        verifyThat("#registerPane", Node::isVisible);
    }

    @Test
    public void should_successfully_register_with_middle_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#middlenameRegister").queryAs(TextField.class).setText("Wil");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
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
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("William"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("Williamson"));
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
        verifyThat(lookup("#registerPane"), Node::isVisible);
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
        verifyThat("#registerPane", Node::isVisible);
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
        verifyThat("#registerPane", Node::isVisible);
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
        verifyThat("#registerPane", Node::isVisible);
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
        verifyThat("#registerPane", Node::isVisible);
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
        verifyThat("#registerPane", Node::isVisible);
    }

    @Test
    public void unsuccessful_register_duplicate_nhi() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("ABC1238");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#registerPane", Node::isVisible);
    }

//    @Test
//    public void should_return_to_login() {
//        interact(() -> lookup("#backLabel").queryAs(Label.class).getOnMouseClicked());
//        verifyThat("#loginPane", Node::isVisible);
//    }

}
