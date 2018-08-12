package data_access.mysqlDAO;

import data_access.interfaces.IContactDataAccess;
import data_access.factories.MySqlFactory;
import model.Patient;
import utility.ImportObservable;
import utility.ResourceManager;
import utility.SystemLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class ContactDAO  implements IContactDataAccess {

    private MySqlFactory mySqlFactory;

    public ContactDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public boolean updateContact(Patient patient) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_CONTACT_QUERY"));
            statement = addUpdateParameters(statement, patient);
            statement.executeUpdate();
        } catch (SQLException e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Failed to update contact", this);
            return false;
        }
        return true;
    }

    @Override
    public void addContactBatch(List<Patient> patients){
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            connection.setAutoCommit(false);
            int extendedQueryCount = 0;
            StringBuilder statements = new StringBuilder(ResourceManager.getStringForQuery("BARE_INSERT_CONTACT_BATCH"));
            PreparedStatement preparedStatement;
            String extendedInsert;
            for (Patient aPatient : patients) {
                statements.append(getNextRecordString(aPatient));
                if (extendedQueryCount == 4000 ) {
                    extendedInsert = statements.toString().substring(0, statements.toString().length() -1)
                            + " " +ResourceManager.getStringForQuery("ON_DUPLICATE_CONTACT_UPDATE");
                    preparedStatement = connection.prepareStatement(extendedInsert);
                    preparedStatement.execute();
                    extendedQueryCount = 0;
                    ImportObservable importObservable = ImportObservable.getInstance();
                    importObservable.setCompleted(importObservable.getCompleted() + 2000);
                }
                extendedQueryCount++;
            }
            if (extendedQueryCount != 0) {
                extendedInsert = statements.toString().substring(0, statements.toString().length() -1)
                        + " " +ResourceManager.getStringForQuery("ON_DUPLICATE_CONTACT_UPDATE");
                connection.prepareStatement(extendedInsert).execute();
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            systemLogger.log(Level.SEVERE, "Could not get contact from MYSQL DB", this);
        }
        return null;
    }

    @Override
    public void deleteContactByNhi(String nhi)
    {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_CONTACT_BY_NHI"));
            statement.setString(1, nhi);
            statement.execute();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete contact from MYSQL DB", this);
        }
    }

    private String getNextRecordString(Patient patient) {
        return String.format(ResourceManager.getStringForQuery("CONTACT_INSERT_ANOTHER"),
                getFormattedString(patient.getNhiNumber()),
                getFormattedString(patient.getStreet1()),
                getFormattedString(patient.getStreet2()),
                getFormattedString(patient.getSuburb()),
                patient.getRegion() == null ? null : String.format("\'%s\'",patient.getRegion().getValue().replaceAll("'", "")),
                getFormattedString(String.valueOf(patient.getZip())),
                getFormattedString(patient.getHomePhone()),
                getFormattedString(patient.getWorkPhone()),
                getFormattedString(patient.getMobilePhone()),
                getFormattedString(patient.getEmailAddress()),
                getFormattedString(patient.getContactEmailAddress()),
                getFormattedString(patient.getContactRelationship()),
                getFormattedString(patient.getContactHomePhone()),
                getFormattedString(patient.getContactWorkPhone()),
                getFormattedString(patient.getContactMobilePhone()),
                getFormattedString(patient.getContactEmailAddress()));
    }


    private String getFormattedString(String str) {
        return str == null ? null : String.format("\'%s\'", str.replaceAll("'", "''" ));
    }

    private PreparedStatement addUpdateParameters(PreparedStatement statement, Patient patient) throws SQLException{
        statement.setString(1,patient.getNhiNumber());
        statement.setString(2, patient.getStreet1());
        statement.setString(3, patient.getStreet2());
        statement.setString(4, patient.getSuburb());
        statement.setString(5, patient.getRegion() == null ? null :
                patient.getRegion().getValue());
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
        return statement;
    }
}
