package utility;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import model.Patient;
import model.PatientOrgan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class ProgressTask extends Task<Void> {

    private ProgressBar progressBar;

    private Patient patient;

    private GlobalEnums.Organ organ;

    private long elapsedTime = 0;

    public ProgressTask(PatientOrgan patientOrgan) {
        this.patient = patientOrgan.getPatient();
        this.organ = patientOrgan.getOrgan();
        this.elapsedTime = getElapsedTime();
    }

    @Override
    protected Void call() throws Exception {
        long remainingTime = calculateRemainingTime();
        System.out.println(LocalTime.MIN.plusSeconds(remainingTime).format(DateTimeFormatter.ISO_LOCAL_TIME));
        for (int i = ((int) elapsedTime); i < remainingTime; i++) {
            updateProgress((1.0 * i) / remainingTime, 1);
            double finalI = (1.0 * i) / remainingTime;
            Platform.runLater(() -> {
                if (finalI >= 0.5){
                    if (finalI >= 0.8) { //todo: take out to css
                        progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                    } else {
                        progressBar.setStyle("-fx-accent: orange; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                    }
                } else {
                    progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
                }
            });
            Thread.sleep(1000); //each loop is now 1 second
        }
        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.1);");
        });
        this.updateProgress(1, 1);
        return null;
    }


    private long getElapsedTime() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - patient.getDeathDate().toEpochSecond(ZoneOffset.UTC)
                + (organ.getOrganLowerBoundSeconds() - calculateRemainingTime());
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public long calculateRemainingTime() {
        long numberSecondsToDeath = patient.getDeathDate().toEpochSecond(ZoneOffset.UTC); //from 1900
        long timeOfOrganExpiry = numberSecondsToDeath + organ.getOrganLowerBoundSeconds();
        long numberSecondsToNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return timeOfOrganExpiry - numberSecondsToNow ;
    }
}
