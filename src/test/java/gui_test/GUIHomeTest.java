package gui_test;

import controller.Main;
import controller.ScreenControl;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIHomeTest extends ApplicationTest {

    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml"));
//        Scene rootScene = new Scene(root, 600, 400);
//        stage.setScene(rootScene); //set scene on primary stage
//        stage.show();
        main.start(stage);
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABC1238");
            lookup("#loginButton").queryAs(Button.class).fire();
        });
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void should_be_on_home_screen() {
        verifyThat("#homePane", Node::isVisible);
    }

    @Test
    public void should_logout() {
        interact(() -> {
            lookup("#logOutButton").queryAs(Button.class).fire();
        });
        assertThat(ScreenControl.getLoggedInDonor() == null);
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_go_to_profile() {
        interact(() -> lookup("#profileButton").queryAs(Button.class).fire());
        verifyThat("#donorProfilePane", Node::isVisible);
    }

    @Test
    public void should_go_to_log() {
        interact(() -> lookup("#historyButton").queryAs(Button.class).fire());
        verifyThat("#donorLogPane", Node::isVisible);
    }


}
