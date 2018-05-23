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
     * @param birthDate birth date of patient
     * @return boolean invalid date
     */
    public boolean isInvalidDiagnosisDate(LocalDate date, LocalDate birthDate) {
        return (date.isAfter(LocalDate.now()) || date.isBefore(birthDate));
    }

    /**
     *  Sets the diagnosed date of the disease. Throws an InvalidObjectException when the date is either in the
     *  future or before the patient was born.
     * @param date - date to set as the diagnosed date
     * @param birthDate patient birth date
     * @throws InvalidObjectException invalid date
     */
    public void setDateDiagnosed(LocalDate date, LocalDate birthDate) throws InvalidObjectException {
        if (isInvalidDiagnosisDate(date, birthDate)){
            throw new InvalidObjectException("Invalid date provided");
        }
        dateDiagnosed = date;
    }

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

    /**
     * Checks if an object is equal to this disease
     * @param o Object to compare
     * @return boolean is equal
     */
    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof Disease) {
            Disease d = (Disease) o;
            if(d.getDiseaseName().equals(this.diseaseName)) {
                if(d.getDateDiagnosed().equals(this.dateDiagnosed)) {
                    return true;
                }
            }
        }
        return false;
    }
}
