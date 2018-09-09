package data_access.mysqlDAO;

import static utility.SystemLogger.systemLogger;

import data_access.factories.MySqlFactory;
import data_access.interfaces.IDonationsDataAccess;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

public class DonatingOrgansDAO implements IDonationsDataAccess {

    private data_access.factories.MySqlFactory mySqlFactory;


    public DonatingOrgansDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public int updateDonatingOrgans(String donorNhi, GlobalEnums.Organ donatingOrgan, String receiverNhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_DONATING_ORGANS_QUERY"));
            statement.setString(1, donorNhi);
            statement.setString(2, receiverNhi);
            statement.setString(3, donatingOrgan.toString());
            return statement.executeUpdate();
        }
        catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not update required organ in MYSQL DB", this);
        }
        return 0;
    }


    // todo
    @Override
    public Map<GlobalEnums.Organ, String> getDonatingOrgansByDonorNhi(String donorNhi) {
        //        try (Connection connection1 = mySqlFactory.getConnectionInstance()) {
        //            Map<GlobalEnums.Organ, String> organs = new HashMap<>();
        //            PreparedStatement statement = connection1.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_DONATING_ORGANS_QUERY"));
        //            statement.setString(1, donorNhi);
        //            ResultSet resultSet = statement.executeQuery();
        //            while (resultSet.next()) {
        //                GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(resultSet.getString("Organ"));
        //                String receiverNhi = resultSet.getString("Recipient");
        //                organs.put(organ, receiverNhi);
        //            }
        //            return organs;
        //        }
        //        catch (SQLException e) {
        //            systemLogger.log(Level.SEVERE, "Could not get donating organs from MYSQL DB", this);
        //        }
        return null;
    }


    // todo not used?
    @Override
    public Map<GlobalEnums.Organ, String> getDonatingOrgansByReceiverNhi(String receiverNhi) {
        //        try (Connection connection1 = mySqlFactory.getConnectionInstance()) {
        //            Map<GlobalEnums.Organ, String> organs = new HashMap<>();
        //            PreparedStatement statement = connection1.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_DONATING_ORGANS_QUERY"));
        //            statement.setString(1, receiverNhi);
        //            ResultSet resultSet = statement.executeQuery();
        //            while (resultSet.next()) {
        //                GlobalEnums.Organ organ = GlobalEnums.Organ.getEnumFromString(resultSet.getString("Organ"));
        //                String receiverNhi = resultSet.getString("Recipient");
        //                organs.put(organ, receiverNhi);
        //            }
        //            return organs;
        //        }
        //        catch (SQLException e) {
        //            systemLogger.log(Level.SEVERE, "Could not get donating organs from MYSQL DB", this);
        //        }
        return null;
    }


    @Override
    public void deleteDonatingOrganByNhi(String donorNhi, GlobalEnums.Organ organ) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_DONATING_ORGAN_QUERY"));
            statement.setString(1, donorNhi);
            statement.setString(2, organ.getValue());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete donating organ from MYSQL DB", this);
        }
    }
}
