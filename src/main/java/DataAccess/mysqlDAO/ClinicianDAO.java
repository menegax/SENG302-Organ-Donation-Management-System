package DataAccess.mysqlDAO;

import DataAccess.factories.MySqlFactory;
import DataAccess.interfaces.IClinicianDataAccess;
import DataAccess.interfaces.ILogDataAccess;
import model.Clinician;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ClinicianActionRecord;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ClinicianDAO implements IClinicianDataAccess {

    private MySqlFactory mySqlFactory;

    public ClinicianDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public void saveClinician(Set<Clinician> clinicians) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement  = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_CLINICIAN_QUERY"));
            for (Clinician clinician : clinicians) {
                preparedStatement = addUpdateParameters(clinician, preparedStatement);
                preparedStatement.executeUpdate();
                ILogDataAccess<ClinicianActionRecord> clinicianActionRecordILogDataAccess = mySqlFactory.getClinicianLogDataAccess();
                clinicianActionRecordILogDataAccess.saveLogs(clinician.getClinicianActionsList(), String.valueOf(clinician.getStaffID()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addClinician(Clinician clinician) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_CLINICIAN_QUERY"));
            statement = addUpdateParameters(clinician, statement);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteClinician(Clinician clinician) {
        return false;
    }

    @Override
    public Clinician getClinicianByStaffId(int id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIAN_STAFF_ID"));
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ArrayList<String> middleNames = new ArrayList<>(Arrays.asList(resultSet.getString("MName"), " "));
                Clinician clinician = new Clinician(resultSet.getInt("StaffID"), resultSet.getString("FName"),
                        middleNames, resultSet.getString("LName"), resultSet.getString("Region") != null ?
                        GlobalEnums.Region.getEnumFromString(resultSet.getString("Region")) : null);
                clinician.setStreet1(resultSet.getString("Street1"));
                clinician.setStreet2(resultSet.getString("Street2"));
                clinician.setRegion(GlobalEnums.Region.getEnumFromString(resultSet.getString("Region")));
                clinician.setSuburb(resultSet.getString("Suburb"));
                ILogDataAccess<ClinicianActionRecord> iLogDataAccess = mySqlFactory.getClinicianLogDataAccess();
                clinician.setClinicianActionsList(iLogDataAccess.getAllLogsByUserId(String.valueOf(clinician.getStaffID())));
                return clinician;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Clinician> searchClinician(String searchTerm) {
        return null;
    }

    @Override
    public Set<Clinician> getClinicians() {
        throw new NotImplementedException();
    }

    @Override
    public int nextStaffID() {throw new NotImplementedException();}

    private PreparedStatement addUpdateParameters(Clinician clinician, PreparedStatement statement) throws SQLException {
        statement.setInt(1, clinician.getStaffID());
        statement.setString(2, clinician.getFirstName());
        statement.setString(3, clinician.getMiddleNames().size() == 0 ? "" : String.join(" ", clinician.getMiddleNames()));
        statement.setString(4, clinician.getLastName());
        statement.setString(5, clinician.getStreet1());
        statement.setString(6, clinician.getStreet2());
        statement.setString(7, clinician.getSuburb());
        statement.setString(8, clinician.getRegion() == null ? null : clinician.getRegion().toString());
        statement.setString(9,clinician.getModified().toString());
        return statement;
    }
}
