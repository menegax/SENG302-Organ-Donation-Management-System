package model;

import java.io.Serializable;
import java.time.LocalDate;

public class OrganReceival implements Serializable {

    private LocalDate registeredOn;

    private String donorNhi;


    public OrganReceival(LocalDate registeredOn, String donorNhi) {
        this.registeredOn = registeredOn;
        this.donorNhi = donorNhi;
    }


    public OrganReceival(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }


    public LocalDate getRegisteredOn() {
        return registeredOn;
    }


    public String getDonorNhi() {
        return donorNhi;
    }


    public void setDonorNhi(String donorNhi) {
        this.donorNhi = donorNhi;
    }

    public String toString() {
        return "Organ receival: \n" + "Donor NHI: " + donorNhi + "\n" + "Registered date: " + registeredOn + "\n";
    }
}
