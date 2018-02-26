package service;

public class DonorNotFoundException extends Exception {

    public DonorNotFoundException(){

    }

    public String toString(){
        return "Invalid donor ID. Donor not found";
    }
}
