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
import java.util.*;

public class ClinicianDAO implements IClinicianDataAccess {

    private MySqlFactory mySqlFactory;

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
        try(Connection connection = mySqlFactory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_CLINICIANS"));
            for (int i=1; i<=2; i++) {
                statement.setInt(i, clinician.getStaffID());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
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
    public Map<Integer, List<Clinician>> searchClinicians(String searchTerm) {
        try (Connection connection = mySqlFactory.getConnectionInstance()) {
            Map<Integer, List<Clinician>> resultMap = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                resultMap.put(i, new ArrayList<>());
            }
            connection.setAutoCommit(false);
            PreparedStatement statement;
            if (searchTerm.equals("")) {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIANS"));
            } else {
                statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_CLINICIANS_FUZZY"));
                for (int i = 1; i <= 5; i++) {
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
                    Integer[] scores = new Integer[]{resultSet.getInt("StaffIDMatch"), resultSet.getInt("NameMatch"), resultSet.getInt("FullNameMatch")};
                    int score = Collections.min(Arrays.asList(scores));
                    resultMap.get(score).add(clinician);
                }
            }
            return resultMap;
        } catch (SQLException e) {
            return null;
        }
    }

    private Clinician constructClinicianObject(ResultSet resultSet) throws SQLException {
        int staffID = resultSet.getInt("Staffid");
        String fName = resultSet.getString("FName");
        ArrayList<String> mNames = new ArrayList<>(Arrays.asList(resultSet.getString("MName").split(" ")));
        String lName = resultSet.getString("LName");
        GlobalEnums.Region region = GlobalEnums.Region.getEnumFromString(resultSet.getString("region"));
        return new Clinician(staffID, fName, mNames, lName, region);
    }

    @Override
    public Set<Clinician> getClinicians() {
        throw new NotImplementedException();
    }

    @Override
    public int nextStaffID() {
        throw new NotImplementedException();
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
