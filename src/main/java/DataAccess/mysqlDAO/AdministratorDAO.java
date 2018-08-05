package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ResourceManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AdministratorDAO implements IAdministratorDataAccess {

    private MySqlFactory mySqlFactory;

    public AdministratorDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_ADMIN_QUERY"));
            for (Administrator administrator : administrators) {
                preparedStatement = addUpdateParams(administrator, preparedStatement);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAdministrator(Administrator clinician) {
        throw new NotImplementedException();
    }

    @Override
    public boolean deleteAdministrator(Administrator administrator) { return false; }

    @Override
    public Administrator getAdministratorByUsername(String username) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMIN_USERNAME"));
            preparedStatement.setString(1, username);
            return constructAdministratorObject(preparedStatement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Administrator> searchAdministrator(String searchTerm) {
        return null;
    }

    @Override
    public Set<Administrator> getAdministrators() {
        throw new NotImplementedException();
    }


    private PreparedStatement addUpdateParams(Administrator admin, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, admin.getUsername());
        preparedStatement.setString(2, admin.getFirstName());
        preparedStatement.setString(3, admin.getMiddleNames().size()  == 0 ? "" : String.join(" ",admin.getMiddleNames()));
        preparedStatement.setString(4, admin.getLastName());
        preparedStatement.setString(5, admin.getSalt());
        preparedStatement.setString(6, admin.getHashedPassword());
        preparedStatement.setString(7, admin.getModified().toString());
        return preparedStatement;
    }


    private Administrator constructAdministratorObject(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            String username = resultSet.getString("Username");
            String fName = resultSet.getString("FName");
            ArrayList<String> mNames = new ArrayList<>(Arrays.asList(resultSet.getString("MName").split(" ")));
            String lName = resultSet.getString("LName");
            String salt = resultSet.getString("salt");
            String password = resultSet.getString("password");
            Timestamp modified = Timestamp.valueOf(resultSet.getString("Modified"));
            return new Administrator(username, fName, mNames, lName, salt, password, modified);
            // administrator.getModified(); //todo:
        }
        return null;
    }
}
