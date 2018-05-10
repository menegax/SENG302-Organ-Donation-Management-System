//package gui_test;
//
//import controller.Main;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import model.Patient;
//import org.junit.After;
//import org.junit.Test;
//import org.testfx.util.WaitForAsyncUtils;
//import service.Database;
//import utility.GlobalEnums;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//import static java.lang.Thread.sleep;
//import static org.testfx.api.FxAssert.verifyThat;
//
//public class GUIReceiverTest {
//
//    private Main main = new Main();
//    @Override
//    public void start(Stage stage) throws Exception {
//
//        // add dummy donor
//        ArrayList<String> dal = new ArrayList<>();
//        dal.add("Middle");
//        Database.addPatients(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
//        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
//        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);
//
//        main.start(stage);
//        interact(() ->  {
//            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
//            lookup("#loginButton").queryAs(Button.class).fire();
//            lookup("#profileButton").queryAs(Button.class).fire();
//        });
//    }
//
//    @After
//    public void waitForEvents() {
//        Database.resetDatabase();
//        WaitForAsyncUtils.waitForFxEvents();
//        sleep(1000);
//    }
//
//    @Test
//    public void should_be_on_profile_screen() {
//        verifyThat("#donorProfilePane", Node::isVisible);
//    }
//}
