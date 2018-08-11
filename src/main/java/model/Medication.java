package model;

import utility.GlobalEnums.*;

import java.io.Serializable;

public class Medication implements Serializable {

    private String medicationName;

    private MedicationStatus medicationStatus;

    public Medication(String name, MedicationStatus status) {
        medicationStatus = status;
        medicationName = name;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public MedicationStatus getMedicationStatus() {
        return medicationStatus;
    }

    public void setMedicationStatus(MedicationStatus medicationStatus) {
        this.medicationStatus = medicationStatus;
    }

    /**
     * Returns the medication name as the toString. Overrides base toString() method
     * @return String medication
     */
    @Override
    public String toString() {
        return medicationName;
    }

}
