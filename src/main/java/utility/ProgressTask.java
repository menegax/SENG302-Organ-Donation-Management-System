package utility;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import model.Patient;
import model.PatientOrgan;
import org.joda.time.Hours;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.*;


public class ProgressTask extends Task<Void> {

    private ProgressBar progressBar;

    private Patient patient;

    private GlobalEnums.Organ organ;

    private long elapsedTime = 0;

    private PatientOrgan patientOrgan;

    public ProgressTask(PatientOrgan patientOrgan) {
        this.patientOrgan = patientOrgan;
        this.patient = patientOrgan.getPatient();
        this.organ = patientOrgan.getOrgan();
        this.elapsedTime = getElapsedTime();
    }

    @Override
    protected Void call() throws Exception {
        long remainingTime = calculateRemainingTime();
        for (int i = ((int) elapsedTime); i < remainingTime; i++) {
            updateProgress((1.0 * i) / organ.getOrganUpperBoundSeconds(), 1);
            updateMessage(getTimeRemaining());
            double finalI = (1.0 * i) / organ.getOrganUpperBoundSeconds();
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
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - patient.getDeathDate().toEpochSecond(ZoneOffset.UTC)
                + (organ.getOrganLowerBoundSeconds() - calculateRemainingTime());
    }

    void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private long calculateRemainingTime() {
        long numberSecondsToDeath = patient.getDeathDate().toEpochSecond(ZoneOffset.UTC); //from 1900
        long timeOfOrganExpiry = numberSecondsToDeath + organ.getOrganUpperBoundSeconds();
        long numberSecondsToNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return timeOfOrganExpiry - numberSecondsToNow ;
    }
}
