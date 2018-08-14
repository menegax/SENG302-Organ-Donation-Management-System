package model;

import javafx.beans.property.StringProperty;
import utility.GlobalEnums;
import utility.ProgressTask;

/**
 * Simple holder for patients and organ so that it is known which organ belongs to whom.
 */
public class PatientOrgan {
    private Patient patient;
    private GlobalEnums.Organ organ;
    private ProgressTask progressTask;
    private StringProperty messageProperty;

    public PatientOrgan(Patient patient, GlobalEnums.Organ organ) {
        this.patient = patient;
        this.organ = organ;
        this.progressTask = new ProgressTask(this);
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

    public long getOrganUpperBound() {
        return organ.getOrganUpperBoundSeconds();
    }

    public long getOrganLowerBound() {
        return organ.getOrganLowerBoundSeconds();
    }

    public final StringProperty messageProperty() {
        return messageProperty;
    }
    public void setMessage(StringProperty message){
        this.messageProperty = message;
    }

    @Override
    public boolean equals(Object obj) {
        PatientOrgan patientOrgan = (PatientOrgan) obj;
        return patientOrgan.patient.getNhiNumber().equals(this.patient.getNhiNumber()) &&
                patientOrgan.organ.equals(this.organ);

    }

}