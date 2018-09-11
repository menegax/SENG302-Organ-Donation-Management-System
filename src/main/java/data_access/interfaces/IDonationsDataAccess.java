package data_access.interfaces;

import utility.GlobalEnums;

import java.util.Map;

public interface IDonationsDataAccess {

    //todo doc dat

    int updateDonatingOrgans(String donorNhi, GlobalEnums.Organ donatingOrgan, String receiverNhi);

    Map<GlobalEnums.Organ, String> getDonatingOrgansByDonorNhi(String donorNhi);

    Map<GlobalEnums.Organ, String> getDonatingOrgansByReceiverNhi(String receiverNhi);

    void deleteDonatingOrganByNhi(String donorNhi, GlobalEnums.Organ organ);

    void deleteAllDonatingOrganByNhi(String donorNhi);
}



