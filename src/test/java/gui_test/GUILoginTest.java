package gui_test;



import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUILoginTest extends ApplicationTest {


    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
//        Scene rootScene = new Scene(root, 600, 400);
//        stage.setScene(rootScene); //set scene on primary stage
//        stage.show();
        main.start(stage);
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void should_open_on_login() {
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test(expected = FxRobotException.class)
    public void click_on_wrong_button() {
        clickOn("#login");
    }

    @Test
    public void should_login() {
        interact(() -> lookup("#nhiLogin").queryAs(TextField.class).setText("ABC1234"));
        assertThat(lookup("#nhiLogin").queryAs(TextField.class)).hasText("ABC1234");
        interact(() -> lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        verifyThat("#homePane", Node::isVisible);
    }

    @Test
    public void should_fail_login() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABD1234");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_fail_login_blank() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });

        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_open_register_form() {
        interact(() -> lookup("#registerLabel").queryAs(Hyperlink.class).fire());
        verifyThat("#registerPane", Node::isVisible);
    }


}
