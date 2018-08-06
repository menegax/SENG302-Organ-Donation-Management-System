package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.ITransplantWaitListDataAccess;
import service.OrganWaitlist;
import utility.GlobalEnums.*;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransplantWaitingListDAO implements ITransplantWaitListDataAccess {

    private MySqlFactory daoFactory = MySqlFactory.getMySqlFactory();

    @Override
    public OrganWaitlist getWaitingList() {
        try (Connection connection = daoFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ALL_TRANSPLANT_WAIT"));
            ResultSet results =  preparedStatement.executeQuery();
            OrganWaitlist organRequests = new OrganWaitlist();
            while (results.next()) {
                organRequests.add(results.getString("FName"),
                        Organ.getEnumFromString(results.getString("Organ")),
                        LocalDate.parse(results.getString("RequestDate")),
                        Region.getEnumFromString(results.getString("Region")),
                        results.getString("nhi"));
            }
            return organRequests;
        } catch (SQLException e) {
            e.printStackTrace();
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
            for (OrganWaitlist.OrganRequest organRequest : organRequests) {
                preparedStatement.setString(1, organRequest.getReceiverNhi());
                preparedStatement.setString(2, organRequest.getRequestDate().toString());
                preparedStatement.setString(3, organRequest.getRequestedOrgan().toString());
                preparedStatement.setString(4, organRequest.getRequestRegion().getValue());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }

    @Override
    public void deleteWaitingList() {
        try(Connection connection = daoFactory.getConnectionInstance()){
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ALL_TRANSPLANT_WAIT"));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
