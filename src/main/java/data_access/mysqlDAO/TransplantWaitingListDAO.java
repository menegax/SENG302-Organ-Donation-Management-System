package data_access.mysqlDAO;

import data_access.factories.MySqlFactory;
import data_access.interfaces.ITransplantWaitListDataAccess;
import service.OrganWaitlist;
import utility.GlobalEnums.*;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class TransplantWaitingListDAO implements ITransplantWaitListDataAccess {

    private MySqlFactory daoFactory = MySqlFactory.getMySqlFactory();

    @Override
    public OrganWaitlist getWaitingList() {
        try (Connection connection = daoFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ALL_TRANSPLANT_WAIT"));
            ResultSet results =  preparedStatement.executeQuery();
            OrganWaitlist organRequests = new OrganWaitlist();
            while (results.next()) {
                Region region = results.getString("Region") == null ? null : Region.getEnumFromString(results.getString("Region"));
                organRequests.add(results.getString("FName"),
                        Organ.getEnumFromString(results.getString("Organ")),
                        LocalDate.parse(results.getString("RequestDate")),
                        region,
                        results.getString("nhi"), 
                        results.getString("Address"));
            }
            return organRequests;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get transplant waiting list from MYSQL DB", this);
        }
        return null;
    }


    /**
     * Deletes all in db then updates from the organ list from client
     */
    @Override
    public void updateWaitingList(OrganWaitlist organRequests) {
        try(Connection connection = daoFactory.getConnectionInstance()){
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_TRANSPLANT_WAIT"));
            if (organRequests != null) {
                deleteWaitingList();
                for (OrganWaitlist.OrganRequest organRequest : organRequests) {
                    preparedStatement.setString(1, organRequest.getReceiverNhi());
                    preparedStatement.setString(2, organRequest.getRequestDate().toString());
                    preparedStatement.setString(3, organRequest.getRequestedOrgan().toString());
                    preparedStatement.setString(4, organRequest.getRequestRegion() != null ? organRequest.getRequestRegion().getValue(): null);
                    preparedStatement.setString(5, organRequest.getAddress());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            systemLogger.log(Level.SEVERE, "Could not update wait list in MYSQL DB", this);
        }
    }

    @Override
    public void deleteWaitingList() {
        try(Connection connection = daoFactory.getConnectionInstance()){
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ALL_TRANSPLANT_WAIT"));
            preparedStatement.execute();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete waiting list from MYSQL DB", this);
        }
    }

    @Override
    public void deleteRequestsByNhi(String nhi) {
        try(Connection connection = daoFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ORGAN_REQUEST_BY_NHI"));
            preparedStatement.setString(1, nhi);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            systemLogger.log(Level.SEVERE, "Could not delete requests by nhi", this);
        }
    }

}
