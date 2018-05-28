package model;

public class Medication {

    private String medicationName;

    public Medication(String name) {
        medicationName = name;
    }

    public String getMedicationName() {
        return medicationName;
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
