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

    private PatientOrgan patientOrgan;

    private boolean interrupted;

    private String color;

    /**
     * Constructor for the ProgressTask
     * @param patientOrgan - patient organ for the task
     */
    public ProgressTask(PatientOrgan patientOrgan) {
        this.patient = patientOrgan.getPatient();
        this.organ = patientOrgan.getOrgan();
        this.elapsedTime = getElapsedTime();
        this.patientOrgan = patientOrgan;
        this.interrupted = false;
    }

    @Override
    protected Void call() throws Exception {
        setColor(elapsedTime / organ.getOrganUpperBoundSeconds());
        int i;
        for (i = ((int) elapsedTime) ; i < organ.getOrganUpperBoundSeconds() && !interrupted; i++) {
            updateProgress((1.0 * i) / (double) organ.getOrganUpperBoundSeconds(), 1);
            updateMessage(getTimeRemaining()); //in fx thread
            double finalI = (1.0 * i) / organ.getOrganUpperBoundSeconds();
            Platform.runLater(() -> setColor(finalI));
            Thread.sleep(1000); //each loop is now 1 second
        }
        if (i >= organ.getOrganUpperBoundSeconds()) {
            ExpiryObservable.getInstance().setExpired(this.patientOrgan);
        }
        return null;
    }

    /**
     * Set the task to interrupted. Will stop counting task
     */
    public void setInterrupted() {
        this.interrupted = true;
    }

    /**
     * Sets the color of the progress bar
     * @return the color string of the progress bar (in hex)
     */
    public String getColor() { return color; }

    /**
     * set the color of progress bar // -fx-accent: green;
     * @param finalI - the ratio of time elapsed / upper bound of the organ
     */
    public void setColor(double finalI) {
        String RED = "#FF0000";
        String ORANGE = "#FFA500";
        String GREEN = "#008000";
        double ratioOfLowerUpper = organ.getOrganLowerBoundSeconds() / (double)organ.getOrganUpperBoundSeconds();
        double percentage = (100 - ratioOfLowerUpper * 100);
        if (finalI >= ratioOfLowerUpper){ //has upper and lower bounds for expiry
            if (finalI >= ratioOfLowerUpper + ((1 - ratioOfLowerUpper)/2)) { //red if greater than ratio of lower/upper + middle point
                progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.5);" +
                        "-fx-background-color: linear-gradient(to left, #f92f2e , #f92f2e " + percentage + "% , transparent 5%)");
                color = RED;
            } else {
                progressBar.setStyle("-fx-accent: orange;-fx-control-inner-background: rgba(255, 255, 255, 0.5);" +
                        "-fx-background-color: linear-gradient(to left, #f92f2e , #f92f2e " + percentage + "% , transparent 5%)");
                color = ORANGE;
            }
        } else {
            progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.5);" +
                    "-fx-background-color: linear-gradient(to left, #f92f2e , #f92f2e " + percentage + "% , transparent 5%)");
            color = GREEN;
        }
        if (ratioOfLowerUpper == 1.0) { //one bound
            if (finalI >= 0.5) {
                if (finalI >= 0.8) {
                    progressBar.setStyle("-fx-accent: red; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                    color = RED;
                } else{
                    progressBar.setStyle("-fx-accent: orange; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                    color = ORANGE;
                }
            } else {
                progressBar.setStyle("-fx-accent: green; -fx-control-inner-background: rgba(255, 255, 255, 0.5);");
                color = GREEN;
            }
        }
    }

    /**
     * Gets the remaining time for the organ expiry
     * @return string of the time remaining of the organ expiry
     */
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


    /**
     * Gets the organ for expiry
     * @return organ for the expiry task
     */
    GlobalEnums.Organ getOrgan() {
        return this.organ;
    }

    /**
     * Gets the elapsed time between date of death and now
     * @return - the elapsed time between date of death and now
     */
    long getElapsedTime() {
        return SECONDS.between(patient.getDeathDate(),LocalDateTime.now());
    }

    /**
     * Sets the progress bar for the task
     * @param progressBar - progress bar to add to the task
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
