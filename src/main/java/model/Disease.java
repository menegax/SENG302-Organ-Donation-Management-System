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
     *  Sets the diagnosed date of the disease
     * @param date - date to set as the diagnosed date
     */
    public void setDateDiagnosed(LocalDate date, Donor donor) throws InvalidObjectException {
        if ((date.isAfter(LocalDate.now()) || date.isBefore(donor.getBirth()))){
            throw new InvalidObjectException("Invalid date provided");
        }
        dateDiagnosed = date;
    }

//    /**
//     *  Gets the disease carrier
//     * @return - Donor object of the carrier
//     */
//    public Donor getDiseaseCarrier() {
//        return diseaseCarrier;
//    }
//
//    /**
//     *  Sets the carrier of the disease
//     * @param diseaseCarrier - Donor object to set as the disease carrier
//     */
//    public void setDiseaseCarrier(Donor diseaseCarrier) {
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
