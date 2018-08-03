package DataAccess.mysqlDAO;

import DataAccess.interfaces.IContactDataAccess;
import DataAccess.factories.MySqlFactory;
import model.Patient;
import utility.ResourceManager;
import utility.SystemLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ContactDAO  implements IContactDataAccess {

    private MySqlFactory mySqlFactory;

    public ContactDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public boolean updateContact(Patient patient) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_CONTACT_QUERY"));
            statement.setString(1,patient.getNhiNumber());
            statement.setString(2, patient.getStreet1());
            statement.setString(3, patient.getStreet2());
            statement.setString(4, patient.getSuburb());
            statement.setString(5, patient.getRegion() == null ? null : patient.getRegion().getValue());
            statement.setInt(6, patient.getZip());
            statement.setString(7, patient.getHomePhone());
            statement.setString(8, patient.getWorkPhone());
            statement.setString(9, patient.getMobilePhone());
            statement.setString(10, patient.getEmailAddress());
            statement.setString(11, patient.getContactName());
            statement.setString(12, patient.getContactRelationship());
            statement.setString(13, patient.getContactHomePhone());
            statement.setString(14, patient.getContactWorkPhone());
            statement.setString(15, patient.getContactMobilePhone());
            statement.setString(16, patient.getContactEmailAddress());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            SystemLogger.systemLogger.log(Level.SEVERE, "Failed to update contact", this);
            return false;
        }
        return true;
    }


    @Override
    public List<String> getContactByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_CONTACTS"));
            statement.setString(1, nhi);
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();
            List<String> contactInfo = new ArrayList<>();
            while (results.next()) {
                int i = 2; //start at 2 to skip nhi
                while (i <= columnCount) {
                    contactInfo.add(results.getString(i++));
                }
            }
            return contactInfo;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void deleteContactByNhi(String nhi)
    {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_CONTACT_BY_NHI"));
            statement.setString(1, nhi);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
