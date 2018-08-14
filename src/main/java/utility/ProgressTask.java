package utility;

import controller.GUIAvailibleOrgans;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;


public class ProgressTask extends Task<Void> {

    private ProgressBar progressBar;


    public ProgressTask(GUIAvailibleOrgans.PatientOrgan patientOrgan) {

    }


    @Override
    protected Void call() throws Exception {
        this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
        for (int i = 0; i < 100; i++) {
            updateProgress((1.0 * i) / 100, 1);
            double finalI = (1.0 * i) / 100;
            Platform.runLater(() -> {
                if (finalI >= 0.5){
                    if (finalI >= 0.8) {
                        progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                    } else {
                        progressBar.setStyle("-fx-accent: orange; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                    }
                } else {
                    progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                }
            });
            Thread.sleep(50);
        }
        return null;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
