package model;

import java.util.ArrayList;

public class Medication {

    private String medicationName;

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getMedicationName() {
        return medicationName;
    }

    /**
     * Transfers a Medication object from one ArrayList to another. Used to shift a Medication from
     * medication history to current medications and vice versa.
     * This method is static so it can be used independently in multiple GUI windows.
     * @param previous ArrayList Medication was previously in
     * @param destination ArrayList to move Medication to
     * @param medication Medication to be moved
     * @param previousIndex index  the transferred medication was at in previous
     */
    public static void transferMedication(ArrayList<Medication> previous, ArrayList<Medication> destination,
                               Medication medication, int previousIndex) {
        destination.add(medication);
        previous.remove(previousIndex);
    }

}
