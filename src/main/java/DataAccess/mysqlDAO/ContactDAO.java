package DataAccess.mysqlDAO;

import DataAccess.interfaces.IContactDataAccess;
import DataAccess.factories.MySqlFactory;
import model.Patient;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO  implements IContactDataAccess {

    private MySqlFactory mySqlFactory;

    public ContactDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public boolean update(Patient patient) {
        return false;
    }


    @Override
    public List<String> select(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()){
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_CONTACTS"));
            statement.setString(1, nhi);
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();
            List<String> contactInfo = new ArrayList<>();
            while (results.next()) {
                int i = 1;
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
