package model;

import utility.GlobalEnums.Organ;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * Represents a procedure performed or to be performed on a patient
 */
public class Procedure implements Serializable {

    private String summary;
    private String description;
    private LocalDate date;
    private Set<Organ> affectedDonations;

    /**
     * Constructor for a procedure
     * @param summary the short summary of the procedure
     * @param description the longer description of the procedure
     * @param date the date the procedure was/is to be performed
     * @param affectedDonations the organ donations affected by this procedure
     */
    public Procedure(String summary, String description, LocalDate date, Set<Organ> affectedDonations) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.affectedDonations = affectedDonations;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<Organ> getAffectedDonations() {
        return affectedDonations;
    }

    public void setAffectedDonations(Set<Organ> affectedDonations) {
        this.affectedDonations = affectedDonations;
    }
}
