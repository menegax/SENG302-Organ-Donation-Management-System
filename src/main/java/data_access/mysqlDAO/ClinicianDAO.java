package data_access.mysqlDAO;

import data_access.factories.MySqlFactory;
import data_access.interfaces.IClinicianDataAccess;
import data_access.interfaces.ILogDataAccess;
import model.Clinician;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utility.ClinicianActionRecord;
import utility.GlobalEnums;
import utility.ResourceManager;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public class ClinicianDAO implements IClinicianDataAccess {

    private final MySqlFactory mySqlFactory;

    public ClinicianDAO() {
        mySqlFactory = MySqlFactory.getMySqlFactory();
    }


    @Override
    public void saveClinician(Set<Clinician> clinicians) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_CLINICIAN_QUERY"));
            for (Clinician clinician : clinicians) {
                preparedStatement = addUpdateParameters(clinician, preparedStatement);
                preparedStatement.executeUpdate();
                ILogDataAccess<ClinicianActionRecord> clinicianActionRecordILogDataAccess = mySqlFactory.getClinicianLogDataAccess();
                clinicianActionRecordILogDataAccess.saveLogs(clinician.getClinicianActionsList(), String.valueOf(clinician.getStaffID()));
            }
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not save clinician to MYSQL DB", this);
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
            systemLogger.log(Level.SEVERE, "Could not add clinician to MYSQL DB", this);        }
        return false;
    }

    @Override
    public boolean deleteClinician(Clinician clinician) {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_CLINICIANS"));
            for (int i=1; i<=2; i++) {
                statement.setInt(i, clinician.getStaffID());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not delete clinician from MYSQL DB", this);
        }
        return false;
    }

    @Override
    public Clinician getClinicianByStaffId(int id) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIAN_STAFF_ID"));
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Clinician clinician = constructClinicianObject(resultSet);
                clinician.setStreet1(resultSet.getString("Street1"));
                clinician.setStreet2(resultSet.getString("Street2"));
                clinician.setRegion(GlobalEnums.Region.getEnumFromString(resultSet.getString("Region")));
                clinician.setSuburb(resultSet.getString("Suburb"));
                ILogDataAccess<ClinicianActionRecord> iLogDataAccess = mySqlFactory.getClinicianLogDataAccess();
                clinician.setClinicianActionsList(iLogDataAccess.getAllLogsByUserId(String.valueOf(clinician.getStaffID())));
                return clinician;
            }
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get clinician from MYSQL DB", this);
        }
        return null;
    }

    @Override
    public Map<Integer, List<Clinician>> searchClinicians(String searchTerm) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            Map<Integer, List<Clinician>> resultMap = new HashMap<>();
            for (int i = 0; i <= 3; i++) {
                resultMap.put(i, new ArrayList<>());
            }
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIANS"));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIANS_SUBSTRING"));
                for (int i = 1; i <= 4; i++) {
                    statement.setString(i, searchTerm);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Clinician clinician = constructClinicianObject(resultSet);
                //Add clinician to resultMap with appropriate score
                if (searchTerm.equals("")) {
                    resultMap.get(0).add(clinician);
                } else {
                    int score = resultSet.getInt("matchNum");
                    resultMap.get(score).add(clinician);
                }
            }
            return resultMap;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get clinicians from search in MYSQL DB", this);
        }
        return null;
    }

    private Clinician constructClinicianObject(ResultSet resultSet) throws SQLException {
        int staffID = resultSet.getInt("Staffid");
        String fName = resultSet.getString("FName");
        ArrayList<String> mNames;
        if (resultSet.getString("MName").length() != 0) {
            mNames = new ArrayList<>(Arrays.asList(resultSet.getString("MName").split(" ")));
        } else {
            mNames = new ArrayList<>();
        }
        String lName = resultSet.getString("LName");
        GlobalEnums.Region region = GlobalEnums.Region.getEnumFromString(resultSet.getString("region"));
        return new Clinician(staffID, fName, mNames, lName, region);
    }

    @Override
    public Set<Clinician> getClinicians() {
        throw new NotImplementedException();
    }

    /**
     * Returns the next staff ID for a new clinician account according to highest staff ID in the database
     */
    @Override
    public int nextStaffID() {
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_NEXT_STAFF_ID"));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("nextID");
            }
            return 1;
        } catch (SQLException e) {
            systemLogger.log(Level.SEVERE, "Could not get next StaffID from MYSQL DB", this);
        }
        return 1;
    }

    private PreparedStatement addUpdateParameters(Clinician clinician, PreparedStatement statement) throws SQLException {
        statement.setInt(1, clinician.getStaffID());
        statement.setString(2, clinician.getFirstName());
        statement.setString(3, clinician.getMiddleNames().size() == 0 ? "" : String.join(" ", clinician.getMiddleNames()));
        statement.setString(4, clinician.getLastName());
        statement.setString(5, clinician.getStreet1());
        statement.setString(6, clinician.getStreet2());
        statement.setString(7, clinician.getSuburb());
        statement.setString(8, clinician.getRegion() == null ? null : clinician.getRegion().toString());
        statement.setString(9, clinician.getModified().toString());
        return statement;
    }
}
