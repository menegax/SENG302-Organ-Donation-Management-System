package model;

import utility.GlobalEnums.DiseaseState;

import java.io.InvalidObjectException;
import java.time.LocalDate;

public class Disease {

    private String diseaseName;

    private LocalDate dateDiagnosed;

    private DiseaseState diseaseState;

    /**
     *  Constructor for a disease
     * @param diseaseName - name of the disease
     * @param diseaseState - disease state
     */
    public Disease(String diseaseName, DiseaseState diseaseState){
        this.diseaseName = diseaseName;
        this.dateDiagnosed = LocalDate.now();
        this.diseaseState = diseaseState;
    }

    /**
     *  Gets the name of the disease
     * @return - String name of the disease
     */
    public String getDiseaseName() {
        return diseaseName;
    }

    /**
     * Sets the name of the disease
     * @param diseaseName - name to set as the disease name
     */
    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    /**
     * Gets the date of the disease being diagnosed
     * @return - LocalDate of the diagnosed date
     */
    public LocalDate getDateDiagnosed() {
        return dateDiagnosed;
    }

    /**
     * Checks a diagnosis date is not after the current date or before the patient's birth date.
     * Returns true if invalid, false otherwise
     * @param date date to set as diagnosis date
     * @param patient patient to check for birth date validity
     * @return boolean invalid date
     */
    public boolean isInvalidDiagnosisDate(LocalDate date, Patient patient) {
        return (date.isAfter(LocalDate.now()) || date.isBefore(patient.getBirth()));
    }

    /**
     *  Sets the diagnosed date of the disease. Throws an InvalidObjectException when the date is either in the
     *  future or before the patient was born.
     * @param date - date to set as the diagnosed date
     * @param patient patient to
     * @throws InvalidObjectException invalid date
     */
    public void setDateDiagnosed(LocalDate date, Patient patient) throws InvalidObjectException {
        if (isInvalidDiagnosisDate(date, patient)){
            throw new InvalidObjectException("Invalid date provided");
        }
        dateDiagnosed = date;
    }

//    /**
//     *  Gets the disease carrier
//     * @return - Patient object of the carrier
//     */
//    public Patient getDiseaseCarrier() {
//        return diseaseCarrier;
//    }
//
//    /**
//     *  Sets the carrier of the disease
//     * @param diseaseCarrier - Patient object to set as the disease carrier
//     */
//    public void setDiseaseCarrier(Patient diseaseCarrier) {
//        this.diseaseCarrier = diseaseCarrier;
//    }

    /**
     * Gets the disease state
     * @return - DiseaseState enum
     */
    public DiseaseState getDiseaseState() {
        return diseaseState;
    }

    /**
     *  Sets the disease state of the disease
     * @param diseaseState - DiseaseState enum of the disease state to set
     */
    public void setDiseaseState(DiseaseState diseaseState) {
        this.diseaseState = diseaseState;
    }
}
