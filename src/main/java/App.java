import cli.CLIMain;
import controller.Main;
import controller.ScreenControl;
import controller.TUIOFXMain;
//import controller.TuioFXApplication;

import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] argv) {

        List<String> argArrayList = Arrays.asList(argv);

        //Launch ScreenControl here
        if (argArrayList.contains("cli")) {
            CLIMain.main(null);
        }
        else if (argArrayList.contains("touch")) {
            ScreenControl.setUpScreenControl("touch");
            TUIOFXMain.main(null);
        }
        else {
            ScreenControl.setUpScreenControl("desktop");
            Main.main(null);
        }
    }
}
