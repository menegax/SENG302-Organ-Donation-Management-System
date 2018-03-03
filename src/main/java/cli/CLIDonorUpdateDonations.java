package cli;
import model.Donor;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import service.Database;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import java.io.InvalidObjectException;
import java.util.ArrayList;

@SuppressWarnings({"unused"})
@Command(name = "donations", description = "used to update the donations on a particular donor")
public class CLIDonorUpdateDonations implements Runnable {

    @Option(names = {"-i", "--ird"}, required = true, description = "Search donor by the IRD number of the donor.")
    private int searchIrd;

    @Option(names = {"-l", "--list"}, description = "Lists current organ donations.")
    private boolean donationsRequested;

    @Option(names = "--add", split = ",", description = "Takes a list of organs to add to donations")
    private ArrayList<String> newDonations; //TODO: need to be string arrays unless we want to use custom type conversion, i'll discuss with you in the morning

    @Option(names = {"-rm", "--remove"}, split = ",", description = "Takes a list of organs to remove from donations")
    private ArrayList<String> rmDonations;

    private void displayInformationMessages(ArrayList<String> messages){
        if (messages.size() == 0){
            System.out.println("*** Successfully updated donations ***");
        } else
            for (String message: messages) System.out.println(message);

    }

    //TODO: still working on it... very very sleepy :(
    private ArrayList<String> updateDonations(Donor d){
        ArrayList<String> informationMessages = new ArrayList<>();
        GlobalEnums.Organ organEnum;
        if (newDonations != null){
            ArrayList<GlobalEnums.Organ> updateDonations = (d.getDonations() == null? new ArrayList<>(): d.getDonations());
            for (String organ: newDonations) {
                organEnum = (GlobalEnums.Organ) GlobalEnums.Organ.getEnumFromString(organ); //null if invalid
                if (organEnum == null) informationMessages.add("Error: Invalid organ " + organ + ".");
                else {
                    if (updateDonations.size() == 0) updateDonations.add(organEnum);
                    else {
                        if (updateDonations.contains(organEnum)) {
                            informationMessages.add("Organ " + organ + " is already part of the donors donations, so was not added.");
                        } else updateDonations.add(organEnum);
                    }
                }
            }
            d.setDonations(updateDonations);
        }
        if (rmDonations != null) { //if --add and --rm and used,then --rm will remove from NEW/OLD list depending on order
            ArrayList<Organ> updateDonations = d.getDonations() == null ? new ArrayList<>(): d.getDonations();
            for (String organ: rmDonations) {
                organEnum = (GlobalEnums.Organ) GlobalEnums.Organ.getEnumFromString(organ);
                if (organEnum == null)
                    informationMessages.add("Error: Invalid organ " + organ + ".");
                else {
                    if (updateDonations.contains(organEnum)) updateDonations.remove(organEnum);
                    else informationMessages.add("Organ " + organ+ " is not part of the donors donations, so could not be removed.");
                }
            }
            d.setDonations(updateDonations);
        }
        return informationMessages;
    }


    public void run() {
        try {
            Donor donor = Database.getDonorByIrd(searchIrd);
            if (donationsRequested) System.out.println("Donations: " + donor.getDonations());
            else displayInformationMessages(updateDonations(donor));
        } catch (InvalidObjectException e){
            System.out.println(e.getMessage());
        }
    }

}
