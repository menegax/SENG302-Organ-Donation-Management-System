package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.IRequiredOrganDataAccess;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class RequiredOrgansDAO implements IRequiredOrganDataAccess {

    private MySqlFactory mySqlFactory;

    public RequiredOrgansDAO () {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }

    @Override
    public int updateRequiredOrgans(String nhi, GlobalEnums.Organ requiredOrgan, LocalDate date) {
        deleteAllRequiredOrgansByNhi(nhi);
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_REQUIRED_ORGANS_QUERY"));
            statement.setString(1, nhi);
            statement.setString(2, requiredOrgan.getValue());
            statement.setString(3, date.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not update required organ in MYSQL DB", this);
        }
        return 0;
    }

    @Override
    public Map<GlobalEnums.Organ, LocalDate> getRequiredOrganByNhi(String nhi) {
        try (Connection connection1 = mySqlFactory.getConnectionInstance()){
            Map<GlobalEnums.Organ, LocalDate> organs = new HashMap<>();
            PreparedStatement statement = connection1.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_REQUIRED_ORGANS_QUERY"));
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                GlobalEnums.Organ organ = GlobalEnums.Organ.valueOf(resultSet.getString("Organ"));
                LocalDate date = LocalDate.parse(resultSet.getString("Date"));
                organs.put(organ, date);
            }
            return organs;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get required organs from MYSQL DB", this);
        }
        return null;
    }

    @Override
    public void deleteRequiredOrganByNhi(String nhi, GlobalEnums.Organ organ) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_REQUIRED_ORGAN_QUERY"));
            statement.setString(1, nhi);
            statement.setString(2, organ.getValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete required organ from MYSQL DB", this);
        }
    }

    @Override
    public void deleteAllRequiredOrgansByNhi(String nhi) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_ALL_REQUIRED_ORGANS_QUERY"));
            statement.setString(1, nhi);
            statement.executeUpdate();
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete all required organs from MYSQL DB", this);
        }
    }
}
