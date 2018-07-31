package DataAccess;

import model.Patient;
import utility.ResourceManager;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDataAccessDAO extends DataAccessBase implements IContactDataAccess {


    @Override
    public boolean update(Patient patient) {
        return false;
    }

    @Override
    public List<String> select(String nhi) {
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_CONTACTS"));
            statement.setString(1, nhi);
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();
            List<String> contactInfo = new ArrayList<>();
            while (results.next()) {
                int i = 1;
                while(i <= columnCount) {
                    contactInfo.add(results.getString(i++));
                }
            }
            return contactInfo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
