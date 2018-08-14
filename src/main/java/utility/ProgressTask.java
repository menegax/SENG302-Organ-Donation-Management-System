package utility;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import model.Patient;
import model.PatientOrgan;

import java.time.LocalDateTime;
import static java.time.temporal.ChronoUnit.*;


public class ProgressTask extends Task<Void> {

    private ProgressBar progressBar;

    private Patient patient;

    private GlobalEnums.Organ organ;

    private long elapsedTime;

    public ProgressTask(PatientOrgan patientOrgan) {
        this.patient = patientOrgan.getPatient();
        this.organ = patientOrgan.getOrgan();
        this.elapsedTime = getElapsedTime();
    }

    @Override //todo: tidy upppppppp
    protected Void call() throws Exception {
        long remainingTime = calculateRemainingTime();
        for (int i = ((int) elapsedTime); i < remainingTime && elapsedTime >= 0; i++) {
            updateProgress((1.0 * i) / organ.getOrganUpperBoundSeconds(), 1);
            updateMessage(getTimeRemaining()); //in fx thread
            double finalI = (1.0 * i) / organ.getOrganUpperBoundSeconds(); //todo: pull out to methods
            Platform.runLater(() -> {
                double ratioOfLowerUpper = organ.getOrganLowerBoundSeconds() / (double)organ.getOrganUpperBoundSeconds();
                if (finalI >= ratioOfLowerUpper){ //has upper and lower bounds for expiry
                    if (finalI >= ratioOfLowerUpper + ((1 - ratioOfLowerUpper)/2)) { //red if greater than ratio of lower/upper + middle point
                        progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");//todo: take out to css
                    } else {
                        progressBar.setStyle("-fx-accent: orange; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                    }
                } else {
                    progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                }
                if (ratioOfLowerUpper == 1.0) { //one bound
                    if (finalI >= 0.5) {
                        if (finalI >= 0.8) {
                            progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                        } else{
                            progressBar.setStyle("-fx-accent: orange; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                        }
                    } else {
                        progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                    }
                }
            });
            Thread.sleep(1000); //each loop is now 1 second
        }
        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
        });
        this.updateProgress(1, 1);
        return null;
    }

    private String getTimeRemaining() {
        int hours = (int) HOURS.between(LocalDateTime.now(), patient.getDeathDate().plusSeconds(organ.getOrganUpperBoundSeconds()));
        int minutes = (int) MINUTES.between(LocalDateTime.now(), patient.getDeathDate().plusSeconds(organ.getOrganUpperBoundSeconds())) - (hours * 60);
        int seconds = (int) SECONDS.between(LocalDateTime.now(),
                patient.getDeathDate().plusSeconds(organ.getOrganUpperBoundSeconds())) - minutes* 60 -(hours * 3600);
        String hrs  = String.valueOf(hours).length() < 2 ? String.format("0%s",hours): String.valueOf(hours);
        String mins  = String.valueOf(minutes).length() < 2 ? String.format("0%s",minutes): String.valueOf(minutes);
        String secs  = String.valueOf(seconds).length() < 2 ? String.format("0%s",seconds): String.valueOf(seconds);
        return String.format("%s:%s:%s", hrs, mins, secs);
    }

    private long getElapsedTime() {
        return SECONDS.between(patient.getDeathDate(),LocalDateTime.now());
    }

    void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private long calculateRemainingTime() {
        long diff = SECONDS.between(LocalDateTime.now(), patient.getDeathDate().plusSeconds(organ.getOrganUpperBoundSeconds()));
        return diff < 0 ? 0 : diff;
    }

}
