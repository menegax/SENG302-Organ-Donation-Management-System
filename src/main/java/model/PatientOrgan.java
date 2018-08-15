package model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import utility.GlobalEnums;
import utility.ProgressTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Simple holder for patients and organ so that it is known which organ belongs to whom.
 */
public class PatientOrgan {
    private Patient patient;
    private GlobalEnums.Organ organ;
    private ProgressTask progressTask;
    private Long timeRemaining;
    private boolean isExpired;

    public PatientOrgan(Patient patient, GlobalEnums.Organ organ) {
        this.patient = patient;
        this.organ = organ;
        this.progressTask = new ProgressTask(this);
        this.timeRemaining = SECONDS.between(patient.getDeathDate().plusSeconds(organ.getOrganUpperBoundSeconds()), LocalDateTime.now());
    }

    public Patient getPatient() {
        return patient;
    }

    public GlobalEnums.Organ getOrgan() {
        return organ;
    }

    public ProgressTask getProgressTask() {
        return progressTask;
    }

    public Long timeRemaining() {
        return timeRemaining;
    }

    public boolean isExpired() { return isExpired; }
    @Override
    public boolean equals(Object obj) {
        PatientOrgan patientOrgan = (PatientOrgan) obj;
        return patientOrgan.patient.getNhiNumber().equals(this.patient.getNhiNumber()) &&
                patientOrgan.organ.equals(this.organ);

    }

}