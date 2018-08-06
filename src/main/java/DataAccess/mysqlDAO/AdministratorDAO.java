package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.IAdministratorDataAccess;
import model.Administrator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ResourceManager;

import java.sql.*;
import java.util.*;

public class AdministratorDAO implements IAdministratorDataAccess {

    private MySqlFactory mySqlFactory;

    public AdministratorDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public void saveAdministrator(Set<Administrator> administrators) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
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
    public boolean deleteAdministrator(Administrator administrator) {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_ADMINISTRATORS"));
            for (int i=1; i<=2; i++) {
                statement.setString(i, administrator.getUsername());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

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
    public Map<Integer, List<Administrator>> searchAdministrators(String searchTerm) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            Map<Integer, List<Administrator>> resultMap = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                resultMap.put(i, new ArrayList<>());
            }
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMINISTRATORS"));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_ADMINISTRATORS_FUZZY"));
                for (int i = 1; i <= 5; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Administrator administrator = constructAdminObject(resultSet);
                //Add clinician to resultMap with appropriate score
                if (searchTerm.equals("")) {
                    resultMap.get(0).add(administrator);
                } else {
                    Integer[] scores = new Integer[]{resultSet.getInt("UsernameMatch"), resultSet.getInt("NameMatch"), resultSet.getInt("FullNameMatch")};
                    int score = Collections.min(Arrays.asList(scores));
                    resultMap.get(score).add(administrator);
                }
            }
            return resultMap;
        } catch (SQLException e) {
            return null;
        }
    }

    private Administrator constructAdminObject(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("Username");
        String fName = resultSet.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(resultSet.getString("FName").split(" ")));
        String lName = resultSet.getString("LName");
        String salt = resultSet.getString("Salt");
        String password = resultSet.getString("Password");
        Timestamp modified = Timestamp.valueOf(resultSet.getString("Modified"));
        return new Administrator(username, fName, mNames, lName, salt, password, modified);
    }

    @Override
    public Set<Administrator> getAdministrators() {
        throw new NotImplementedException();
    }


    private PreparedStatement addUpdateParams(Administrator admin, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, admin.getUsername());
        preparedStatement.setString(2, admin.getFirstName());
        preparedStatement.setString(3, admin.getMiddleNames().size() == 0 ? "" : String.join(" ", admin.getMiddleNames()));
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
        }
        return null;
    }
}
