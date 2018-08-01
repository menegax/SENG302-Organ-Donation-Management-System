package DataAccess.mysqlDAO;

import DataAccess.interfaces.IContactDataAccess;
import DataAccess.factories.MySqlFactory;
import model.Patient;
import utility.GlobalEnums;
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
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_CLINICIAN_QUERY"));
            statement.setString(1, patient.getStreet1());
            statement.setString(2, patient.getStreet2());
            statement.setString(3, patient.getSuburb());
            statement.setString(4, patient.getRegion() == null ? null : patient.getRegion().getValue());
            statement.setInt(5, patient.getZip());
            statement.setString(6, patient.getHomePhone());
            statement.setString(7, patient.getWorkPhone());
            statement.setString(8, patient.getMobilePhone());
            statement.setString(9, patient.getEmailAddress());
            statement.setString(10, patient.getContactName());
            statement.setString(11, patient.getContactRelationship());
            statement.setString(12, patient.getContactHomePhone());
            statement.setString(13, patient.getContactWorkPhone());
            statement.setString(14, patient.getContactMobilePhone());
            statement.setString(15, patient.getContactEmailAddress());
        } catch (SQLException e) {
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
    public boolean delete() {
        return false;
    }
}
